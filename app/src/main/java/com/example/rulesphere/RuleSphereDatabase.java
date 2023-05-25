package com.example.rulesphere;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Quote.class}, version = 5)
public abstract class RuleSphereDatabase extends RoomDatabase {

    private static final String DB_NAME = "quote_database";

    // Singleton instance of the database
    private static RuleSphereDatabase instance;

    public static synchronized RuleSphereDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            RuleSphereDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract QuoteDao quoteDao();
}
