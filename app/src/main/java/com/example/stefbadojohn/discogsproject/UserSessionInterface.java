package com.example.stefbadojohn.discogsproject;

public interface UserSessionInterface {
    boolean getIsLoggedIn();
    String getUserToken();
    String getUserTokenSecret();
    void saveUserToken(String userToken, String userTokenSecret);
    void login();
    void logout();
}
