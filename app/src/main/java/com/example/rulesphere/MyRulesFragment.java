package com.example.rulesphere;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

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
        RecyclerView rv = view.findViewById(R.id.recycler_view_myRules);
        mainActivity.updateMyRulesList(rv);

        boolean isNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        MaterialButtonToggleGroup materialButtonToggleGroup = view.findViewById(R.id.myRulesCategory);

        materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                for (int i = 0; i < materialButtonToggleGroup.getChildCount(); i++) {
                    MaterialButton materialButton = (MaterialButton) materialButtonToggleGroup.getChildAt(i);

                    if (materialButtonToggleGroup.getCheckedButtonId() == materialButton.getId()) {
                        if (isNightMode) {
                            materialButton.setBackgroundColor(getContext().getColor(R.color.md_theme_light_primary));
                        } else {
                            materialButton.setBackgroundColor(getContext().getColor(com.google.android.material.R.color.material_dynamic_primary80));
                        }

                        continue;
                    }

                    if (isNightMode) {
                        materialButton.setBackgroundColor(getContext().getColor(R.color.md_theme_dark_secondaryContainer));
                    } else {
                        materialButton.setBackgroundColor(getContext().getColor(R.color.md_theme_light_secondaryContainer));
                    }
                }
            }
        });

        materialButtonToggleGroup.check(materialButtonToggleGroup.getChildAt(0).getId());

        return view;
    }
}