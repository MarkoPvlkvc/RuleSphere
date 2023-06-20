package com.example.rulesphere;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RuleSphereDatabaseUnitTests {
    private RuleSphereDatabase database;
    private QuoteDao quoteDao;

    @Before
    public void setup() {
        // Create an in-memory Room database for testing
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RuleSphereDatabase.class
        ).build();

        quoteDao = database.quoteDao();
    }

    @After
    public void cleanup() {
        // Close the database after testing
        database.close();
    }

    @Test
    public void insertAndGetQuote() throws Exception {
        // Create a Quote object
        Quote quote = new Quote();
        quote.quote = "Test quote";
        quote.category = "Test category";
        quote.author = "Test author";
        quote.id = 1;

        // Insert the quote into the database
        quoteDao.insert(quote);

        // Retrieve the quote from the database
        Quote retrievedQuote = quoteDao.getQuote(1 + "");

        // Assert that the retrieved quote is equal to the inserted quote
        assertEquals(quote.quote, retrievedQuote.quote);
    }

    @Test
    public void getAllQuotes() throws Exception {
        // Insert some test quotes into the database
        Quote quote1 = new Quote();
        quote1.quote = "Test quote 1";
        quote1.category = "Test category 1";
        quote1.author = "Test author 1";

        Quote quote2 = new Quote();
        quote2.quote = "Test quote 2";
        quote2.category = "Test category 2";
        quote2.author = "Test author 2";

        Quote quote3 = new Quote();
        quote3.quote = "Test quote 3";
        quote3.category = "Test category 3";
        quote3.author = "Test author 3";

        quoteDao.insert(quote1);
        quoteDao.insert(quote2);
        quoteDao.insert(quote3);

        // Retrieve all quotes from the database
        List<Quote> quotes = quoteDao.getAll();

        // Assert that the number of retrieved quotes matches the number of inserted quotes
        assertEquals(3, quotes.size());
    }

    @Test
    public void getPersonal() throws Exception {
        Quote quote1 = new Quote();
        quote1.quote = "Test quote 1";
        quote1.category = "Test category 1";
        quote1.author = "Test author 1";
        quote1.isPersonal = true;

        Quote quote2 = new Quote();
        quote2.quote = "Test quote 2";
        quote2.category = "Test category 2";
        quote2.author = "Test author 2";
        quote2.isPersonal = false;

        Quote quote3 = new Quote();
        quote3.quote = "Test quote 3";
        quote3.category = "Test category 3";
        quote3.author = "Test author 3";

        quoteDao.insert(quote1);
        quoteDao.insert(quote2);
        quoteDao.insert(quote3);

        List<Quote> quotes = quoteDao.getPersonal();

        assertEquals(1, quotes.size());
    }
}