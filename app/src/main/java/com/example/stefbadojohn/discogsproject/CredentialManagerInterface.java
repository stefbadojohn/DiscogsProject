package com.example.stefbadojohn.discogsproject;

public interface CredentialManagerInterface {
    String getConsumerKey();
    String getConsumerSecret();
    String getUserToken();
    String getUserTokenSecret();
    void setUserToken(String userToken);
    void setUserTokenSecret(String userTokenSecret);
    void clearUserToken();

    static CredentialManagerInterface instance = new InsecureCredentialManager();
}
