package com.example.rulesphere;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    MaterialButton favoriteButton, createQuoteButton, designWallpaperButton,
    copyQuoteButton, saveQuoteButton;
    TextView ruleOfDayDay, ruleOfDayQuote, ruleOfDayAuthor, profileNameText;
    ScrollView scrollView;
    View.OnScrollChangeListener scrollViewScrollChange;
    int statusBarDefaultColor, statusBarScrolledColor;
    boolean isNightMode;
    FrameLayout shareBottomSheet, profileBottomSheet;
    ExtendedFloatingActionButton shareFAB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        String profileName = SharedPreferencesManager.getProfileName(new String());
        if (profileName.isEmpty()) {
            profileNameText.setText("Guest.");
        } else {
            profileName = profileName + ".";
            profileNameText.setText(profileName);
        }

        //SET TODAY'S QUOTE
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        QuoteDao quoteDao = getDb().quoteDao();
        AtomicReference<Quote> quoteRef = new AtomicReference<>();

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
                            ToggleCard();
                        }
                    });
                }
            }
        });
        //SET TODAY'S QUOTE

        materialCardView.setCheckedIcon(null);
        materialCardView.setStrokeWidth(0);

        favoriteButton.setOnClickListener(v -> {
            //Na favorite button se zove .setChecked() iako ne treba
            ToggleCard();

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
                    ToggleCard();

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Quote quote = quoteDao.getQuoteAtDay(dayOfYear);

                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.favoriteAQuote(quote.id + "");
                        }
                    });


                    Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.like_anim);
                    favoriteButton.startAnimation(animation);
                }
            }
        });

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);

        createQuoteButton.setOnClickListener(v -> {
            if (createQuoteButton.isChecked()) {
                createQuoteButton.setChecked(false);

                //Do smth
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

    public RuleSphereDatabase getDb() {
        return RuleSphereDatabase.getInstance(getContext());
    }

    private void ToggleCard() {
        materialCardView.toggle();
        favoriteButton.setChecked(materialCardView.isChecked());

        if (materialCardView.isChecked()) {
            materialCardView.setStrokeWidth(5);
            favoriteButton.setBackground(null);
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
}