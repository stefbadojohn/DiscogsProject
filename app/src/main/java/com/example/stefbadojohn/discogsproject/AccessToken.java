package com.example.stefbadojohn.discogsproject;

class AccessToken {
    private String oauthToken;

    private String oauthTokenSecret;

    public String getOauthToken() {
        return oauthToken;
    }

    public String getOauthTokenSecret() {
        return oauthTokenSecret;
    }

    public void setOauthTokenSecret(String oauthTokenSecret) {
        this.oauthTokenSecret = oauthTokenSecret;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }
}
