package com.example.stefbadojohn.discogsproject;

import java.util.List;

public class DiscogsArtist {

    private long id;
    private String name;
    private List<DiscogsImage> images;
    private String profile;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<DiscogsImage> getImages() {
        return images;
    }

    public String getProfile() {
        return profile;
    }
}
