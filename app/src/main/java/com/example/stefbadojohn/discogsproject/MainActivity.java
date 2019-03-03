package com.example.stefbadojohn.discogsproject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textViewArtist;
    private TextView textViewTitle;
    private Button buttonFetch;
    private Button buttonAuth;
    private EditText editTextId;
    private ListView artistsListView;
    private ImageView imageView;
    private WebView webView;
    private ProgressBar progressBar;

    private String consumerKey = "key";
    private String consumerSecret = "secret";

    private String authorizeUrl = "https://www.discogs.com/oauth/authorize";
    private AccessToken accessToken = new AccessToken();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

        }

        setContentView(R.layout.activity_main);

        textViewArtist = findViewById(R.id.textView_artist);
        textViewTitle = findViewById(R.id.textView_title);
        buttonFetch = findViewById(R.id.button_fetch);
        buttonAuth = findViewById(R.id.button_auth);
        editTextId = findViewById(R.id.editText_id);
        artistsListView = findViewById(R.id.artistListView);
        imageView = findViewById(R.id.imageView01);
        webView = findViewById(R.id.webview_auth);
        progressBar = findViewById(R.id.loading_request);

        textViewArtist.setVisibility(View.INVISIBLE);

        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String releaseId = editTextId.getText().toString();
                getRelease(releaseId);
            }
        });

        buttonAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestToken();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void requestToken() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.discogs.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        DiscogsClient client = retrofit.create(DiscogsClient.class);

        String oauthHeader = createOAuthHeader("request",
                consumerKey,
                consumerSecret,
                "",
                "",
                "",
                "discogsproject://callback"
        );

        Call<String> call = client.requestToken(oauthHeader);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    Log.d("TokenRequest", "Error code: " + response.code());
                } else {
                    String body[] = response.body().split("&");
                    Log.d("TokenRequest body", body[0] + "\n" + body[1] + "\n" + body[2]);
                    accessToken.setOauthTokenSecret(body[1].substring(body[1].indexOf("=")+1));
                    //at.setOauthTokenSecret(body[1].substring(body[1].indexOf("=")+1));
                    openAuthPage(body[0]);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("TokenRequest", "Throwable msg: " + t.getMessage());
            }
        });
    }

    private void openAuthPage(String token) {
        buttonFetch.setVisibility(View.GONE);
        buttonAuth.setVisibility(View.GONE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("discogsproject")) {
                    Log.d("WebView Url", url);
                    webView.destroy();
                    prepAccessTokenReq(url);
                    return true;
                } else {
                    return false;
                }
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(authorizeUrl + "?" + token);

    }

    private void prepAccessTokenReq(String url) {
        buttonFetch.setVisibility(View.VISIBLE);
        buttonAuth.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        String data[] = url.split("&");
        String token[] = data[0].split("=");
        String verifier[] = data[1].split("=");

        requestAccessToken(token[1], verifier[1], accessToken.getOauthTokenSecret());
    }

    private void requestAccessToken(String oauthToken, String verifier, String tokenSecret) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.discogs.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        DiscogsClient client = retrofit.create(DiscogsClient.class);

        String oauthHeader = createOAuthHeader("access",
                consumerKey,
                consumerSecret,
                oauthToken,
                tokenSecret,
                verifier,
                ""
        );

        Call<String> call = client.getAccessToken(oauthHeader);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    Log.d("AccessToken", "Error: " + response.code());
                } else {
                    Log.d("AccessToken", "Code: " + response.code() + "\n" + response.headers().toString());
                    if (response.body() != null) {
                        String body = response.body();
                        //Log.d("AccessToken", "" + response.body());

                        String data[] = body.split("&");
                        accessToken.setOauthToken(data[0].substring(data[0].indexOf("=")+1));
                        accessToken.setOauthTokenSecret(data[1].substring(data[1].indexOf("=")+1));

                        Log.d("AccessToken/Token", accessToken.getOauthToken());
                        Log.d("AccessToken/Secret", accessToken.getOauthTokenSecret());

                        buttonAuth.setVisibility(View.GONE);
                        testAuth();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("AccessToken", "Throwable: " + t.getMessage());
            }
        });
    }

    private String createOAuthHeader(String type, String key, String secret, String oauthToken, String oauthTokenSecret, String usersVerifier, String callbackUrl) {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        String callback = "";
        String token = "";
        String verifier = "";
        String tokenSecret = "";
        String timestamp = "";

        if (type.equals("request")) {
            callback = "oauth_callback=\"" + callbackUrl + "\"";
            timestamp = "oauth_timestamp=\"" + ts + "\", ";
        }

        if (type.equals("access")) {
            token = "oauth_token=\"" + oauthToken + "\", ";
            verifier = "oauth_verifier=\"" + usersVerifier + "\"";
            tokenSecret = oauthTokenSecret;
            timestamp = "oauth_timestamp=\"" + ts + "\", ";
        }

        if (type.equals("oauth")) {
            token = "oauth_token=\"" + oauthToken + "\", ";
            tokenSecret = oauthTokenSecret;
            timestamp = "oauth_timestamp=\"" + ts + "\"";
        }

        String oauthHeader =  "OAuth " +
                "oauth_consumer_key=\"" + key + "\", " +
                "oauth_nonce=\"" + ts + "\", " +
                token +
                "oauth_signature=\"" + secret + "&" + tokenSecret + "\", " +
                "oauth_signature_method=\"PLAINTEXT\", " +
                timestamp +
                verifier +
                callback;

        Log.d("OAuth Header", ""+oauthHeader);

        return oauthHeader;
    }

    private void testAuth() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.discogs.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DiscogsClient client = retrofit.create(DiscogsClient.class);

        String oauthHeader = createOAuthHeader("oauth",
                consumerKey,
                consumerSecret,
                accessToken.getOauthToken(),
                accessToken.getOauthTokenSecret(),
                "",
                ""
        );

        Call<DiscogsIdentity> call = client.getIdentity(oauthHeader);

        call.enqueue(new Callback<DiscogsIdentity>() {
            @Override
            public void onResponse(Call<DiscogsIdentity> call, Response<DiscogsIdentity> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                } else {
                    DiscogsIdentity identity = response.body();
                    Toast.makeText(MainActivity.this, "Welcome back " + identity.getUsername(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DiscogsIdentity> call, Throwable t) {
                Log.d("testAuth", "" + t.getMessage());
            }
        });
    }

    private void getRelease(String releaseId) {
        buttonFetch.setEnabled(false);

        Retrofit retrofit = RetrofitClient.getClient();

        DiscogsClient client = retrofit.create(DiscogsClient.class);

        Call<DiscogsRelease> call = client.release(releaseId);

        call.enqueue(new Callback<DiscogsRelease>() {
            @Override
            public void onResponse(Call<DiscogsRelease> call, Response<DiscogsRelease> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 404) {
                        Toast.makeText(MainActivity.this,
                                "Couldn't find a release with the specified ID!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Code: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else { // Success
                    DiscogsRelease release = response.body();
                    List<DiscogsArtist> artistList = release.getArtists();
                    List<DiscogsImage> imageList;

                    if (release.getImages() != null) {
                        imageList = release.getImages();                    // Fill imageList
                        String imgUrl = imageList.get(0).getImageUrl();     // Get 1st image url
                        loadImageByInternetUrl(imgUrl);                     // Load image using picasso
                    } else {
                        imageView.setImageResource(android.R.color.transparent);    // Clear imageView
                        Toast.makeText(MainActivity.this,
                                "No image found for this release!",
                                Toast.LENGTH_SHORT).show();
                    }

                    displayRelease(artistList, release.getTitle());
                }

                buttonFetch.setEnabled(true);
            }

            @Override
            public void onFailure(Call<DiscogsRelease> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Error",
                        Toast.LENGTH_LONG).show();
                Log.e("Release Call Failure", "Throwable: " + t.toString());

                buttonFetch.setEnabled(true);
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

        //intent.putExtra("artistArrayNames", artistArrayNames);

        artistsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent.putExtra("itemPositionInArray", i);
                intent.putExtra("artistId", artistList.get(i).getId());
                startActivity(intent);
            }
        });
    }

    private void loadImageByInternetUrl(String imgUrl) {
        Picasso.get()
                .load(imgUrl)
                .into(imageView);
    }
}