package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.skminibank.R;
import com.bank.skminibank.adapters.TransactionChatAdapter;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.database.AppDatabase;
import com.bank.skminibank.database.ChatMessageEntity;
import com.bank.skminibank.model.AccountResponse;
import com.bank.skminibank.model.Transaction;
import com.bank.skminibank.model.TransactionChatMessage;
import com.bank.skminibank.model.TransactionResponse;
import com.bank.skminibank.utils.SessionManager;
import com.bank.skminibank.utils.PaymentVoiceUtil;
import com.bank.skminibank.utils.RecentRecipientStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatTransferActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private final List<TransactionChatMessage> messageList = new ArrayList<>();
    private TransactionChatAdapter adapter;
    private String contactName, contactMobile;
    private String resolvedReceiverIdentifier = "";
    private String resolvedReceiverType = "upi";
    private boolean receiverResolved = false;
    private double pendingAmount = 0;
    private final Handler pollingHandler = new Handler();
    private final Set<String> displayedTxnIds = new HashSet<>();
    private SessionManager sessionManager;
    private AppDatabase db;

    private View layoutChatInput, layoutPaymentOverlay;
    private TextView tvBankingName, tvSelectedAmount;
    private ImageView btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_transfer);

        sessionManager = new SessionManager(this);
        db = AppDatabase.getInstance(this);

        contactName = getIntent().getStringExtra("name");
        contactMobile = getIntent().getStringExtra("mobile");
        
        // Normalize mobile for consistent DB matching (Last 10 digits)
        if (contactMobile != null) {
            String digits = contactMobile.replaceAll("[^0-9]", "");
            if (digits.length() >= 10) {
                contactMobile = digits.substring(digits.length() - 10);
            } else {
                contactMobile = digits;
            }
        }
        
        String contactInitial = getIntent().getStringExtra("initial");
        String contactColor = getIntent().getStringExtra("color");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ((TextView) findViewById(R.id.tvContactName)).setText(contactName);
        ((TextView) findViewById(R.id.tvContactMobile)).setText(contactMobile);
        ((TextView) findViewById(R.id.tvContactInitial)).setText(contactInitial);

        View p = (View) findViewById(R.id.tvContactInitial).getParent();
        if (p instanceof androidx.cardview.widget.CardView) {
            ((androidx.cardview.widget.CardView) p).setCardBackgroundColor(android.graphics.Color.parseColor(contactColor));
        }

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        layoutChatInput = findViewById(R.id.layoutChatInput);
        layoutPaymentOverlay = findViewById(R.id.layoutPaymentOverlay);
        tvBankingName = findViewById(R.id.tvBankingName);
        tvSelectedAmount = findViewById(R.id.tvSelectedAmount);
        btnSend = findViewById(R.id.btnSend);

        tvBankingName.setText("Banking name: " + contactName.toUpperCase());

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionChatAdapter(messageList);
        rvChat.setAdapter(adapter);

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String val = s.toString().trim();
                if (isNumeric(val)) {
                    showPaymentOverlay(val);
                } else {
                    hidePaymentOverlay();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSend.setOnClickListener(v -> sendMessage());
        findViewById(R.id.btnPayAction).setOnClickListener(v -> handlePaymentAction());

        findViewById(R.id.btnHi).setOnClickListener(v -> sendQuickMessage("Hi"));
        findViewById(R.id.btnHand).setOnClickListener(v -> sendQuickMessage("👋"));
        findViewById(R.id.btnSend1).setOnClickListener(v -> initiatePayment(1.0));

        resolveContactRecipient();
        loadLocalChatHistory(); // This will now start polling after loading
    }

    private void loadLocalChatHistory() {
        String ownerAcc = sessionManager.getAccountNumber();
        if (ownerAcc == null) ownerAcc = "UNKNOWN";
        
        final String finalOwnerAcc = ownerAcc;
        new Thread(() -> {
            List<ChatMessageEntity> localMessages = db.chatDao().getChatHistory(finalOwnerAcc, contactMobile);
            runOnUiThread(() -> {
                messageList.clear();
                displayedTxnIds.clear();
                for (ChatMessageEntity entity : localMessages) {
                    // Record ID to prevent duplicates from API
                    if (entity.getTransactionId() != null && !entity.getTransactionId().startsWith("LOCAL_")) {
                        displayedTxnIds.add(entity.getTransactionId());
                    }
                    
                    TransactionChatMessage msg;
                    if (entity.getType() == TransactionChatMessage.TYPE_TEXT) {
                        msg = new TransactionChatMessage(entity.getContent(), entity.isSentByMe(), TransactionChatMessage.TYPE_TEXT);
                    } else {
                        msg = new TransactionChatMessage(entity.getAmount(), entity.getStatus(), entity.isSentByMe(), entity.getTransactionId(), entity.getTimestamp());
                        msg.setOtherPartyName(contactName);
                    }
                    messageList.add(msg);
                }
                adapter.notifyDataSetChanged();
                if (!messageList.isEmpty()) {
                    rvChat.scrollToPosition(messageList.size() - 1);
                }
                
                // START POLLING ONLY AFTER HISTORY IS LOADED
                startChatPolling();
            });
        }).start();
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showPaymentOverlay(String amount) {
        tvSelectedAmount.setText(amount);
        layoutPaymentOverlay.setVisibility(View.VISIBLE);
        layoutChatInput.setAlpha(0.5f);
        pendingAmount = Double.parseDouble(amount);
    }

    private void hidePaymentOverlay() {
        layoutPaymentOverlay.setVisibility(View.GONE);
        layoutChatInput.setAlpha(1.0f);
    }

    private void resolveContactRecipient() {
        if (contactMobile == null || contactMobile.isEmpty()) {
            return;
        }

        ApiClient.getService().searchByMobile(contactMobile).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccountResponse account = response.body();
                    if ("success".equalsIgnoreCase(account.getStatus())) {
                        receiverResolved = true;
                        if (account.getUpiId() != null && !account.getUpiId().isEmpty() && !"null".equalsIgnoreCase(account.getUpiId())) {
                            resolvedReceiverType = "upi";
                            resolvedReceiverIdentifier = account.getUpiId();
                        } else if (account.getAccountNumber() != null && !account.getAccountNumber().isEmpty() && !"null".equalsIgnoreCase(account.getAccountNumber())) {
                            resolvedReceiverType = "transfer";
                            resolvedReceiverIdentifier = account.getAccountNumber();
                        } else {
                            receiverResolved = false;
                            resolvedReceiverIdentifier = "";
                        }

                        if (account.getCustomerName() != null && !account.getCustomerName().isEmpty()) {
                            contactName = account.getCustomerName();
                            tvBankingName.setText("Banking name: " + contactName.toUpperCase(Locale.getDefault()));
                        }
                    } else {
                        receiverResolved = false;
                        resolvedReceiverIdentifier = "";
                        resolvedReceiverType = "upi";
                        Toast.makeText(ChatTransferActivity.this, account.getMessage() != null ? account.getMessage() : "Recipient not registered", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    receiverResolved = false;
                    resolvedReceiverIdentifier = "";
                    resolvedReceiverType = "upi";
                    Toast.makeText(ChatTransferActivity.this, "Unable to verify recipient right now", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                receiverResolved = false;
                resolvedReceiverIdentifier = "";
                resolvedReceiverType = "upi";
                Toast.makeText(ChatTransferActivity.this, "Recipient verification failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePaymentAction() {
        if (pendingAmount <= 0) {
            Toast.makeText(this, "Enter an amount first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sessionManager.isBiometricEnabled()) {
            showBiometricPrompt();
        } else {
            initiatePayment(pendingAmount);
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String storedPin = sessionManager.getUpiPin();
                executeActualPayment(storedPin != null ? storedPin : "VERIFIED");
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirm Payment")
                .setSubtitle("Transfer ₹" + pendingAmount + " to " + contactName)
                .setNegativeButtonText("Use PIN Instead")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void executeActualPayment(String pin) {
        if (!receiverResolved || resolvedReceiverIdentifier.isEmpty()) {
            Toast.makeText(this, "Recipient could not be verified. Please use a registered account or UPI ID.", Toast.LENGTH_LONG).show();
            return;
        }

        // 1. Add Processing Bubble Immediately (Like PhonePe)
        TransactionChatMessage processingMsg = new TransactionChatMessage(pendingAmount, "PROCESSING", true);
        processingMsg.setOtherPartyName(contactName); // Set for Paytm look
        messageList.add(processingMsg);
        adapter.notifyItemInserted(messageList.size() - 1);
        rvChat.scrollToPosition(messageList.size() - 1);

        String senderMobile = sessionManager.getMobile();
        String senderAccount = sessionManager.getAccountNumber();
        String senderName = sessionManager.getCustomerName();
        String paymentRemarks = "Payment from mobile " + safe(senderMobile)
                + " account " + safe(senderAccount)
                + " sender " + safe(senderName)
                + " to mobile " + safe(contactMobile)
                + " receiver " + safe(contactName);

        // 2. Perform API Call in Background after 1 second
        new Handler().postDelayed(() -> {
            if ("upi".equalsIgnoreCase(resolvedReceiverType)) {
                ApiClient.getService().performUpiPayment(sessionManager.getAccountNumber(), resolvedReceiverIdentifier, pin, pendingAmount, paymentRemarks)
                        .enqueue(new PaymentCallback(processingMsg));
            } else {
                ApiClient.getService().transferAmount(sessionManager.getAccountNumber(), resolvedReceiverIdentifier, pendingAmount, paymentRemarks, pin)
                        .enqueue(new PaymentCallback(processingMsg));
            }
        }, 1000); // 1 Second delay as requested

        etMessage.setText("");
        hidePaymentOverlay();
    }

    private void handlePaymentResponse(Response<com.bank.skminibank.model.LoginResponse> response, TransactionChatMessage msg) {
        if (response.isSuccessful() && response.body() != null && "success".equalsIgnoreCase(response.body().getStatus())) {
            msg.setStatus("PAID");
            RecentRecipientStore.save(this, contactName, contactMobile);
            String localTxnId = "LOCAL_PAYMENT_" + System.currentTimeMillis();
            String ownerAcc = sessionManager.getAccountNumber();
            saveMessageLocally(new ChatMessageEntity(
                    ownerAcc, localTxnId, contactMobile,
                    "Payment to " + contactName, msg.getAmount(), true,
                    TransactionChatMessage.TYPE_PAYMENT, msg.getTimestamp(), "SUCCESS"));
            
            // ALSO Save to Transactions table for Passbook visibility
            String cleanOwnerAcc = ownerAcc.replaceAll("\\s+", "");
            new Thread(() -> {
                db.transactionDao().insertTransaction(new com.bank.skminibank.database.TransactionEntity(
                        cleanOwnerAcc, localTxnId, "DEBIT", msg.getAmount(),
                        "Sent to " + contactName + " (" + contactMobile + ")", 
                        msg.getTimestamp(), -1
                ));
            }).start();

            PaymentVoiceUtil.speakPayment(this, msg.getAmount(), false);
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
        } else {
            msg.setStatus("FAILED");
            String error = (response.body() != null) ? response.body().getMessage() : "Failed";
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
        rvChat.scrollToPosition(messageList.size() - 1);
    }

    private class PaymentCallback implements Callback<com.bank.skminibank.model.LoginResponse> {
        private final TransactionChatMessage message;

        PaymentCallback(TransactionChatMessage message) {
            this.message = message;
        }

        @Override
        public void onResponse(@NonNull Call<com.bank.skminibank.model.LoginResponse> call,
                               @NonNull Response<com.bank.skminibank.model.LoginResponse> response) {
            handlePaymentResponse(response, message);
        }

        @Override
        public void onFailure(@NonNull Call<com.bank.skminibank.model.LoginResponse> call, @NonNull Throwable t) {
            message.setStatus("FAILED");
            adapter.notifyDataSetChanged();
            Toast.makeText(ChatTransferActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void startChatPolling() {
        pollingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchTransactions();
                pollingHandler.postDelayed(this, 1000); // Poll every 1 second
            }
        }, 0);
    }

    private void fetchTransactions() {
        ApiClient.getService().getTransactions(sessionManager.getAccountNumber(), sessionManager.getCustomerId()).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> txns = response.body().getTransactions();
                    if (txns != null) {
                        updateChatList(txns);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable t) {}
        });
    }

    private void updateChatList(List<Transaction> txns) {
        String ownerAcc = sessionManager.getAccountNumber();
        for (int i = txns.size() - 1; i >= 0; i--) {
            Transaction txn = txns.get(i);
            String transactionId = getStableTransactionId(txn);
            if (displayedTxnIds.contains(transactionId)) continue;

            String desc = txn.getDescription() != null ? txn.getDescription().toLowerCase(Locale.getDefault()) : "";
            String targetMobileClean = contactMobile != null ? contactMobile.replaceAll("[^0-9]", "") : "";
            if (targetMobileClean.length() > 10) targetMobileClean = targetMobileClean.substring(targetMobileClean.length() - 10);
            
            String targetNameLower = contactName != null ? contactName.toLowerCase(Locale.getDefault()) : "";
            String cleanIdentifierLower = resolvedReceiverIdentifier != null ? resolvedReceiverIdentifier.toLowerCase(Locale.getDefault()) : "";

            String txnType = txn.getType() != null ? txn.getType().toUpperCase(Locale.getDefault()) : "";
            boolean isSentByMe = txnType.contains("DEBIT") || txnType.contains("PAYMENT");
            if (txnType.contains("CREDIT")) {
                isSentByMe = false;
            }

            boolean hasStructuredCounterparty = isSentByMe
                    ? hasValue(txn.getToMobile()) || hasValue(txn.getToAccount()) || hasValue(txn.getReceiverName())
                    : hasValue(txn.getFromMobile()) || hasValue(txn.getFromAccount()) || hasValue(txn.getSenderName());

            boolean isRelevant;
            if (isSentByMe) {
                isRelevant = matchesContact(txn.getToMobile(), txn.getToAccount(), txn.getReceiverName(),
                        targetMobileClean, cleanIdentifierLower, targetNameLower);
            } else {
                isRelevant = matchesContact(txn.getFromMobile(), txn.getFromAccount(), txn.getSenderName(),
                        targetMobileClean, cleanIdentifierLower, targetNameLower);
            }
            if (!hasStructuredCounterparty) {
                isRelevant = containsContactValue(desc, targetMobileClean)
                        || containsContactValue(desc, targetNameLower)
                        || containsContactValue(desc, cleanIdentifierLower);
            }
            
            if (isRelevant) {
                displayedTxnIds.add(transactionId);
                
                final boolean finalIsSentByMe = isSentByMe;
                new Thread(() -> {
                    if (!db.chatDao().isTransactionExists(ownerAcc, transactionId)) {
                        ChatMessageEntity entity = new ChatMessageEntity(
                                ownerAcc,
                                transactionId,
                                contactMobile, 
                                desc, 
                                txn.getAmount(), 
                                finalIsSentByMe, 
                                TransactionChatMessage.TYPE_PAYMENT, 
                                txn.getDate(), 
                                "SUCCESS"
                        );
                        db.chatDao().insertMessage(entity);
                        runOnUiThread(() -> {
                            TransactionChatMessage newMsg = new TransactionChatMessage(txn.getAmount(), "SUCCESS", finalIsSentByMe, transactionId, txn.getDate());
                            newMsg.setOtherPartyName(contactName); 
                            messageList.add(newMsg);
                            adapter.notifyItemInserted(messageList.size() - 1);
                            rvChat.scrollToPosition(messageList.size() - 1);
                        });
                    }
                }).start();
            }
        }
    }

    private void sendMessage() {
        String msg = etMessage.getText().toString().trim();
        if (!msg.isEmpty()) {
            sendQuickMessage(msg);
            etMessage.setText("");
        }
    }

    private void sendQuickMessage(String msg) {
        String ownerAcc = sessionManager.getAccountNumber();
        TransactionChatMessage chatMsg = new TransactionChatMessage(msg, true, TransactionChatMessage.TYPE_TEXT);
        messageList.add(chatMsg);
        String localTxnId = "LOCAL_" + System.currentTimeMillis();
        
        // Ensure mobile is normalized before saving
        String normalizedMobile = contactMobile;
        if (normalizedMobile != null) {
            normalizedMobile = normalizedMobile.replaceAll("[^0-9]", "");
            if (normalizedMobile.length() >= 10) {
                normalizedMobile = normalizedMobile.substring(normalizedMobile.length() - 10);
            }
        }
        
        saveMessageLocally(new ChatMessageEntity(ownerAcc, localTxnId, normalizedMobile, msg, 0, true, TransactionChatMessage.TYPE_TEXT, chatMsg.getTimestamp(), ""));
        adapter.notifyItemInserted(messageList.size() - 1);
        rvChat.scrollToPosition(messageList.size() - 1);
    }

    private void saveMessageLocally(ChatMessageEntity entity) {
        new Thread(() -> db.chatDao().insertMessage(entity)).start();
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private boolean containsContactValue(String value, String expected) {
        if (value == null || expected == null || expected.isEmpty()) {
            return false;
        }
        String normalizedValue = value.toLowerCase(Locale.getDefault()).replaceAll("[^a-z0-9]", "");
        String normalizedExpected = expected.toLowerCase(Locale.getDefault()).replaceAll("[^a-z0-9]", "");
        return !normalizedExpected.isEmpty() && normalizedValue.contains(normalizedExpected);
    }

    private boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean matchesContact(String mobile, String account, String name,
                                   String targetMobile, String targetIdentifier, String targetName) {
        return containsContactValue(mobile, targetMobile)
                || containsContactValue(account, targetIdentifier)
                || containsContactValue(name, targetName);
    }

    private String getStableTransactionId(Transaction txn) {
        if (txn.getTransactionId() != null && !txn.getTransactionId().trim().isEmpty()) {
            return txn.getTransactionId();
        }
        return "SERVER_" + (txn.getType() + "|" + txn.getAmount() + "|" + txn.getDate() + "|"
                + txn.getDescription()).hashCode();
    }

    private void initiatePayment(double amount) {
        Intent intent = new Intent(this, UpiPinActivity.class);
        intent.putExtra("amount", String.valueOf(amount));
        intent.putExtra("name", contactName);
        intent.putExtra("acc", contactMobile);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            boolean verified = data.getBooleanExtra("verified", false);
            if (verified) {
                executeActualPayment(data.getStringExtra("pin"));
            }
        }
    }

    @Override
    protected void onDestroy() {
        pollingHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
