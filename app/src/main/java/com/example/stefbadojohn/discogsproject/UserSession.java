package com.example.stefbadojohn.discogsproject;

import android.content.Context;

public class UserSession implements UserSessionInterface {
    private CredentialManagerInterface credManager;

    public UserSession(Context context) {
        credManager = new CredentialManager(context);
    }

    @Override
    public boolean isLoggedIn() {
        if ((credManager.getUserToken() != null) && (credManager.getUserTokenSecret() != null)) {
            return true;
        } else {
            return false;
        }
    }

    //TODO: Replace getUserToken/Secret with -> AccessToken getAccessToken()

    @Override
    public String getUserToken() {
        return credManager.getUserToken();
    }

    @Override
    public String getUserTokenSecret() {
        return credManager.getUserTokenSecret();
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public void saveUserToken(String userToken, String userTokenSecret) {
        credManager.setUserToken(userToken);
        credManager.setUserTokenSecret(userTokenSecret);
    }

    @Override
    public void login() {
        //TODO: Login (OAuth)
    }

    @Override
    public void logout() {
        credManager.clearUserToken();
    }

}
