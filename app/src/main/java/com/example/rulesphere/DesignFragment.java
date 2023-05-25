package com.example.rulesphere;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_design, container, false);

        MaterialCardView wallpaper = view.findViewById(R.id.wallpaper);
        MaterialButton colorPalette = view.findViewById(R.id.colorPalette);
        MaterialButton colorPicker = view.findViewById(R.id.colorPicker);
        TextView wallpaperColorHex = view.findViewById(R.id.wallpaperColorHex);

        FrameLayout colorBottomSheet = view.findViewById(R.id.colorBottomSheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(colorBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        colorPalette.setOnClickListener(v -> {
            GridLayout colorBottomSheetGrid = view.findViewById(R.id.colorBottomSheetGrid);
            int[] colorArray = getResources().getIntArray(R.array.wallpaperColors);
            for (int i = 0; i < colorBottomSheetGrid.getChildCount(); i++) {
                MaterialButton colorButton = (MaterialButton) colorBottomSheetGrid.getChildAt(i);
                colorButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(android.R.color.transparent)));
                colorButton.setStrokeColor(ColorStateList.valueOf(colorArray[i]));

                colorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialButton colorButton = (MaterialButton) view;
                        int color = colorButton.getStrokeColor().getDefaultColor();

                        for (int i = 0; i < colorBottomSheetGrid.getChildCount(); i++) {
                            MaterialButton colorButton2 = (MaterialButton) colorBottomSheetGrid.getChildAt(i);

                            colorButton2.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(android.R.color.transparent)));
                            colorButton2.setStrokeColor(ColorStateList.valueOf(colorArray[i]));
                        }

                        colorButton.setBackgroundTintList(ColorStateList.valueOf(color));
                        colorButton.setStrokeColor(ColorStateList.valueOf(getContext().getColor(android.R.color.transparent)));

                        wallpaper.setBackgroundTintList(ColorStateList.valueOf(color));
                        wallpaperColorHex.setText(String.format("#%06X", (0xFFFFFF & color)));

                        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.like_anim);
                        colorButton.startAnimation(animation);
                    }
                });
            }

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        colorPicker.setOnClickListener(v -> {
            View colorWheelView = LayoutInflater.from(getActivity()).inflate(R.layout.color_wheel, null);
            ImageView colorWheelImage = colorWheelView.findViewById(R.id.colorWheelImage);
            MaterialButton currentColorButton = colorWheelView.findViewById(R.id.currentColorColorWheel);
            int currentColorButtonStrokeColor = currentColorButton.getStrokeColor().getDefaultColor();
            ImageView colorWheelPointer = colorWheelView.findViewById(R.id.colorWheelPointer);

            colorWheelImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        float density = getResources().getDisplayMetrics().density;
                        int imageViewWidth = view.getWidth(); // Get the width of the ImageView
                        int imageViewHeight = view.getHeight(); // Get the height of the ImageView

                        int imageWidth = colorWheelImage.getWidth(); // Get the width of the actual image
                        int imageHeight = colorWheelImage.getHeight(); // Get the height of the actual image

                        float scaleFactorX = (float) imageWidth / imageViewWidth * density;
                        float scaleFactorY = (float) imageHeight / imageViewHeight * density;

                        int x = (int) (motionEvent.getX() * scaleFactorX);
                        int y = (int) (motionEvent.getY() * scaleFactorY);

                        int color = getPixelColor(colorWheelImage, x, y);
                        //paintPixelBlack(colorWheelImage, x, y);
                        //paintColorPixels(colorWheelImage, x , y);

                        colorWheelPointer.setX(motionEvent.getX() + (float) colorWheelPointer.getWidth() / 2);
                        colorWheelPointer.setY(motionEvent.getY() + (float) colorWheelPointer.getHeight() / 2);

                        if (color != 0) {
                            currentColorButton.setBackgroundTintList(ColorStateList.valueOf(color));
                            currentColorButton.setStrokeColor(ColorStateList.valueOf(getContext().getColor(android.R.color.transparent)));
                        }
                        else {
                            currentColorButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(android.R.color.transparent)));
                            currentColorButton.setStrokeColor(ColorStateList.valueOf(currentColorButtonStrokeColor));
                        }
                        return true;
                    }
                    return false;
                }
            });

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(getActivity())
                    .setTitle("Pick Color")
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int color = currentColorButton.getBackgroundTintList().getDefaultColor();
                            wallpaper.setBackgroundTintList(ColorStateList.valueOf(color));
                            wallpaperColorHex.setText(String.format("#%06X", (0xFFFFFF & color)));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setView(colorWheelView)
                    .show();
        });

        return view;
    }

    private int getPixelColor(ImageView imageView, int x, int y) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        // Make sure the coordinates are within the image bounds
        if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight()) {
            return 0;
        }

        int pixel = bitmap.getPixel(x, y);

//        int red = Color.red(pixel);
//        int green = Color.green(pixel);
//        int blue = Color.blue(pixel);
//        int alpha = Color.alpha(pixel);

        if (pixel == 0) {
            return findClosestNonZeroColor(bitmap, x, y);
        }

//        Log.d("PixelColor", "Red: " + red + ", Green: " + green + ", Blue: " + blue + ", Alpha: " + alpha);
        return pixel;
    }

    private int findClosestNonZeroColor(Bitmap bitmap, int x, int y) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int maxDistance = Math.max(width, height);

        for (int distance = 1; distance <= maxDistance; distance++) {
            for (int i = -distance; i <= distance; i++) {
                for (int j = -distance; j <= distance; j++) {
                    int newX = x + i;
                    int newY = y + j;

                    if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                        int color = bitmap.getPixel(newX, newY);
                        if (Color.alpha(color) != 0) {
                            return color;
                        }
                    }
                }
            }

            if (distance >= 30) {
                break;
            }
        }

        // No non-zero color found, return transparent
        return Color.TRANSPARENT;
    }

    private void paintPixelBlack(ImageView imageView, int x, int y) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        // Make sure the coordinates are within the image bounds
        if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight()) {
            return;
        }

        // Create a mutable bitmap to modify the pixel colors
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        int left = Math.max(x - 20, 0);
        int top = Math.max(y - 20, 0);
        int right = Math.min(x + 20, mutableBitmap.getWidth() - 1);
        int bottom = Math.min(y + 20, mutableBitmap.getHeight() - 1);

        for (int i = left; i <= right; i++) {
            for (int j = top; j <= bottom; j++) {
                // Set the pixel color to black
                mutableBitmap.setPixel(i, j, Color.BLACK);
            }
        }

        // Set the modified bitmap to the ImageView
        imageView.setImageBitmap(mutableBitmap);
    }
}