package com.example.stefbadojohn.discogsproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface DiscogsClient {

    @Headers("Authorization: Discogs key=foo, secret=bar")
    @GET("/releases/{releaseId}")
    Call<DiscogsRelease> release(@Path("releaseId") String id);

}
