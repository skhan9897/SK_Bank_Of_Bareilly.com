package com.bank.skminibank.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.database.AppDatabase;
import com.bank.skminibank.database.ChatMessageEntity;
import com.bank.skminibank.model.Transaction;
import com.bank.skminibank.model.TransactionResponse;
import com.bank.skminibank.utils.SessionManager;
import com.bank.skminibank.utils.RecentRecipientStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMoneyActivity extends AppCompatActivity {

    private static final String PREF_NAME = "recent_recipients";
    private static final String KEY_RECENT_RECIPIENTS = "recent_recipients";

    private RecyclerView rvContacts;
    private EditText etSearch;
    private ImageButton btnScanQr;
    private ImageButton btnGallery;
    private final List<Contact> allContacts = new ArrayList<>();
    private final List<Contact> visibleContacts = new ArrayList<>();
    private ContactAdapter adapter;
    private SessionManager sessionManager;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        sessionManager = new SessionManager(this);
        db = AppDatabase.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        rvContacts = findViewById(R.id.rvContacts);
        etSearch = findViewById(R.id.etSearch);
        btnScanQr = findViewById(R.id.btnScanQr);
        btnGallery = findViewById(R.id.btnGallery);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ContactAdapter(visibleContacts);
        rvContacts.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s == null ? "" : s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                openContact(createQuickContact(query));
                return true;
            }
            return false;
        });

        findViewById(R.id.btnPickContact).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, 101);
        });

        btnScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(this, VerticalCaptureActivity.class);
            startActivity(intent);
        });

        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 102);
        });

        loadTransactionContacts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactionContacts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String number = cursor.getString(numberIndex) == null ? "" : cursor.getString(numberIndex).replaceAll("\\s+", "");
                String name = cursor.getString(nameIndex) == null ? "New Contact" : cursor.getString(nameIndex);

                cursor.close();

                if (!number.isEmpty()) {
                    String finalNumber = normalizeMobile(number);
                    // Resolve server name before opening
                    ApiClient.getService().searchByMobile(finalNumber).enqueue(new Callback<com.bank.skminibank.model.AccountResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<com.bank.skminibank.model.AccountResponse> call, @NonNull Response<com.bank.skminibank.model.AccountResponse> response) {
                            String actualName = name;
                            if (response.isSuccessful() && response.body() != null && "success".equalsIgnoreCase(response.body().getStatus())) {
                                actualName = response.body().getCustomerName();
                            }
                            Contact selected = new Contact(actualName, finalNumber, actualName.substring(0, 1).toUpperCase(Locale.getDefault()), "#5E35B1", 0, "");
                            saveRecentRecipient(selected.name, selected.mobile);
                            openContact(selected);
                        }
                        @Override
                        public void onFailure(@NonNull Call<com.bank.skminibank.model.AccountResponse> call, @NonNull Throwable t) {
                            Contact selected = new Contact(name, finalNumber, name.substring(0, 1).toUpperCase(Locale.getDefault()), "#5E35B1", 0, "");
                            saveRecentRecipient(selected.name, selected.mobile);
                            openContact(selected);
                        }
                    });
                }
            }
        } else if (requestCode == 102 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                Contact selected = new Contact("Gallery Scan", "", "G", "#4CAF50", 0, "");
                saveRecentRecipient(selected.name, selected.mobile);
                openContact(selected);
            }
        }
    }

    private void loadTransactionContacts() {
        // First, load from local DB to include chat-only contacts
        String ownerAcc = sessionManager.getAccountNumber();
        new Thread(() -> {
            List<ChatMessageEntity> allMessages = db.chatDao().getAllMessages(ownerAcc);
            Set<String> mobiles = new HashSet<>();
            List<Contact> localContacts = new ArrayList<>();
            
            for (ChatMessageEntity entity : allMessages) {
                if (entity.getContactMobile() != null && !mobiles.contains(entity.getContactMobile())) {
                    mobiles.add(entity.getContactMobile());
                    String name = "User " + entity.getContactMobile();
                    // We try to find name in recent prefs or transactions later
                    localContacts.add(new Contact(name, entity.getContactMobile(), name.substring(0, 1).toUpperCase(), "#78909C", 0, "Chat"));
                }
            }
            
            runOnUiThread(() -> {
                for (Contact lc : localContacts) {
                    addContactIfMissing(lc);
                }
                adapter.notifyDataSetChanged();
                
                // Then fetch from server to get actual transaction based contacts
                fetchServerTransactions();
            });
        }).start();
    }

    private void fetchServerTransactions() {
        ApiClient.getService().getTransactions(sessionManager.getAccountNumber(), sessionManager.getCustomerId()).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> txns = response.body().getTransactions();
                    if (txns != null) {
                        processTransactions(txns);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable t) {
                processTransactions(new ArrayList<>());
            }
        });
    }

    private void processTransactions(List<Transaction> txns) {
        allContacts.clear();
        addSavedRecipientsToList();

        Set<String> processedMobiles = new HashSet<>();
        for (Transaction t : txns) {
            String desc = t.getDescription();
            if (desc == null) continue;

            String mobile = "";
            String name = "";

            if (desc.contains("sent to")) {
                try {
                    String[] parts = desc.split(" sent to ");
                    String identifier = parts[parts.length - 1];
                    mobile = normalizeMobile(identifier.replace("@skpay", ""));
                } catch (Exception ignored) {}
            } else if (desc.contains("Sent to")) {
                try {
                    String[] parts = desc.split("Sent to ");
                    String identifier = parts[parts.length - 1];
                    mobile = normalizeMobile(identifier.replace("@skpay", ""));
                } catch (Exception ignored) {}
            } else if (desc.contains("Transfer to")) {
                try {
                    String[] parts = desc.split("Transfer to ");
                    String identifier = parts[parts.length - 1];
                    mobile = normalizeMobile(identifier);
                } catch (Exception ignored) {}
            }

            if (!mobile.isEmpty() && !processedMobiles.contains(mobile)) {
                processedMobiles.add(mobile);
                if (name.isEmpty()) {
                    name = "User " + (mobile.length() > 4 ? mobile.substring(mobile.length() - 4) : mobile);
                }
                Contact c = new Contact(name, mobile, name.substring(0, 1).toUpperCase(Locale.getDefault()), "#5E35B1", t.getAmount(), t.getDate());
                addContactIfMissing(c);
            }
        }
        
        // SORTING: Most recent transactions/payments at the top
        java.util.Collections.sort(allContacts, (c1, c2) -> {
            if (c1.lastDate == null && c2.lastDate == null) return 0;
            if (c1.lastDate == null) return 1;
            if (c2.lastDate == null) return -1;
            return c2.lastDate.compareTo(c1.lastDate); // Descending
        });

        promoteSavedRecipients(); // Then move manually saved/recent to top over transactions if any

        if (allContacts.isEmpty()) {
            addContactIfMissing(new Contact("Samaeen Sister", "7668246445", "S", "#2E7D32", 0, ""));
            addContactIfMissing(new Contact("Tahir Bhanja", "9368860645", "T", "#1565C0", 0, ""));
        }

        filterContacts(etSearch.getText() == null ? "" : etSearch.getText().toString());
    }

    private void addSavedRecipientsToList() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        for (String entry : RecentRecipientStore.read(prefs)) {
            String[] parts = entry.split("::", 2);
            if (parts.length == 2) {
                String name = parts[0];
                String mobile = normalizeMobile(parts[1]);
                if (!mobile.isEmpty()) {
                    addContactIfMissing(new Contact(name, mobile, name.substring(0, 1).toUpperCase(Locale.getDefault()), "#4CAF50", 0, ""));
                }
            }
        }
    }

    private void promoteSavedRecipients() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        List<Contact> reordered = new ArrayList<>();
        Set<String> addedMobiles = new HashSet<>();

        for (String entry : RecentRecipientStore.read(prefs)) {
            String[] parts = entry.split("::", 2);
            if (parts.length != 2) {
                continue;
            }
            String mobile = normalizeMobile(parts[1]);
            for (Contact contact : allContacts) {
                if (mobile.equalsIgnoreCase(contact.mobile) && !addedMobiles.contains(mobile)) {
                    reordered.add(contact);
                    addedMobiles.add(mobile);
                    break;
                }
            }
        }

        for (Contact contact : allContacts) {
            if (!addedMobiles.contains(contact.mobile)) {
                reordered.add(contact);
            }
        }
        allContacts.clear();
        allContacts.addAll(reordered);
    }

    private void addContactIfMissing(Contact contact) {
        for (Contact existing : allContacts) {
            if (existing.mobile.equalsIgnoreCase(contact.mobile)) {
                return;
            }
        }
        allContacts.add(contact);
    }

    private void filterContacts(String query) {
        visibleContacts.clear();
        String search = query == null ? "" : query.trim().toLowerCase(Locale.getDefault());

        if (search.isEmpty()) {
            visibleContacts.addAll(allContacts);
        } else {
            for (Contact contact : allContacts) {
                String nameLower = contact.name != null ? contact.name.toLowerCase(Locale.getDefault()) : "";
                String mobileLower = contact.mobile != null ? contact.mobile.toLowerCase(Locale.getDefault()) : "";
                if (nameLower.contains(search) || mobileLower.contains(search)) {
                    visibleContacts.add(contact);
                }
            }
            
            // If no local match and it looks like a mobile number, search on server
            if (visibleContacts.isEmpty() && search.length() >= 10 && search.matches("[0-9]+")) {
                performServerSearch(search);
            } else if (visibleContacts.isEmpty()) {
                visibleContacts.add(createQuickContact(query));
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void performServerSearch(String mobile) {
        ApiClient.getService().searchByMobile(mobile).enqueue(new Callback<com.bank.skminibank.model.AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<com.bank.skminibank.model.AccountResponse> call, @NonNull Response<com.bank.skminibank.model.AccountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.bank.skminibank.model.AccountResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.getStatus()) && res.getCustomerName() != null) {
                        Contact serverContact = new Contact(res.getCustomerName(), mobile, res.getCustomerName().substring(0, 1).toUpperCase(), "#E91E63", 0, "Registered User");
                        addContactIfMissing(serverContact);
                        // Trigger filter again to show the new contact
                        String currentQuery = etSearch.getText().toString();
                        if (currentQuery.contains(mobile)) {
                            if (!visibleContacts.contains(serverContact)) {
                                visibleContacts.add(0, serverContact);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
            @Override public void onFailure(@NonNull Call<com.bank.skminibank.model.AccountResponse> call, @NonNull Throwable t) {}
        });
    }

    private Contact createQuickContact(String raw) {
        String normalized = normalizeMobile(raw);
        if (!normalized.isEmpty()) {
            return new Contact("Pay to " + normalized, normalized, "P", "#4CAF50", 0, "");
        }
        return new Contact("Pay to " + raw, raw, "P", "#4CAF50", 0, "");
    }

    private String normalizeMobile(String input) {
        if (input == null) return "";
        String digits = input.replaceAll("[^0-9]", "");
        return digits.length() > 10 ? digits.substring(digits.length() - 10) : digits;
    }

    private void saveRecentRecipient(String name, String mobile) {
        RecentRecipientStore.save(this, name, mobile);
    }

    private void openContact(Contact contact) {
        if (!"VERIFIED".equalsIgnoreCase(sessionManager.getKycStatus())) {
            Toast.makeText(this, "PLEASE UPDATE AND VERIFY YOUR KYC FIRST", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, KYCUpdateActivity.class));
            return;
        }

        if (contact.mobile == null || contact.mobile.isEmpty()) return;
        saveRecentRecipient(contact.name, contact.mobile);
        Intent intent = new Intent(SendMoneyActivity.this, ChatTransferActivity.class);
        intent.putExtra("name", contact.name);
        intent.putExtra("mobile", contact.mobile);
        intent.putExtra("initial", contact.initial);
        intent.putExtra("color", contact.color);
        startActivity(intent);
    }

    private static class Contact {
        String name, mobile, initial, color, lastDate;
        double lastAmount;

        Contact(String n, String m, String i, String c, double amt, String date) {
            this.name = n;
            this.mobile = m;
            this.initial = i;
            this.color = c;
            this.lastAmount = amt;
            this.lastDate = date;
        }
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
        private final List<Contact> list;

        ContactAdapter(List<Contact> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pp_contact, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Contact c = list.get(position);
            holder.name.setText(c.name);
            holder.mobileLabel.setText(c.mobile.isEmpty() ? "Tap to start payment" : "+91 " + c.mobile);
            holder.initial.setText(c.initial);
            holder.card.setCardBackgroundColor(android.graphics.Color.parseColor(c.color));

            // Fetch last amount from local DB if server didn't provide it
            if (c.lastAmount <= 0 && !c.mobile.isEmpty()) {
                String ownerAcc = sessionManager.getAccountNumber();
                new Thread(() -> {
                    List<ChatMessageEntity> history = db.chatDao().getChatHistory(ownerAcc, c.mobile);
                    if (!history.isEmpty()) {
                        for (int i = history.size() - 1; i >= 0; i--) {
                            ChatMessageEntity entity = history.get(i);
                            if (entity.getType() == 2 && entity.getAmount() > 0) { // TYPE_PAYMENT
                                final double amt = entity.getAmount();
                                final String time = entity.getTimestamp();
                                runOnUiThread(() -> {
                                    c.lastAmount = amt;
                                    c.lastDate = time;
                                    holder.time.setText("₹" + String.format(Locale.getDefault(), "%.0f", amt));
                                    holder.mobileLabel.setText("Last paid: ₹" + String.format(Locale.getDefault(), "%.0f", amt));
                                });
                                break;
                            }
                        }
                    }
                }).start();
            }

            if (c.lastAmount > 0) {
                holder.time.setText("₹" + String.format(Locale.getDefault(), "%.0f", c.lastAmount));
                holder.mobileLabel.setText("Last paid: ₹" + String.format(Locale.getDefault(), "%.0f", c.lastAmount));
            } else {
                holder.time.setText(c.lastDate);
            }

            holder.itemView.setOnClickListener(v -> openContact(c));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, mobileLabel, initial, time;
            androidx.cardview.widget.CardView card;

            ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.tvContactName);
                mobileLabel = v.findViewById(R.id.tvContactMobile);
                initial = v.findViewById(R.id.tvContactInitial);
                card = v.findViewById(R.id.cardInitial);
                time = v.findViewById(R.id.tvTime);
            }
        }
    }
}
