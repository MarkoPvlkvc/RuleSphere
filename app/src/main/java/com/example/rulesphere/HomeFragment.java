package com.example.rulesphere;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    MaterialCardView materialCardView;
    MaterialButton favouriteButton;
    MaterialButton createQuoteButton;
    MaterialButton designWallpaperButton;
    ScrollView scrollView;
    View.OnScrollChangeListener scrollViewScrollChange;
    int statusBarDefaultColor, statusBarScrolledColor;
    boolean isNightMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        materialCardView = view.findViewById(R.id.ruleCard);
        favouriteButton = view.findViewById(R.id.favouriteButton);
        createQuoteButton = view.findViewById(R.id.createQuoteButton);
        designWallpaperButton = view.findViewById(R.id.designWallpaperButton);
        scrollView = view.findViewById(R.id.homeScrolLView);

        isNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        if (isNightMode) {
            statusBarDefaultColor = getContext().getColor(R.color.md_theme_dark_background);
            statusBarScrolledColor = getContext().getColor(R.color.md_theme_dark_searchViewInputBackground);
        } else {
            statusBarDefaultColor = getContext().getColor(R.color.md_theme_light_background);
            statusBarScrolledColor = getContext().getColor(R.color.md_theme_light_searchViewInputBackground);
        }

        materialCardView.setCheckedIcon(null);
        materialCardView.setStrokeWidth(0);

        materialCardView.setOnClickListener(v -> {
            materialCardView.toggle();

            favouriteButton.setChecked(materialCardView.isChecked());
            favouriteButton.setBackground(null);

            materialCardView.setCardForegroundColor(null);
            Drawable drawable = favouriteButton.getCompoundDrawables()[0].mutate();
            if (materialCardView.isChecked()) {
                materialCardView.setStrokeWidth(5);

                /* CHANGE CARD HEART COLOR
                drawable.setColorFilter(getResources().getColor(com.google.android.material.R.color.m3_ref_palette_dynamic_primary80), PorterDuff.Mode.SRC_ATOP);
                favouriteButton.setCompoundDrawables(drawable, null, null, null);
                */
            } else {
                materialCardView.setStrokeWidth(0);

                drawable.setColorFilter(null);
                favouriteButton.setCompoundDrawables(drawable, null, null, null);
            }

            Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.like_anim);
            favouriteButton.startAnimation(animation);
        });

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);

        createQuoteButton.setOnClickListener(v -> {
            if (createQuoteButton.isChecked()) {
                createQuoteButton.setChecked(false);
            }
        });

        designWallpaperButton.setOnClickListener(v -> {
            if (designWallpaperButton.isChecked()) {
                designWallpaperButton.setChecked(false);
                bottomNavigationView.setSelectedItemId(R.id.design);
            }
        });

        scrollViewScrollChange = new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY != 0) {
                    getActivity().getWindow().setStatusBarColor(statusBarScrolledColor);
                    getActivity().getWindow().findViewById(R.id.appBarLayout).setBackgroundColor(statusBarScrolledColor);
                } else {
                    getActivity().getWindow().setStatusBarColor(statusBarDefaultColor);
                    getActivity().getWindow().findViewById(R.id.appBarLayout).setBackgroundColor(statusBarDefaultColor);
                }
            }
        };

        scrollView.setOnScrollChangeListener(scrollViewScrollChange);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getWindow().setStatusBarColor(statusBarDefaultColor);
        getActivity().getWindow().findViewById(R.id.appBarLayout).setBackgroundColor(statusBarDefaultColor);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scrollView.getScrollY() != 0) {
            getActivity().getWindow().setStatusBarColor(statusBarScrolledColor);
            getActivity().getWindow().findViewById(R.id.appBarLayout).setBackgroundColor(statusBarScrolledColor);
        }
    }
}