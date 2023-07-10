package com.example.rulesphere;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.platform.MaterialContainerTransform;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyRulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyRulesFragment extends Fragment {
    RecyclerView recyclerView;
    QuoteDao quoteDao;

    public MyRulesFragment() {
        // Required empty public constructor
    }

    public static MyRulesFragment newInstance(String param1, String param2) {
        MyRulesFragment fragment = new MyRulesFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rules, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        quoteDao = getDb().quoteDao();

        MaterialButton myRulesFavorites = view.findViewById(R.id.myRulesFavorites);
        MaterialButton myRulesQuotes = view.findViewById(R.id.myRulesQuotes);

        recyclerView = view.findViewById(R.id.recycler_view_myRules);
        updateFavoritesPersonal(true, null, null);

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
                        materialButton.setBackgroundTintList(ColorStateList.valueOf(getColorFromResource(getContext(), com.google.android.material.R.attr.colorPrimary)));
                        materialButton.setTextColor(getColorFromResource(getContext(), com.google.android.material.R.attr.colorOnPrimary));
                        materialButton.setIconTint(ColorStateList.valueOf(getColorFromResource(getContext(), com.google.android.material.R.attr.colorOnPrimary)));

                        if (checkedId == myRulesFavorites.getId()) {
                            updateFavoritesPersonal(true, null, null);
                            addQuoteFab.hide();
                        } else {
                            updateFavoritesPersonal(false, null, null);
                            addQuoteFab.show();
                        }

                        continue;
                    }

                    materialButton.setBackgroundTintList(ColorStateList.valueOf(getColorFromResource(getContext(), com.google.android.material.R.attr.colorSurfaceVariant)));
                    materialButton.setTextColor(getColorFromResource(getContext(), com.google.android.material.R.attr.colorOnSurfaceVariant));
                    materialButton.setIconTint(ColorStateList.valueOf(getColorFromResource(getContext(), com.google.android.material.R.attr.colorOnSurfaceVariant)));
                }
            }
        });

        materialButtonToggleGroup.check(myRulesFavorites.getId());

        View addRuleView = inflater.inflate(R.layout.add_rule_view, container, false);
        //addRuleView.setVisibility(View.GONE);
        FrameLayout addRuleFrameLayout = requireActivity().findViewById(R.id.addRuleFrameLayout);
        addRuleFrameLayout.addView(addRuleView);

        MaterialContainerTransform containerTransformToView = new MaterialContainerTransform();
        containerTransformToView.setDuration(500);
        containerTransformToView.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
        containerTransformToView.setStartView(addQuoteFab);
        containerTransformToView.setEndView(addRuleFrameLayout);
        containerTransformToView.addTarget(addRuleFrameLayout);
        //containerTransformToView.setPathMotion(new ArcMotion());
        containerTransformToView.setScrimColor(Color.TRANSPARENT);

        MaterialContainerTransform containerTransformFromView = new MaterialContainerTransform();
        containerTransformFromView.setDuration(400);
        containerTransformToView.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
        containerTransformFromView.setStartView(addRuleFrameLayout);
        containerTransformFromView.setEndView(addQuoteFab);
        containerTransformFromView.addTarget(addQuoteFab);
        //containerTransformFromView.setPathMotion(new ArcMotion());
        containerTransformFromView.setScrimColor(Color.TRANSPARENT);

        addQuoteFab.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(container, containerTransformToView);
            addQuoteFab.setVisibility(View.GONE);
            addRuleFrameLayout.setVisibility(View.VISIBLE);
        });

        MaterialButton closeNewRuleViewButton = addRuleView.findViewById(R.id.closeNewRuleView);

        closeNewRuleViewButton.setOnClickListener(v -> {
            View view1 = getActivity().getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }

            TransitionManager.beginDelayedTransition(container, containerTransformFromView);
            addQuoteFab.setVisibility(View.VISIBLE);
            addRuleFrameLayout.setVisibility(View.GONE);
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
                    button.setBackgroundTintList(ColorStateList.valueOf(getColorFromResource(getContext(), com.google.android.material.R.attr.colorPrimary)));
                    button.setTextColor(getColorFromResource(getContext(), com.google.android.material.R.attr.colorOnPrimary));
                    button.setStrokeColor(ColorStateList.valueOf(Color.TRANSPARENT));
                } else {
                    button.setBackgroundTintList(ColorStateList.valueOf(getColorFromResource(getContext(), com.google.android.material.R.attr.colorSurfaceVariant)));
                    button.setTextColor(getColorFromResource(getContext(), com.google.android.material.R.attr.colorOnSurfaceVariant));
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

        TextInputLayout ruleInput = addRuleView.findViewById(R.id.ruleInput);
        TextInputLayout ruleAuthorInput = addRuleView.findViewById(R.id.ruleAuthorInput);
        MaterialButton addRuleButton = addRuleView.findViewById(R.id.addRule);

        addRuleButton.setOnClickListener(v -> {
            if (ruleInput.getEditText().getText().toString().isEmpty()) {
                ruleInput.setError("Please enter a Rule");
                return;
            }

            Quote quote = new Quote();
            quote.quote = ruleInput.getEditText().getText().toString();
            quote.author = ruleAuthorInput.getEditText().getText().toString();
            quote.category = pickedCategoryWrapper.getValue();
            quote.isPersonal = true;

            if (quote.author.equals("")) {
                quote.author = "Unknown";
            }

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    quoteDao.insert(quote);
                }
            });

            updateFavoritesPersonal(false, -1, true);
            TransitionManager.beginDelayedTransition(container, containerTransformFromView);
            addQuoteFab.setVisibility(View.VISIBLE);
            addRuleFrameLayout.setVisibility(View.GONE);

            ruleInput.getEditText().getText().clear();
            ruleAuthorInput.getEditText().getText().clear();

            for (int i = 0; i < filterChipGroup.getChildCount(); i++) {
                MaterialButton materialButton = (MaterialButton) filterChipGroup.getChildAt(i);
                materialButton.setChecked(false);
            }
        });

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

    public RuleSphereDatabase getDb() {
        return RuleSphereDatabase.getInstance(getContext());
    }

    CardAdapter recyclerViewAdapter;

    public void updateFavoritesPersonal(boolean trueFavoritesFalsePersonal, Integer elementPosition, Boolean trueAddedFalseRemoved) {
        AsyncTask.execute(() -> {
            List <Quote> quotes;
            if (trueFavoritesFalsePersonal) {
                quotes = quoteDao.getFavorites();
            } else {
                quotes = quoteDao.getPersonal();
            }

            requireActivity().runOnUiThread(() -> {
                if (recyclerViewAdapter == null) {
                    recyclerViewAdapter = new CardAdapter(quotes, (MainActivity) getActivity(), recyclerView.getId());
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }

                recyclerViewAdapter.setQuotes(quotes);
                if (elementPosition == null) {
                    recyclerViewAdapter.notifyDataSetChanged(); // Ignore the warning, I'm changing the whole dataset
                } else if (elementPosition == -1 && trueAddedFalseRemoved) {
                  recyclerViewAdapter.notifyItemInserted(recyclerViewAdapter.getItemCount());
                } else {
                    if (trueAddedFalseRemoved) {
                        recyclerViewAdapter.notifyItemInserted(elementPosition);
                    } else {
                        recyclerViewAdapter.notifyItemRemoved(elementPosition);
                    }
                }
            });
        });
    }
}