package com.bank.skminibank.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public final class PaymentVoiceUtil {

    private PaymentVoiceUtil() {
    }

    public static void speakPayment(Context context, double amount, boolean received) {
        final TextToSpeech[] speaker = new TextToSpeech[1];
        speaker[0] = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status != TextToSpeech.SUCCESS) {
                return;
            }

            String amountText = String.format(Locale.US, "%.0f", amount);
            String english = received
                    ? "Received payment of " + amountText + " rupees successfully."
                    : "You have sent " + amountText + " rupees successfully.";
            String hindi = received
                    ? amountText + " रुपये सफलतापूर्वक प्राप्त हुए हैं।"
                    : amountText + " रुपये सफलतापूर्वक भेजे गए हैं।";

            speaker[0].setOnUtteranceProgressListener(new android.speech.tts.UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    if ("payment-hindi".equals(utteranceId)) {
                        speaker[0].shutdown();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    if ("payment-hindi".equals(utteranceId)) {
                        speaker[0].shutdown();
                    }
                }
            });
            speaker[0].setLanguage(Locale.US);
            speaker[0].speak(english, TextToSpeech.QUEUE_FLUSH, null, "payment-english");
            speaker[0].setLanguage(new Locale("hi", "IN"));
            speaker[0].speak(hindi, TextToSpeech.QUEUE_ADD, null, "payment-hindi");
        });
    }
}
