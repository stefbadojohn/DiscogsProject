package com.example.stefbadojohn.discogsproject;

import android.content.Context;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitNetwork implements NetworkInterface {

    private CredentialManagerInterface credManager = CredentialManagerInterface.instance;
    private UserSessionInterface userSession = UserSessionInterface.instance;

    private OkHttpClient okhttp;

    private DiscogsClient client;

    private DiscogsClient loginClient;

    public RetrofitNetwork() {
        okhttpInit();
        retrofitInit();
        loginRetrofitInit();
    }

    private void okhttpInit() {
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        okhttpBuilder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                String endpoint = chain.request().url().encodedPath();

                Request request = chain.request();
                Request.Builder newRequest = request.newBuilder()
                        .header("User-Agent", "DiscogsProject/0.1");

                boolean isLoginEndpoint = endpoint.contains(DiscogsClient.ENDPOINT_REQUEST)
                        || endpoint.contains(DiscogsClient.ENDPOINT_ACCESS);

                if (!isLoginEndpoint) {
                    String oauthHeader = NetworkUtils.createOAuthHeader(
                            NetworkUtils.RequestType.AUTHORIZED,
                            credManager.getConsumerKey(),
                            credManager.getConsumerSecret(),
                            userSession.getUserToken(),
                            userSession.getUserTokenSecret(),
                            "",
                            ""
                    );
                    newRequest.header("Authorization", oauthHeader);
                } else {
                    newRequest.header("Content-Type", "application/x-www-form-urlencoded");
                    if (endpoint.contains(DiscogsClient.ENDPOINT_REQUEST)) {
                        String oauthHeader = NetworkUtils.createOAuthHeader(
                                NetworkUtils.RequestType.TEMP_TOKEN,
                                credManager.getConsumerKey(),
                                credManager.getConsumerSecret(),
                                "",
                                "",
                                "",
                                "discogsproject://callback"
                        );
                        newRequest.header("Authorization", oauthHeader);
                    }
                }

                return chain.proceed(newRequest.build());
            }
        });

        okhttp = okhttpBuilder.build();
    }

    private void retrofitInit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.discogs.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okhttp)
                .build();

        client = retrofit.create(DiscogsClient.class);
    }

    private void loginRetrofitInit() {
        Retrofit loginRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.discogs.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okhttp)
                .build();

        loginClient = loginRetrofit.create(DiscogsClient.class);
    }

    @Override
    public Observable<String> requestToken() {
        Observable<String> obsRequest = loginClient.requestToken();

        return obsRequest
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<String> getAccessToken(String verifier) {
        String oauthHeader = NetworkUtils.createOAuthHeader(
                NetworkUtils.RequestType.ACCESS_TOKEN,
                credManager.getConsumerKey(),
                credManager.getConsumerSecret(),
                userSession.getUserToken(),
                userSession.getUserTokenSecret(),
                verifier,
                ""
        );

        Observable<String> obsRequest = loginClient.getAccessToken(oauthHeader);

        return obsRequest
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DiscogsIdentity> getIdentity() {
        Observable<DiscogsIdentity> obsRequest = client.getIdentity();

        return obsRequest
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DiscogsRelease> getReleaseById(long releaseId) {
        Observable<DiscogsRelease> obsRequest = client.release(releaseId);

        return obsRequest
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DiscogsArtist> getArtistsById(long artistId) {
        Observable<DiscogsArtist> obsRequest = client.artist(artistId);

        return obsRequest
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
