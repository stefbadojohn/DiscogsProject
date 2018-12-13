package com.example.stefbadojohn.discogsproject;

import java.util.List;

public class DiscogsArtist {

    private int id;
    private String name;
    private List<DiscogsImage> images;
    private String profile;

    public int getId() {
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
