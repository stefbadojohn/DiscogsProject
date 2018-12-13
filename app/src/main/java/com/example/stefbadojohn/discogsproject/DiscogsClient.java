package com.example.stefbadojohn.discogsproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface DiscogsClient {

    @GET("/releases/{releaseId}")
    Call<DiscogsRelease> release(@Path("releaseId") String id);

    @GET("/artists/{artistId}")
    Call<DiscogsArtist> artist(@Path("artistId") String id);
}
