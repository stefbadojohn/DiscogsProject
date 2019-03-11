package com.example.stefbadojohn.discogsproject;

import io.reactivex.Observable;

public interface NetworkInterface {
    // TODO: //AccessToken getAccessToken();
    Observable<String> requestToken();
    Observable<String> getAccessToken(String verifier);

    Observable<DiscogsIdentity> getIdentity();
    Observable<DiscogsRelease> getReleaseById(long id);
    Observable<DiscogsArtist> getArtistsById(long id);
}
