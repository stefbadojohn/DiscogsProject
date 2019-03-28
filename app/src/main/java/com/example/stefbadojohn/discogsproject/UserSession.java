package com.example.stefbadojohn.discogsproject;

import android.content.Context;
import android.support.annotation.Nullable;

public class UserSession implements UserSessionInterface {
    private CredentialManagerInterface credManager = CredentialManagerInterface.instance;

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
    @Nullable
    public String getUserToken() {
        return credManager.getUserToken();
    }

    @Override
    @Nullable
    public String getUserTokenSecret() {
        return credManager.getUserTokenSecret();
    }

    @Override
    @Nullable
    public String getUsername() {
        return null;
        //TODO: Add this! :P
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
