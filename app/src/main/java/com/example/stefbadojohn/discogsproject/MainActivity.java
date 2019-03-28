package com.example.stefbadojohn.discogsproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private TextView textViewArtist;
    private TextView textViewTitle;
    private Button buttonFetch;
    private Button buttonAuth;
    private Button buttonLogout;
    private EditText editTextId;
    private ListView artistsListView;
    private ImageView imageView;
    private ProgressBar spinner;

    private UserSessionInterface userSession = UserSessionInterface.instance;

    private NetworkInterface network = NetworkInterface.instance;

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
        spinner = findViewById(R.id.progressBar);

        textViewArtist.setVisibility(View.INVISIBLE);

        if (userSession.isLoggedIn()) {
            buttonAuth.setVisibility(View.GONE);
            buttonLogout.setVisibility(View.VISIBLE);
        } else {
            buttonLogout.setVisibility(View.GONE);
            openOAuthActivity();
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
                openOAuthActivity();
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
        if (userSession.isLoggedIn()) {
            buttonLogout.setVisibility(View.VISIBLE);
            buttonAuth.setVisibility(View.GONE);
            getIdentity();
        }
    }

    private void openOAuthActivity() {
        Intent intent = new Intent(MainActivity.this, OAuthActivity.class);
        startActivity(intent);
    }

    private void getIdentity() {
        Observable<DiscogsIdentity> obsRequest = network.getIdentity();

        obsRequest.subscribe(new Observer<DiscogsIdentity>() {
            @Override
            public void onSubscribe(Disposable d) {
                spinner.setVisibility(View.VISIBLE);
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

                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onComplete() {
                spinner.setVisibility(View.GONE);
            }
        });

    }

    private void getRelease(long releaseId) {
        buttonFetch.setEnabled(false);

        Observable<DiscogsRelease> obsRelease = network.getReleaseById(releaseId);

        obsRelease.subscribe(new Observer<DiscogsRelease>() {
            @Override
            public void onSubscribe(Disposable d) {
                spinner.setVisibility(View.VISIBLE);

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
                spinner.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,
                        NetworkUtils.networkExceptionHandle(e),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                buttonFetch.setEnabled(true);
                spinner.setVisibility(View.GONE);
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