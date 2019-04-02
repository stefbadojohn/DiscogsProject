package com.example.stefbadojohn.discogsproject;

public interface UserSessionInterface {
    boolean isLoggedIn();
    String getUserToken();
    String getUserTokenSecret();
    String getUsername();

    void setUsername(String username);
    void saveUserToken(String userToken, String userTokenSecret);
    void login();
    void logout();

    static UserSessionInterface instance = new UserSession();
}
