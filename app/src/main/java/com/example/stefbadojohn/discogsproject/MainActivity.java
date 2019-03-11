package com.example.stefbadojohn.discogsproject;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textViewArtist;
    private TextView textViewTitle;
    private Button buttonFetch;
    private Button buttonAuth;
    private Button buttonLogout;
    private EditText editTextId;
    private ListView artistsListView;
    private ImageView imageView;
    private WebView webView;
    private ProgressBar progressBar;

    private UserSessionInterface userSession = new UserSession(this);

    private NetworkInterface network = new Network(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            savedInstanceState.getString("releaseIdString");
        }

        setContentView(R.layout.activity_main);

        textViewArtist = findViewById(R.id.textView_artist);
        textViewTitle = findViewById(R.id.textView_title);
        buttonFetch = findViewById(R.id.button_fetch);
        buttonAuth = findViewById(R.id.button_auth);
        buttonLogout = findViewById(R.id.button_logout);
        editTextId = findViewById(R.id.editText_id);
        artistsListView = findViewById(R.id.artistListView);
        imageView = findViewById(R.id.imageView01);
        webView = findViewById(R.id.webview_auth);
        progressBar = findViewById(R.id.loading_request);

        textViewArtist.setVisibility(View.INVISIBLE);

        if (userSession.getIsLoggedIn()) { // Logged in
            buttonFetch.setVisibility(View.VISIBLE);
            editTextId.setVisibility(View.VISIBLE);
            buttonAuth.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Logged in!",
                    Toast.LENGTH_LONG).show();
            buttonLogout.setVisibility(View.VISIBLE);
        } else {
            // TODO: Start activity with a webview for OAuth
            buttonFetch.setVisibility(View.GONE);
            editTextId.setVisibility(View.GONE);
            buttonLogout.setVisibility(View.GONE);
        }

        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String releaseIdString = editTextId.getText().toString();
                if (!releaseIdString.equals("")) {
                    long releaseId = Long.parseLong(releaseIdString);
                    getRelease(releaseId);
                } else {
                    Toast.makeText(MainActivity.this,
                            "Release id field is empty!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestToken();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSession.logout();
                buttonLogout.setVisibility(View.GONE);
                buttonFetch.setVisibility(View.GONE);
                buttonAuth.setVisibility(View.VISIBLE);
                editTextId.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String releaseIdString = editTextId.getText().toString();
        outState.putString("releaseIdString", releaseIdString);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userSession.getIsLoggedIn()) {
            buttonLogout.setVisibility(View.VISIBLE);
        }
    }

    private void requestToken() {
        Observable<String> obsTokenRequest = network.requestToken();

        obsTokenRequest.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                buttonFetch.setVisibility(View.GONE);
                buttonAuth.setVisibility(View.GONE);
                editTextId.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(String s) {
                Log.d("RequestToken onNext", s);

                String body[] = s.split("&");
                //Log.d("TokenRequest body", body[0] + "\n" + body[1] + "\n" + body[2]);
                if (body[0].contains("oauth_token") && body[1].contains("token_secret")) {
                    String tempToken = body[0].substring(body[0].indexOf("=") + 1);
                    String tokenSecret = body[1].substring(body[1].indexOf("=") + 1);
                    Log.d("RequestToken",
                            "tempToken: " + tempToken +
                                    "\n tokenSecret: " + tokenSecret);
                    userSession.saveUserToken(tempToken, tokenSecret);
                    openAuthPage();
                }
            }

            @Override
            public void onError(Throwable e) {
                NetworkUtils.networkExceptionHandle(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void openAuthPage() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(!url.contains("discogsproject")) {
                    webView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Log.d("auth page", url);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("discogsproject")) {
                    Log.d("callback", url);
                    prepAccessTokenReq(url);
                }
            }

        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(DiscogsClient.AUTHORIZE_URL + "?oauth_token=" + userSession.getUserToken());

    }

    private void prepAccessTokenReq(String url) {
        webView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // "discogsproject://callback?denied=..."
        if (!url.contains("denied")) {
            String data[] = url.split("&");
            //String token[] = data[0].split("=");
            String verifier[] = data[1].split("=");
            requestAccessToken(verifier[1]);
        } else {
            Toast.makeText(MainActivity.this,
                    "User denied authorization!",
                    Toast.LENGTH_SHORT).show();
            userSession.logout();

            progressBar.setVisibility(View.GONE);
            buttonAuth.setVisibility(View.VISIBLE);
        }
    }

    private void requestAccessToken(String verifier) {
        Observable<String> obsAccessToken = network.getAccessToken(verifier);

        obsAccessToken.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("token debug", userSession.getUserToken());
            }

            @Override
            public void onNext(String s) {
                if (s != null) {
                    String data[] = s.split("&");
                    userSession.saveUserToken(
                            data[0].substring(data[0].indexOf("=") + 1),
                            data[1].substring(data[1].indexOf("=") + 1)
                    );
                    getIdentity();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this,
                        NetworkUtils.networkExceptionHandle(e),
                        Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void getIdentity() {
        Observable<DiscogsIdentity> obsRequest = network.getIdentity();

        obsRequest.subscribe(new Observer<DiscogsIdentity>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DiscogsIdentity discogsIdentity) {
                Toast.makeText(MainActivity.this,
                        "Welcome back " + discogsIdentity.getUsername(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this,
                        NetworkUtils.networkExceptionHandle(e),
                        Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onComplete() {
                progressBar.setVisibility(View.GONE);
                buttonFetch.setVisibility(View.VISIBLE);
                buttonAuth.setVisibility(View.GONE);
                editTextId.setVisibility(View.VISIBLE);
                buttonLogout.setVisibility(View.VISIBLE);
            }
        });

    }

    private void getRelease(long releaseId) {

        buttonFetch.setEnabled(false);

        NetworkInterface network = new Network(MainActivity.this);

        Observable<DiscogsRelease> obsRelease = network.getReleaseById(releaseId);

        obsRelease.subscribe(new Observer<DiscogsRelease>() {
            @Override
            public void onSubscribe(Disposable d) {
                progressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNext(DiscogsRelease release) {
                List<DiscogsImage> releaseImages = release.getImages();
                if (releaseImages != null) {
                    if (!release.getImages().get(0).getImageUrl().equals("")) {
                        NetworkUtils.loadImageByInternetUrl(
                                releaseImages.get(0).getImageUrl(),
                                imageView);
                    }
                }
                displayRelease(release.getArtists(), release.getTitle());
            }

            @Override
            public void onError(Throwable e) {
                buttonFetch.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,
                        NetworkUtils.networkExceptionHandle(e),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                buttonFetch.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void displayRelease(List<DiscogsArtist> artistList, String title) {
        textViewTitle.setText(getString(R.string.title, title));
        textViewArtist.setVisibility(View.VISIBLE);
        populateArtistListView(artistList);
    }

    private void populateArtistListView(final List<DiscogsArtist> artistList) {
        final Intent intent = new Intent(MainActivity.this, ArtistActivity.class);

        ArrayList<String> artistArrayNames = new ArrayList<>();

        for (DiscogsArtist artist : artistList) {
            artistArrayNames.add(artist.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                artistArrayNames
        );

        artistsListView.setAdapter(adapter);

        artistsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent.putExtra("itemPositionInArray", i);
                intent.putExtra("artistId", artistList.get(i).getId());
                startActivity(intent);
            }
        });
    }

}