package com.example.rulesphere;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rulesphere.databinding.ActivityMainBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    SearchView searchView;
    String searchViewText = "";
    String searchViewCategory = "";
    MaterialButton searchBackButton;
    FloatingActionButton goToTopButton;
    String activeFragment;
    RecyclerView recyclerView;

    Chip religionChip, movieChip, philosophyChip, martialArtsChip,
            famousPeopleChip, sportChip, musicChip;
    int statusBarDefaultColor, statusBarScrolledColor;
    boolean isNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Database
        QuoteDao quoteDao = getDb().quoteDao();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (quoteDao.getRowCount() == 0)
                {
                    Context context = getApplicationContext();
                    try {
                        InputStream is = context.getAssets().open("database/quote_database.json");
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();

                        String json = new String(buffer, StandardCharsets.UTF_8);
                        JSONArray quotesArray = new JSONArray(json);

                        for (int i = 0; i < quotesArray.length(); i++) {
                            JSONObject quoteObj = quotesArray.getJSONObject(i);

                            Quote quote = new Quote();
                            quote.quote = quoteObj.getString("QUOTE");
                            quote.category = quoteObj.getString("CATEGORY");
                            quote.author = quoteObj.getString("AUTHOR");
                            quote.isAdminPicked = quoteObj.getBoolean("ADMIN_PICKED");
                            quoteDao.insert(quote);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                updateSearchView();
            }
        });
        // Database

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // INITIALIZATION
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view_search);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        goToTopButton = findViewById(R.id.extended_fab_go_to_top);

        religionChip = findViewById(R.id.religionChip);
        movieChip = findViewById(R.id.movieChip);
        philosophyChip = findViewById(R.id.philosophyChip);
        martialArtsChip = findViewById(R.id.martialArtsChip);
        famousPeopleChip = findViewById(R.id.famousPeopleChip);
        sportChip = findViewById(R.id.sportChip);
        musicChip = findViewById(R.id.musicChip);

        isNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        if (isNightMode) {
            statusBarDefaultColor = getApplicationContext().getColor(R.color.md_theme_dark_background);
            statusBarScrolledColor = getApplicationContext().getColor(R.color.md_theme_dark_searchViewInputBackground);
        } else {
            statusBarDefaultColor = getApplicationContext().getColor(R.color.md_theme_light_background);
            statusBarScrolledColor = getApplicationContext().getColor(R.color.md_theme_light_searchViewInputBackground);
        }
        // INITIALIZATION

        replaceFragment(new HomeFragment());
        activeFragment = "home";

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
                updateStatusBarColor();
                searchView.getToolbar().setLogo(R.drawable.baseline_format_quote_24);
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

        /*
        searchBackButton.setOnClickListener(v -> {
            // Closes soft keyboard
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            // Closes soft keyboard

            onBackPressed();
        });*/

        goToTopButton.setOnClickListener(v -> {
            final float MILLISECONDS_PER_INCH = 5f;
            LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                }
            };

            linearSmoothScroller.setTargetPosition(0);
            recyclerView.getLayoutManager().startSmoothScroll(linearSmoothScroller);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < 0) {
                    goToTopButton.show();
                } else {
                    goToTopButton.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.computeVerticalScrollOffset() == 0) {
                    goToTopButton.hide();
                }
            }
        });

        searchView.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchViewText = searchView.getText().toString();
                updateSearchView();
                return false;
            }
        });

        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    Chip chip = (Chip) group.getChildAt(i);

                    if (checkedIds.contains(chip.getId())) {
                        if (isNightMode)
                            chip.setChipBackgroundColorResource(R.color.md_theme_light_primary);
                        else
                            chip.setChipBackgroundColorResource(com.google.android.material.R.color.material_dynamic_primary80);

                        searchViewCategory = chip.getText().toString();

                        continue;
                    }

                    if (checkedIds.isEmpty())
                        searchViewCategory = "";

                    chip.setChecked(false);
                    chip.setChipBackgroundColor(null);
                }

                updateSearchView();
            }
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

            updateStatusBarColor();
        } else {
            super.onBackPressed();
        }
    }

    public RuleSphereDatabase getDb() {
        return RuleSphereDatabase.getInstance(getApplicationContext());
    }

    public void updateSearchView() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Quote> quotes;
                QuoteDao quoteDao = getDb().quoteDao();

//                if (searchViewText.isEmpty() && searchViewCategory.isEmpty())
//                    quotes = quoteDao.getAll();
//                else if (!searchViewText.isEmpty() && searchViewCategory.isEmpty())
//                    quotes = quoteDao.getAllSearch(searchViewText);
//                else if (searchViewText.isEmpty())
//                    quotes = quoteDao.getAllCategory(searchViewCategory);
//                else
//                    quotes = quoteDao.getAllSearchAndCategory(searchViewText, searchViewCategory);

                quotes = quoteDao.getAllSearchAndCategory2(searchViewText, searchViewCategory);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CardAdapter adapter = new CardAdapter(quotes);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                });
            }
        });
    }

    public void updateStatusBarColor() {
        int startColor = getWindow().getStatusBarColor();
        int endColor;

        try {
            if (getWindow().findViewById(R.id.homeScrolLView).getScrollY() != 0)
                return;
        } catch (NullPointerException ignored) {}

        if (startColor == statusBarDefaultColor)
            endColor = statusBarScrolledColor;
        else
            endColor = statusBarDefaultColor;

        ValueAnimator colorAnimation = ValueAnimator.ofArgb(startColor, endColor);
        colorAnimation.setDuration(250);

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int color = (int) animator.getAnimatedValue();
                Window window = getWindow();
                window.setStatusBarColor(color);
            }
        });

        colorAnimation.start();
    }
}