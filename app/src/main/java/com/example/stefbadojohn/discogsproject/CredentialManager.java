package com.example.stefbadojohn.discogsproject;

import android.content.Context;
import android.content.SharedPreferences;

public class CredentialManager implements CredentialManagerInterface {
    private Context context;

    public CredentialManager(Context context) {
        this.context = context;
    }

    @Override
    public String getConsumerKey() {
        return "JoFTMKwVGpTTMyDZANrv";
    }

    @Override
    public String getConsumerSecret() {
        return "ppxHeEWXFNVHquzNQFSylDLaGKDWzdlO";
    }

    @Override
    public String getUserToken() {
        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        return pref.getString("userToken", null);
    }

    @Override
    public String getUserTokenSecret() {
        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        return pref.getString("userTokenSecret", null);
    }

    @Override
    public void setUserToken(String userToken) {
        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userToken", userToken);
        editor.apply();
    }

    @Override
    public void setUserTokenSecret(String userTokenSecret) {
        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userTokenSecret", userTokenSecret);
        editor.apply();
    }

    @Override
    public void clearUserToken() {
        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
