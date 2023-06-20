package com.example.rulesphere;

import android.app.Application;

public class RuleSphereApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferencesManager.init(getApplicationContext());
    }
}
