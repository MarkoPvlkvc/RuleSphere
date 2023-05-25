package com.example.rulesphere;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.DeleteTable;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuoteDao {
    @Query("SELECT * FROM Quote")
    List<Quote> getAll();

    @Query("SELECT * FROM Quote WHERE (:searchTerm IS NULL OR QUOTE LIKE '%' || :searchTerm || '%') AND (:category IS NULL OR CATEGORY LIKE '%' || :category || '%')")
    List<Quote> getAllSearchAndCategory2(String searchTerm, String category);

    @Query("SELECT * FROM Quote WHERE FAVORITE = 1")
    List<Quote> getFavorites();

    @Query("SELECT * FROM Quote WHERE ID = :id")
    Quote getQuote(String id);

    @Query("SELECT * FROM Quote WHERE DAY_USED = :dayInYear")
    Quote getQuoteAtDay(int dayInYear);

    @Query("SELECT * FROM Quote WHERE DAY_USED = 0 ORDER BY RANDOM() LIMIT 1")
    Quote getRandomQuote();

    @Query("SELECT * FROM Quote WHERE PERSONAL = 1")
    List<Quote> getPersonal();

    @Query("SELECT * FROM Quote WHERE ADMIN_PICKED = 1")
    List<Quote> getAdminPicked();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Quote quote);

    @Delete
    void delete(Quote quote);

    @Query("SELECT COUNT(*) FROM Quote")
    int getRowCount();
}
