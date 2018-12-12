package com.example.stefbadojohn.discogsproject;

import java.util.List;

public class DiscogsRelease {

    private List<DiscogsArtist> artists;
    private String title;
    private List<DiscogsImages> images;

    public List<DiscogsArtist> getArtists() {
        return artists;
    }

    public String getTitle() {
        return title;
    }

    public List<DiscogsImages> getImages() {
        return images;
    }
}
