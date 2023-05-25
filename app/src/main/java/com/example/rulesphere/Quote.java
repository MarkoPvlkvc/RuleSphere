package com.example.rulesphere;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Quote {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    public long id;
    @ColumnInfo(name = "QUOTE")
    public String quote;
    @ColumnInfo(name = "CATEGORY")
    public String category;
    @ColumnInfo(name = "AUTHOR")
    public String author;
    @ColumnInfo(name = "FAVORITE")
    public boolean isFavorite = false;
    @ColumnInfo(name = "PERSONAL")
    public boolean isPersonal = false;
    @ColumnInfo(name = "ADMIN_PICKED")
    public boolean isAdminPicked = false;

    @ColumnInfo(name = "DAY_USED")
    public int dayUsed;
}
