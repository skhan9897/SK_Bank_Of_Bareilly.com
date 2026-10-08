package com.bank.sk_bank_of_bareilly_android

import android.app.Application

class SKBankApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            // Global Uncaught Exception Guard to prevent any unexpected thread crash
            val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                throwable.printStackTrace()
                if (defaultHandler != null) {
                    try {
                        defaultHandler.uncaughtException(thread, throwable)
                    } catch (ignored: Exception) {}
                }
            }
        } catch (ignored: Exception) {}
    }
}
