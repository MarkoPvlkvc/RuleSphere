package com.example.rulesphere;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyRulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyRulesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyRulesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyRulesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyRulesFragment newInstance(String param1, String param2) {
        MyRulesFragment fragment = new MyRulesFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rules, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();

        MaterialButton myRulesFavorites = view.findViewById(R.id.myRulesFavorites);
        MaterialButton myRulesQuotes = view.findViewById(R.id.myRulesQuotes);

        RecyclerView rv = view.findViewById(R.id.recycler_view_myRules);
        FloatingActionButton addQuoteFab = view.findViewById(R.id.fab_addQuote);

        boolean isNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        MaterialButtonToggleGroup materialButtonToggleGroup = view.findViewById(R.id.myRulesCategory);

        materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (!isChecked) {
                    // Button was unchecked, do nothing
                    return;
                }

                for (int i = 0; i < group.getChildCount(); i++) {
                    MaterialButton materialButton = (MaterialButton) group.getChildAt(i);

                    if (checkedId == materialButton.getId()) {
                        materialButton.setBackgroundTintList(ColorStateList.valueOf(getColorFromResource(getContext(), com.google.android.material.R.attr.colorPrimaryContainer)));

                        if (checkedId == myRulesFavorites.getId()) {
                            mainActivity.updateMyRulesList(rv);
                            addQuoteFab.hide();
                        } else {
                            mainActivity.updateMyQuotesList(rv);
                            addQuoteFab.show();
                        }

                        continue;
                    }

                    materialButton.setBackgroundTintList(ColorStateList.valueOf(getColorFromResource(getContext(), com.google.android.material.R.attr.colorSurfaceVariant)));
                }
            }
        });

        materialButtonToggleGroup.check(myRulesFavorites.getId());

        View addRuleView = inflater.inflate(R.layout.add_rule_view, container, false);
        FrameLayout addRuleFrameLayout = getActivity().findViewById(R.id.addRuleFrameLayout);
        addQuoteFab.setOnClickListener(v -> {
            addRuleFrameLayout.addView(addRuleView);
        });

        MaterialButton closeNewRuleViewButton = addRuleView.findViewById(R.id.closeNewRuleView);
        closeNewRuleViewButton.setOnClickListener(v -> {
            addRuleFrameLayout.removeView(addRuleView);
        });

        class MutableString {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        ChipGroup filterChipGroup = addRuleView.findViewById(R.id.filterChipGroupNewRule);
        MutableString pickedCategoryWrapper = new MutableString();

        MaterialButton.OnCheckedChangeListener filterOnChecked = new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                if (isChecked) {
                    button.setBackgroundTintList(ColorStateList.valueOf(getColorFromResource(getContext(), com.google.android.material.R.attr.colorPrimaryContainer)));
                    button.setStrokeColor(ColorStateList.valueOf(getContext().getColor(android.R.color.transparent)));
                } else {
                    button.setBackgroundTintList(ColorStateList.valueOf(getColorFromResource(getContext(), com.google.android.material.R.attr.colorSurfaceVariant)));
                    button.setStrokeColor(ColorStateList.valueOf(getColorFromResource(getContext(), com.google.android.material.R.attr.colorOutline)));
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
                        pickedCategoryWrapper.setValue((String) materialButton.getText());
                        somethingChecked = true;
                        continue;
                    }

                    materialButton.setChecked(false);
                }

                if (!somethingChecked) {
                    pickedCategoryWrapper.setValue("");
                }
            }
        };

        for (int i = 0; i < filterChipGroup.getChildCount(); i++) {
            MaterialButton materialButton = (MaterialButton) filterChipGroup.getChildAt(i);
            materialButton.addOnCheckedChangeListener(filterOnChecked);
            materialButton.setOnClickListener(filterOnClick);
        }

        return view;
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