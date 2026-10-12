package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.skminibank.R;
import com.bank.skminibank.adapters.ChatAdapter;
import com.bank.skminibank.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AiAssistantActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private EditText etMessage;
    private ProgressBar progressBar;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        progressBar = findViewById(R.id.chatProgress);

        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // Initialize Text-to-Speech
        tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.US);
            }
        });

        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());
        findViewById(R.id.btnMic).setOnClickListener(v -> startVoiceInput());
        
        // Welcome Message
        addMessage("Hello! I am SK Bank AI Assistant. You can ask me about Loan, ATM, UPI or any service.", false);
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "How can I help you today?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Voice input not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                etMessage.setText(result.get(0));
                sendMessage();
            }
        }
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        addMessage(text, true);
        etMessage.setText("");
        
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            String response = getSmartResponse(text.toLowerCase());
            addMessage(response, false);
            speak(response);
        }, 1500);
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private String getSmartResponse(String query) {
        if (query.contains("loan")) {
            return "To apply for a loan, go to the 'Apply Loan' section from All Services. We offer Personal, Home, and Business loans at low interest rates.";
        } else if (query.contains("atm") || query.contains("card")) {
            return "You can apply for a new ATM/Debit card from the 'ATM Apply' section. Once approved, it will be delivered to your address in 7 to 10 working days.";
        } else if (query.contains("upi") || query.contains("pin")) {
            return "Go to 'Manage UPI' to set or change your 4-digit UPI PIN. Never share your PIN with anyone.";
        } else if (query.contains("balance")) {
            return "You can check your balance instantly by clicking on your Account Summary card on the Dashboard.";
        } else if (query.contains("transfer") || query.contains("paisa")) {
            return "You can transfer money using Mobile Number or Account Number. Go to 'Money Transfer' section for this.";
        } else if (query.contains("kyc")) {
            return "KYC is mandatory. You can submit your documents from the 'Update KYC' section in All Services.";
        } else if (query.contains("hi") || query.contains("hello")) {
            return "Hello! How can I help you with your SK Mini Bank account today?";
        } else {
            return "I am currently learning about your request. For specific queries, please contact our support team at support@skbank.com.";
        }
    }

    private void addMessage(String text, boolean isUser) {
        messages.add(new ChatMessage(text, isUser));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
