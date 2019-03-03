package com.example.stefbadojohn.discogsproject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DiscogsClient {

    @GET("/oauth/request_token")
    @Headers({
            "User-Agent: DiscogsProject/0.1",
            "Content-Type: application/x-www-form-urlencoded"
    })
    Call<String> requestToken(@Header("Authorization") String oauth);

    @POST("/oauth/access_token")
    //@FormUrlEncoded
    @Headers({
            "User-Agent: DiscogsProject/0.1",
            "Content-Type: application/x-www-form-urlencoded"
            //"Authorization: OAuth"
    })
    Call<String> getAccessToken(@Header("Authorization") String oauth);

    @GET("/oauth/identity")
    @Headers({"User-Agent: DiscogsProject/0.1"})
    Call<DiscogsIdentity> getIdentity(@Header("Authorization") String oauth);

    @GET("/releases/{releaseId}")
    @Headers({"User-Agent: DiscogsProject/0.1"})
    Call<DiscogsRelease> release(@Path("releaseId") String id);

    @GET("/artists/{artistId}")
    @Headers({"User-Agent: DiscogsProject/0.1"})
    Call<DiscogsArtist> artist(@Path("artistId") String id);
}
