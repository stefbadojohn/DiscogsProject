package com.example.stefbadojohn.discogsproject;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DiscogsClient {

    String AUTHORIZE_URL = "https://www.discogs.com/oauth/authorize";

    String ENDPOINT_REQUEST = "/oauth/request_token";
    String ENDPOINT_ACCESS = "/oauth/access_token";

    @GET(ENDPOINT_REQUEST)
    Observable<String> requestToken();

    @POST(ENDPOINT_ACCESS)
    Observable<String> getAccessToken(@Header("Authorization") String oauth);

    @GET("/oauth/identity")
    Observable<DiscogsIdentity> getIdentity();

    @GET("/releases/{releaseId}")
    Observable<DiscogsRelease> release(@Path("releaseId") long id);

    @GET("/artists/{artistId}")
    Observable<DiscogsArtist> artist(@Path("artistId") long id);

    //@GET("/database/search?release_title={releaseTitle}&per_page=3&page=1")
}
