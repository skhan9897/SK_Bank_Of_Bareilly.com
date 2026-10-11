package com.bank.sk_bank_of_bareilly_android

import android.app.Application
import android.util.Log

class SKBankApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            // Uncaught Exception Handler Guard to log errors without killing the app thread
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                Log.e("SKBankApp", "Caught uncaught exception on thread ${thread.name}", throwable)
            }
        } catch (ignored: Exception) {}
    }
}
