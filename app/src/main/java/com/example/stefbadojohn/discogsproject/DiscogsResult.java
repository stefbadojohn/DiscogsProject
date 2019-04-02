package com.example.stefbadojohn.discogsproject;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DiscogsResult {
    private long id;
    private String title;
    @SerializedName("cover_image")
    private String thumbUrl;
    private List<String> style;
    private String year;

    public List<String> getStyle() {
        return style;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getYear() {
        return year;
    }
}
