package com.example.stefbadojohn.discogsproject;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import static com.example.stefbadojohn.discogsproject.MainApplication.getAppContext;

public class InsecureCredentialManager implements CredentialManagerInterface {

    @Override
    public String getConsumerKey() {
        return "key";
    }

    @Override
    public String getConsumerSecret() {
        return "secret";
    }

    @Override
    @Nullable
    public String getUserToken() {
        Context context = getAppContext();
        if (context == null) {
            return null;
        }

        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        return pref.getString("userToken", null);
    }

    @Override
    @Nullable
    public String getUserTokenSecret() {
        Context context = getAppContext();
        if (context == null) {
            return null;
        }

        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        return pref.getString("userTokenSecret", null);
    }

    @Override
    public void setUserToken(String userToken) {
        Context context = getAppContext();
        if (context == null) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userToken", userToken);
        editor.apply();
    }

    @Override
    public void setUserTokenSecret(String userTokenSecret) {
        Context context = getAppContext();
        if (context == null) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userTokenSecret", userTokenSecret);
        editor.apply();
    }

    @Override
    public void clearUserToken() {
        Context context = getAppContext();
        if (context == null) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
