package com.example.rulesphere;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuoteDao {
    @Query("SELECT * FROM Quote")
    List<Quote> getAll();

    @Query("SELECT * FROM Quote WHERE Quote.isFavorite = 1")
    List<Quote> getFavorites();

    @Query("SELECT * FROM Quote WHERE Quote.isPersonal = 1")
    List<Quote> getPersonal();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Quote quote);

    @Delete
    void delete(Quote quote);
}
