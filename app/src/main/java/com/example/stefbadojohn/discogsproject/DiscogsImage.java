package com.example.stefbadojohn.discogsproject;

import com.google.gson.annotations.SerializedName;

public class DiscogsImage {

    @SerializedName("uri")
    private String imageUri;

    @SerializedName("resource_url")
    private String resourceUrl;

    private int height;
    private int width;

    public String getImageUrl() {
        return imageUri;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
