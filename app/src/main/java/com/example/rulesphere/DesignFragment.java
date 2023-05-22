package com.example.rulesphere;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DesignFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DesignFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DesignFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DesignFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DesignFragment newInstance(String param1, String param2) {
        DesignFragment fragment = new DesignFragment();
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
        View view = inflater.inflate(R.layout.fragment_design, container, false);

        int wallpaperColor1 = R.color.wallpaperColor1;
        int wallpaperColor2 = R.color.wallpaperColor2;
        int wallpaperColor3 = R.color.wallpaperColor3;

        MaterialCardView wallpaper = view.findViewById(R.id.wallpaper);
        MaterialButton wallpaperColor1Button = view.findViewById(R.id.wallpaperColor1);
        MaterialButton wallpaperColor2Button = view.findViewById(R.id.wallpaperColor2);
        MaterialButton wallpaperColor3Button = view.findViewById(R.id.wallpaperColor3);

        View.OnClickListener wallpaperColorChange = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialButton materialButton = (MaterialButton) view;
                String buttonId = getResources().getResourceName(materialButton.getId());
                char buttonIdNumber = buttonId.charAt(buttonId.length() - 1);

                int color = 0;
                switch (buttonIdNumber) {
                    case '1':
                        color = wallpaperColor1;
                        break;
                    case '2':
                        color = wallpaperColor2;
                        break;
                    case '3':
                        color = wallpaperColor3;
                        break;
                }

                if (color != 0)
                    wallpaper.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color)));
            }
        };

        wallpaperColor1Button.setOnClickListener(wallpaperColorChange);
        wallpaperColor2Button.setOnClickListener(wallpaperColorChange);
        wallpaperColor3Button.setOnClickListener(wallpaperColorChange);

        return view;
    }
}