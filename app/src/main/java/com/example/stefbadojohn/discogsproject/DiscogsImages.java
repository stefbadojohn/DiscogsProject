package com.example.stefbadojohn.discogsproject;

import com.google.gson.annotations.SerializedName;

public class DiscogsImages {

    @SerializedName("uri")
    private String imageUrl;

    @SerializedName("resource_url")
    private String resourceUrl;

    private int height;
    private int width;

    public String getImageUrl() {
        return imageUrl;
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
