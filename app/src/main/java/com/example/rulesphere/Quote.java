package com.example.rulesphere;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Quote {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String quote;
    public String category;
    public String author;
    public boolean isFavorite = false;
    public boolean isPersonal = false;
}
