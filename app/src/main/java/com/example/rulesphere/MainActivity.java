package com.example.rulesphere;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rulesphere.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.transition.MaterialFadeThrough;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

        searchView.getToolbar().setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                if (Objects.equals(activeFragment, "home") && !searchView.isShowing())
                    return true;
                activeFragment = "home";
                if (searchView.isShowing())
                    searchView.hide();
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.search) {
                updateSearchView();
                searchView.show();
                updateStatusBarColor();
                //searchView.getToolbar().setLogo(R.drawable.baseline_format_quote_24);
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

                    chip.setChipBackgroundColor(null);
                }

                updateSearchView();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        MaterialFadeThrough enterTransition = new MaterialFadeThrough();
        enterTransition.setSecondaryAnimatorProvider(null);
        fragment.setEnterTransition(enterTransition);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (searchView.isShowing() || findViewById(R.id.closeSearchView) != null) {
            View searchContext = findViewById(R.id.searchContext);
            if (searchContext != null) {
                searchContext.findViewById(R.id.imageView).performClick();
                return;
            }

            searchView.hide();
            hideSearchView();

            if (Objects.equals(activeFragment, "home"))
                binding.bottomNavigation.setSelectedItemId(R.id.home);
            else if (Objects.equals(activeFragment, "design"))
                binding.bottomNavigation.setSelectedItemId(R.id.design);
            else if (Objects.equals(activeFragment, "myRules"))
                binding.bottomNavigation.setSelectedItemId(R.id.myRules);

            //updateStatusBarColor();
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

                quotes = quoteDao.getAllSearchAndCategory2(searchViewText, searchViewCategory);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CardAdapter adapter = new CardAdapter(quotes, MainActivity.this, R.id.recycler_view_search);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                });
            }
        });
    }

    public void updateMyRulesList(RecyclerView rv) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Quote> quotes;
                QuoteDao quoteDao = getDb().quoteDao();

                quotes = quoteDao.getFavorites();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CardAdapter adapter = new CardAdapter(quotes, MainActivity.this, R.id.recycler_view_myRules);
                        rv.setAdapter(adapter);
                        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                });
            }
        });
    }

    public void updateMyQuotesList(RecyclerView rv) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Quote> quotes;
                QuoteDao quoteDao = getDb().quoteDao();

                quotes = quoteDao.getPersonal();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CardAdapter adapter = new CardAdapter(quotes, MainActivity.this, R.id.recycler_view_myRules);
                        rv.setAdapter(adapter);
                        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                });
            }
        });
    }

    public void favoriteAQuote(String id) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                QuoteDao quoteDao = getDb().quoteDao();
                Quote quote = quoteDao.getQuote(id);
                quote.isFavorite = !quote.isFavorite;
                quoteDao.insert(quote);
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

    public void updateSearchView2(RecyclerView rv) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Quote> quotes;
                QuoteDao quoteDao = getDb().quoteDao();

                quotes = quoteDao.getAllSearchAndCategory2(searchViewText, searchViewCategory);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CardAdapter adapter = new CardAdapter(quotes, MainActivity.this, rv.getId());
                        rv.setAdapter(adapter);
                        rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    }
                });
            }
        });
    }

    //Na kraju implementacije zamijenit addView i removeView tako da na pocetku adda u onCreate
    //i onda samo sakriva i pokazuje kada je potrebno
    //maknut sve funkcije (listenere) i to gore kada se sada jednom treba kreirat
    public void showSearchView() {
        View searchView = LayoutInflater.from(this).inflate(R.layout.search_view, null);
        FrameLayout searchViewFrameLayout = findViewById(R.id.searchViewFrameLayout);
        // Set the initial translationX value to the width of the FrameLayout
        searchView.setTranslationY(searchViewFrameLayout.getHeight());
        searchViewFrameLayout.addView(searchView);

        RecyclerView rv = searchView.findViewById(R.id.recycler_view_search);
        updateSearchView2(rv);

        // Animate the translationY property to 0
        ObjectAnimator animator = ObjectAnimator.ofFloat(searchView, "translationY", 0);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                int endColor;
                if (isNightMode) {
                    endColor = getApplicationContext().getColor(R.color.md_theme_dark_background);
                } else {
                    endColor = getApplicationContext().getColor(R.color.md_theme_light_background);
                }

                getWindow().setStatusBarColor(endColor);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        animator.start();

        // BOTTOM SHEET
        FrameLayout filterBottomSheetView = findViewById(R.id.filterBottomSheet);
        ChipGroup filterBottomSheetChipGroup = findViewById(R.id.filterChipGroup);
        if (filterBottomSheetView.getParent() != null) {
            ((ViewGroup) filterBottomSheetView.getParent()).removeView(filterBottomSheetView);
        }
        BottomSheetDialog filterBottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        filterBottomSheetDialog.setContentView(filterBottomSheetView);

        MaterialButton.OnCheckedChangeListener filterOnChecked = new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                if (isChecked) {
                    button.setBackgroundTintList(ColorStateList.valueOf(getColorFromResource(MainActivity.this, com.google.android.material.R.attr.colorPrimaryContainer)));
                } else {
                    button.setBackgroundTintList(ColorStateList.valueOf(getColorFromResource(MainActivity.this, com.google.android.material.R.attr.colorSurfaceVariant)));
                }
            }
        };

        View.OnClickListener filterOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChipGroup chipGroup = (ChipGroup) v.getParent();
                boolean somethingChecked = false;
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    MaterialButton materialButton = (MaterialButton) chipGroup.getChildAt(i);

                    if (materialButton == v && materialButton.isChecked()) {
                        searchViewCategory = (String) materialButton.getText();
                        somethingChecked = true;
                        continue;
                    }

                    materialButton.setChecked(false);
                }

                if (!somethingChecked) {
                    searchViewCategory = "";
                }

                updateSearchView2(rv);
            }
        };

        for (int i = 0; i < filterBottomSheetChipGroup.getChildCount(); i++) {
            MaterialButton materialButton = (MaterialButton) filterBottomSheetChipGroup.getChildAt(i);
            materialButton.addOnCheckedChangeListener(filterOnChecked);
            materialButton.setOnClickListener(filterOnClick);
        }
        // BOTTOM SHEET

        searchView.findViewById(R.id.closeSearchView).setOnClickListener(v2 -> {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            // Daje vremena tipkovnici da se zatvori da bi search view zauzimao cijeli ekran
            // da mu animacija ne bi prekinula na pola ekrana prema dolje
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideSearchView();
                }
            }, 10);
        });

        MaterialButton filterSearchViewButton = searchView.findViewById(R.id.filterSearchView);
        filterSearchViewButton.setOnClickListener(v2 -> {
            filterBottomSheetDialog.show();
        });

        TextInputEditText searchViewInput = searchView.findViewById(R.id.searchViewInput);
        searchViewInput.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.removeCallbacksAndMessages(null);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchViewText = editable.toString();
                        updateSearchView2(rv);
                    }
                }, 500);
            }
        });

        FloatingActionButton goToTopFab = searchView.findViewById(R.id.fab_goToTop);

        goToTopFab.setOnClickListener(v2 -> {
            final float MILLISECONDS_PER_INCH = 5f;
            LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(rv.getContext()) {
                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                }
            };

            linearSmoothScroller.setTargetPosition(0);
            rv.getLayoutManager().startSmoothScroll(linearSmoothScroller);
        });

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < 0) {
                    goToTopFab.show();
                } else {
                    goToTopFab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.computeVerticalScrollOffset() == 0) {
                    goToTopFab.hide();
                }
            }
        });
    }

    public void hideSearchView() {
        FrameLayout searchViewFrameLayout = findViewById(R.id.searchViewFrameLayout);
        View searchView = searchViewFrameLayout.getChildAt(0); // Get the first child view (searchView)

        ScrollView homeScrollView = findViewById(R.id.homeScrolLView);
        if (homeScrollView != null) {
            homeScrollView.setScrollY(0);
        }

        // Animate the translationY property to 100%
        ObjectAnimator animator = ObjectAnimator.ofFloat(searchView, "translationY", searchViewFrameLayout.getHeight());
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                searchViewFrameLayout.removeView(searchView);
                searchViewText = null;
            }
        }, 300);
    }

    private void animateStatusBarColor(final int endColor) {
        final Window window = getWindow();
        final int animationDuration = 300;
        final int startColor = window.getStatusBarColor();

        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimator.setDuration(animationDuration);

        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int animatedValue = (int) animator.getAnimatedValue();
                window.setStatusBarColor(animatedValue);
            }
        });

        colorAnimator.start();
    }

    public int getColorFromResource(Context context, int resource) {
        TypedValue typedValue = new TypedValue();
        int colorSurfaceVariant = 0;

        // Resolve the attribute value
        boolean resolved = context.getTheme().resolveAttribute(resource, typedValue, true);

        if (resolved && typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // Attribute value is a color
            colorSurfaceVariant = typedValue.data;
        }

        return colorSurfaceVariant;
    }
}