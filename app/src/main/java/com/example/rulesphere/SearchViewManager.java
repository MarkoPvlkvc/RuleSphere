package com.example.rulesphere;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class SearchViewManager {
    MainActivity mainActivity;
    FrameLayout searchViewFrameLayout;
    View searchView;
    ObjectAnimator animatorToTop, animatorToBottom;
    public boolean isShown = false;
    String searchViewText = "", searchViewCategory = "";

    public SearchViewManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        createSearchView();
    }

    private void createSearchView() {
        searchViewFrameLayout = mainActivity.findViewById(R.id.searchViewFrameLayout);
        searchView = LayoutInflater.from(mainActivity).inflate(R.layout.search_view, searchViewFrameLayout, false);

        ViewTreeObserver viewTreeObserver = searchViewFrameLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // Remove the listener to avoid redundant calls
                    searchViewFrameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    // Set the initial translationY value to the height of the FrameLayout
                    searchView.setTranslationY(searchViewFrameLayout.getHeight());

                    // Add the search view to the FrameLayout
                    searchViewFrameLayout.addView(searchView);
                }
            });
        }

        RecyclerView recyclerView = searchView.findViewById(R.id.recycler_view_search);
        updateSearchView(recyclerView);

        // BOTTOM SHEET
        FrameLayout filterBottomSheetView = searchView.findViewById(R.id.filterBottomSheet);
        ChipGroup filterBottomSheetChipGroup = searchView.findViewById(R.id.filterChipGroup);
        if (filterBottomSheetView.getParent() != null) {
            ((ViewGroup) filterBottomSheetView.getParent()).removeView(filterBottomSheetView);
        }
        BottomSheetDialog filterBottomSheetDialog = new BottomSheetDialog(mainActivity);
        filterBottomSheetDialog.setContentView(filterBottomSheetView);

        MaterialButton.OnCheckedChangeListener filterOnChecked = new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                if (isChecked) {
                    button.setBackgroundTintList(ColorStateList.valueOf(getColorFromResource(mainActivity, com.google.android.material.R.attr.colorPrimary)));
                    button.setTextColor(getColorFromResource(mainActivity, com.google.android.material.R.attr.colorOnPrimary));
                    button.setStrokeColor(ColorStateList.valueOf(Color.TRANSPARENT));
                } else {
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                    button.setTextColor(getColorFromResource(mainActivity, com.google.android.material.R.attr.colorOnSurfaceVariant));
                    button.setStrokeColor(ColorStateList.valueOf(getColorFromResource(mainActivity, com.google.android.material.R.attr.colorOutline)));
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

                updateSearchView(recyclerView);
            }
        };

        for (int i = 0; i < filterBottomSheetChipGroup.getChildCount(); i++) {
            MaterialButton materialButton = (MaterialButton) filterBottomSheetChipGroup.getChildAt(i);
            materialButton.addOnCheckedChangeListener(filterOnChecked);
            materialButton.setOnClickListener(filterOnClick);
        }
        // BOTTOM SHEET

        searchView.findViewById(R.id.closeSearchView).setOnClickListener(v2 -> {
            View view = mainActivity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            mainActivity.onBackPressed();
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
                        updateSearchView(recyclerView);
                    }
                }, 500);
            }
        });

        FloatingActionButton goToTopFab = searchView.findViewById(R.id.fab_goToTop);

        goToTopFab.setOnClickListener(v2 -> {
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

    public void show() {
        if (!isShown) {
            // Animator for the translationY property to 0
            animatorToTop = ObjectAnimator.ofFloat(searchView, "translationY", 0);
            animatorToTop.setDuration(300);
            animatorToTop.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorToTop.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    int endColor = getColorFromResource(mainActivity, android.R.attr.statusBarColor);

                    mainActivity.getWindow().setStatusBarColor(endColor);
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {

                }
            });
            animatorToTop.start();

            isShown = true;
        }
    }

    public void hide() {
        ScrollView homeScrollView = mainActivity.findViewById(R.id.homeScrolLView);
        if (homeScrollView != null) {
            homeScrollView.setScrollY(0);
        }

        if (isShown) {
            // Animator for the translationY to bottom
            animatorToBottom = ObjectAnimator.ofFloat(searchView, "translationY", searchViewFrameLayout.getHeight());
            animatorToBottom.setDuration(300);
            animatorToBottom.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorToBottom.start();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                searchViewText = null;

                isShown = false;
            }
        }, 300);
    }

    public void updateSearchView(RecyclerView rv) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Quote> quotes;
                QuoteDao quoteDao = getDb().quoteDao();

                quotes = quoteDao.getAllSearchAndCategory2(searchViewText, searchViewCategory);

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CardAdapter adapter = new CardAdapter(quotes, mainActivity, rv.getId());
                        rv.setAdapter(adapter);
                        rv.setLayoutManager(new LinearLayoutManager(mainActivity));
                    }
                });
            }
        });
    }

    public RuleSphereDatabase getDb() {
        return RuleSphereDatabase.getInstance(mainActivity);
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
