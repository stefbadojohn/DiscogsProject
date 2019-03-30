package com.example.stefbadojohn.discogsproject;

import android.app.Application;
import android.content.Context;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class MainApplication extends Application {
    private static WeakReference<Context> context;

    public void onCreate() {
        super.onCreate();
        MainApplication.context = new WeakReference<>(getApplicationContext());
    }

    @Nullable
    public static Context getAppContext() {
        return MainApplication.context.get();
    }
}
