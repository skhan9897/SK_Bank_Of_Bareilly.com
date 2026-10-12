package com.bank.skminibank.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ChatMessageEntity.class, TransactionEntity.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract ChatDao chatDao();
    public abstract TransactionDao transactionDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "sk_mini_bank_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}