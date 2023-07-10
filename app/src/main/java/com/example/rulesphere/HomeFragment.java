package com.example.rulesphere;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

public class HomeFragment extends Fragment {

    private MyRulesFragment myRulesFragment;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(MyRulesFragment myRulesFragment) {
        HomeFragment fragment = new HomeFragment();

        fragment.myRulesFragment = myRulesFragment;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    MaterialCardView materialCardView;
    MaterialButton favoriteButton, createQuoteButton, designWallpaperButton,
    copyQuoteButton, saveQuoteButton;
    TextView ruleOfDayDay, ruleOfDayQuote, ruleOfDayAuthor, profileNameText;
    ScrollView scrollView;
    View.OnScrollChangeListener scrollViewScrollChange;
    int statusBarDefaultColor, statusBarScrolledColor;
    boolean isNightMode;
    FrameLayout shareBottomSheet, profileBottomSheet;
    ExtendedFloatingActionButton shareFAB;
    QuoteDao quoteDao;
    int dayOfYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        quoteDao = getDb().quoteDao();
        dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        materialCardView = view.findViewById(R.id.ruleCard);
        ruleOfDayDay = view.findViewById(R.id.ruleOfDayDay);
        ruleOfDayQuote = view.findViewById(R.id.ruleOfDayQuote);
        ruleOfDayAuthor = view.findViewById(R.id.ruleOfDayAuthor);
        favoriteButton = view.findViewById(R.id.favoriteButton);
        createQuoteButton = view.findViewById(R.id.createQuoteButton);
        designWallpaperButton = view.findViewById(R.id.designWallpaperButton);
        scrollView = view.findViewById(R.id.homeScrolLView);
        profileNameText = view.findViewById(R.id.profileNameText);

        isNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        copyQuoteButton = view.findViewById(R.id.copyQuote);
        saveQuoteButton = view.findViewById(R.id.saveQuote);
        shareFAB = view.findViewById(R.id.extended_fab_share);

        shareBottomSheet = view.findViewById(R.id.shareBottomSheet);
        if (shareBottomSheet.getParent() != null) {
            ((ViewGroup) shareBottomSheet.getParent()).removeView(shareBottomSheet);
        }
        BottomSheetDialog shareBottomSheetDialog = new BottomSheetDialog(getContext());
        shareBottomSheetDialog.setContentView(shareBottomSheet);

        profileBottomSheet = view.findViewById(R.id.profileBottomSheet);
        if (profileBottomSheet.getParent() != null) {
            ((ViewGroup) profileBottomSheet.getParent()).removeView(profileBottomSheet);
        }
        BottomSheetDialog profileBottomSheetDialog = new BottomSheetDialog(getContext());
        profileBottomSheetDialog.setContentView(profileBottomSheet);

        if (isNightMode) {
            statusBarDefaultColor = getContext().getColor(R.color.md_theme_dark_background);
            statusBarScrolledColor = getContext().getColor(R.color.md_theme_dark_scrolled);
        } else {
            statusBarDefaultColor = getContext().getColor(R.color.md_theme_light_background);
            statusBarScrolledColor = getContext().getColor(R.color.md_theme_light_scrolled);
        }

        String profileName = SharedPreferencesManager.getProfileName("");
        if (profileName.isEmpty()) {
            profileNameText.setText("Guest.");
        } else {
            profileName = profileName + ".";
            profileNameText.setText(profileName);
        }

        //SET TODAY'S QUOTE
        setRuleOFDay();

        favoriteButton.setOnClickListener(v -> {
            //Na favorite button se zove .setChecked() iako ne treba
            toggleCard();

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Quote quote = quoteDao.getQuoteAtDay(dayOfYear);

                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.favoriteAQuote(quote.id + "");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myRulesFragment.updateFavoritesPersonal(true, null, null);
                        }
                    });
                }
            });

            Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.like_anim);
            favoriteButton.startAnimation(animation);
        });

        materialCardView.setOnClickListener(new View.OnClickListener() {
            boolean isDoubleClick = false;
            Handler handler = new Handler();
            final int DOUBLE_CLICK_DELAY = 300;

            @Override
            public void onClick(View view) {
                if (!isDoubleClick) {
                    isDoubleClick = true;
                    handler.postDelayed(() -> {
                        isDoubleClick = false;
                    }, DOUBLE_CLICK_DELAY);
                } else {
                    favoriteButton.performClick();
                }
            }
        });

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);

        createQuoteButton.setOnClickListener(v -> {
            if (createQuoteButton.isChecked()) {

                //Do smth
            }
        });

        designWallpaperButton.setOnClickListener(v -> {
            if (designWallpaperButton.isChecked()) {
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

        shareFAB.setOnClickListener(v -> {
            shareBottomSheetDialog.show();
        });

        copyQuoteButton.setOnClickListener(v -> {
            String text = "Rule nr. " + ruleOfDayDay.getText() + "\n" +
                    ruleOfDayQuote.getText() + "\n" +
                    ruleOfDayAuthor.getText();

            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getContext(), "Rule copied to clipboard", Toast.LENGTH_SHORT).show();

            shareBottomSheetDialog.hide();
        });

        saveQuoteButton.setOnClickListener(v -> {
            saveViewToJpeg(view.findViewById(R.id.ruleCard));

            shareBottomSheetDialog.hide();
        });

        MaterialToolbar topAppBar = getActivity().findViewById(R.id.topAppBar);
        topAppBar.findViewById(R.id.profile).setOnClickListener(v -> {
            Window window = profileBottomSheetDialog.getWindow();
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            }

            profileBottomSheetDialog.show();
        });

        profileBottomSheet.findViewById(R.id.updateProfileName).setOnClickListener(v -> {
            TextInputLayout profileNameInput = profileBottomSheet.findViewById(R.id.profileNameInput);
            String profileNameTemp = profileNameInput.getEditText().getText().toString();
            SharedPreferencesManager.putProfileName(profileNameTemp);
            profileNameTemp = profileNameTemp + ".";
            profileNameText.setText(profileNameTemp);
            profileBottomSheetDialog.hide();
            profileNameInput.getEditText().getText().clear();
        });

        profileBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                TextInputLayout profileNameInput = profileBottomSheet.findViewById(R.id.profileNameInput);
                profileNameInput.clearFocus();
            }
        });

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            getActivity().getWindow().setStatusBarColor(statusBarDefaultColor);
            getActivity().getWindow().findViewById(R.id.appBarLayout).setBackgroundColor(statusBarDefaultColor);
            scrollView.setScrollY(0);
        } else {
            int dayNow = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            if (dayOfYear != dayNow) {
                dayOfYear = dayNow;
                setRuleOFDay();
            }
        }
    }

    public RuleSphereDatabase getDb() {
        return RuleSphereDatabase.getInstance(getContext());
    }

    public void toggleCard() {
        materialCardView.toggle();
        favoriteButton.setChecked(materialCardView.isChecked());

        if (materialCardView.isChecked()) {
            materialCardView.setStrokeWidth(5);
            favoriteButton.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            materialCardView.setCardForegroundColor(null);
        } else {
            materialCardView.setStrokeWidth(0);
        }
    }

    private void saveViewToJpeg(View view) {
        // Create a Bitmap with the same dimensions as the view
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a Canvas and associate it with the Bitmap
        Canvas canvas = new Canvas(bitmap);

        // Draw the view onto the Canvas
        view.draw(canvas);

        // Generate a unique filename
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String fileName = "rule_nr_" + ruleOfDayDay.getText() + "_" + year + ".png";

        // Get the content resolver
        ContentResolver resolver = getContext().getContentResolver();

        // Create the content values for the image
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");

        // Insert the image into MediaStore and get the URI
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            // Open an output stream using the URI
            OutputStream outputStream = resolver.openOutputStream(imageUri);

            if (outputStream != null) {
                // Compress the bitmap as JPEG with quality 100 and write it to the output stream
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);

                // Close the output stream
                outputStream.close();

                Toast.makeText(getContext(), "Rule saved", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRuleOFDay() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Quote quote = quoteDao.getQuoteAtDay(dayOfYear);

                if (quote == null) {
                    quote = quoteDao.getRandomQuote();

                    quote.dayUsed = dayOfYear;
                    quoteDao.insert(quote);
                }

                ruleOfDayDay.setText(String.valueOf(dayOfYear));
                ruleOfDayQuote.setText(quote.quote);
                String author = "- " + quote.author;
                ruleOfDayAuthor.setText(author);

                if (quote.isFavorite) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleCard();
                        }
                    });
                }
            }
        });
    }
}