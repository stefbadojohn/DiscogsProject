package com.example.stefbadojohn.discogsproject;

import com.google.gson.annotations.SerializedName;

class DiscogsIdentity {
    private String id;
    private String username;
    @SerializedName("resource_url")
    private String resourceUrl;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }
}
