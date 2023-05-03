package com.example.rulesphere;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.example.rulesphere.databinding.ActivityMainBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.search.SearchView;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    SearchView searchView;
    MaterialButton searchBackButton;
    String activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Database
        RuleSphereDatabase db = Room.databaseBuilder(getApplicationContext(),
                RuleSphereDatabase.class, "query-database")
                .fallbackToDestructiveMigration()
                .build();

        Quote quote = new Quote();
        quote.quote = "prob2";
        quote.category = "kategorija";
        quote.author = "Autor";
        //quote.isFavorite = false;
        //quote.isPersonal = false;

        QuoteDao quoteDao = db.quoteDao();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //quoteDao.insert(quote);
                //quoteDao.getAll();
                List<Quote> favorites = quoteDao.getFavorites();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("MARKO", "favorites: " + favorites.toString());
                    }
                });
            }
        });
        // Database

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchView = findViewById(R.id.search_view);
        searchBackButton = findViewById(R.id.search_back_button);
        replaceFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                if (Objects.equals(activeFragment, "home") && !searchView.isShowing())
                    return true;
                activeFragment = "home";
                if (searchView.isShowing())
                    searchView.hide();
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.search) {
                searchView.show();
            } else if (item.getItemId() == R.id.design) {
                if (Objects.equals(activeFragment, "design") && !searchView.isShowing())
                    return true;
                activeFragment = "design";
                if (searchView.isShowing())
                    searchView.hide();
                replaceFragment(new DesignFragment());
            } else if (item.getItemId() == R.id.myRules) {
                if (Objects.equals(activeFragment, "myRules") && !searchView.isShowing())
                    return true;
                activeFragment = "myRules";
                if (searchView.isShowing())
                    searchView.hide();
                replaceFragment(new MyRulesFragment());
            }

            return true;
        });

        searchBackButton.setOnClickListener(v -> {
            // Closes soft keyboard
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            // Closes soft keyboard

            onBackPressed();
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frameLayout, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (searchView.isShowing()) {
            searchView.hide();

            if (Objects.equals(activeFragment, "home"))
                binding.bottomNavigation.setSelectedItemId(R.id.home);
            else if (Objects.equals(activeFragment, "design"))
                binding.bottomNavigation.setSelectedItemId(R.id.design);
            else if (Objects.equals(activeFragment, "myRules"))
                binding.bottomNavigation.setSelectedItemId(R.id.myRules);
        } else {
            super.onBackPressed();
        }
    }
}