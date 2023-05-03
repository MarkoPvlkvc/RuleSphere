package com.example.rulesphere;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Quote.class}, version = 2)
public abstract class RuleSphereDatabase extends RoomDatabase {
    public abstract QuoteDao quoteDao();
}
