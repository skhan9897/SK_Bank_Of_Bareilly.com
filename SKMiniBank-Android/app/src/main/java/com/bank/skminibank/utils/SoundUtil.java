package com.bank.skminibank.utils;

import android.media.AudioManager;
import android.media.ToneGenerator;

public class SoundUtil {
    public static void playSuccessTone() {
        try {
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_PROP_ACK, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
