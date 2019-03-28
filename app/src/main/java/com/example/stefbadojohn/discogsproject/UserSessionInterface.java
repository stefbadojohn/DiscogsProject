package com.example.stefbadojohn.discogsproject;

public interface UserSessionInterface {
    boolean isLoggedIn();
    String getUserToken();
    String getUserTokenSecret();
    String getUsername(); //TODO: Add this! :P
    void saveUserToken(String userToken, String userTokenSecret);
    void login();
    void logout();
}
