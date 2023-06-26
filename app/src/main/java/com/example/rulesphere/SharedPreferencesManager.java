package com.example.rulesphere;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "MyPreferences";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private SharedPreferencesManager() {
        // Private constructor to prevent instantiation
    }

    public static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    public static void putQuote(String value) {
        editor.putString("quoteForDesign", value);
        editor.apply();
    }
    public static void putAuthor(String value) {
        editor.putString("authorForDesign", value);
        editor.apply();
    }

    public static String getQuote(String defaultValue) {
        return sharedPreferences.getString("quoteForDesign", defaultValue);
    }
    public static String getAuthor(String defaultValue) {
        return sharedPreferences.getString("authorForDesign", defaultValue);
    }

    public static void putProfileName(String value) {
        editor.putString("profileName", value);
        editor.apply();
    }
    public static String getProfileName(String defaultValue) {
        return sharedPreferences.getString("profileName", defaultValue);
    }

    public static void clearSharedPreferences() {
        editor.clear();
    }
}
