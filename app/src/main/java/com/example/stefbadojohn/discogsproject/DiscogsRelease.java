package com.example.stefbadojohn.discogsproject;

import java.util.List;

public class DiscogsRelease {

    private List<DiscogsArtist> artists;
    private String title;
    private List<DiscogsImage> images;

    public List<DiscogsArtist> getArtists() {
        return artists;
    }

    public String getTitle() {
        return title;
    }

    public List<DiscogsImage> getImages() {
        return images;
    }
}
