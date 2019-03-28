package com.example.stefbadojohn.discogsproject;

import io.reactivex.Observable;

public interface NetworkInterface {
    // TODO: //AccessToken getAccessToken();
    Observable<String> requestToken();
    Observable<String> getAccessToken(String verifier);

    Observable<DiscogsIdentity> getIdentity();
    Observable<DiscogsRelease> getReleaseById(long id);
    Observable<DiscogsArtist> getArtistsById(long id);

    static NetworkInterface _instance = null;
    static NetworkInterface getInstance() {
        if (_instance == null) {
            
        }

        return _instance;
    }
}
