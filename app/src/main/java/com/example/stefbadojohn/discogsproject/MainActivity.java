package com.example.stefbadojohn.discogsproject;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView_artist)
    TextView textViewArtist;
    @BindView(R.id.textView_title)
    TextView textViewTitle;
    @BindView(R.id.button_fetch)
    Button buttonFetch;
    @BindView(R.id.button_auth)
    Button buttonAuth;
    @BindView(R.id.button_logout)
    Button buttonLogout;
    /*@BindView(R.id.editText_id)
    EditText editTextId;*/
    @BindView(R.id.artistListView)
    ListView artistsListView;
    @BindView(R.id.imageView01)
    ImageView imageView;
    @BindView(R.id.progressBar)
    ProgressBar spinner;
    @BindView(R.id.editText_release)
    EditText editTextRelease;
    @BindView(R.id.myListView)
    ListView myListView;
/*    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;*/

    private ResultsAdapter resultsAdapter;

    private UserSessionInterface userSession = UserSessionInterface.instance;

    private NetworkInterface network = NetworkInterface.instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            savedInstanceState.getString("releaseIdString");
        }

        textViewArtist.setVisibility(View.INVISIBLE);

        if (userSession.isLoggedIn()) {
            buttonAuth.setVisibility(View.GONE);
            buttonLogout.setVisibility(View.VISIBLE);
        } else {
            buttonLogout.setVisibility(View.GONE);
            openOAuthActivity();
        }
/*

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

*/
        buttonFetch.setVisibility(View.GONE); //TODO: Remove fetch button entirely?

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
                /*editTextId.setVisibility(View.GONE);*/
            }
        });

        searchInit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /*String releaseIdString = editTextId.getText().toString();
        outState.putString("releaseIdString", releaseIdString);*/

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

    private void getReleaseByTitle(String title) {

        Observable<DiscogsSearch> obsSearch = network.getReleaseByTitle("release", title, "10", "1");

        obsSearch.subscribe(new Observer<DiscogsSearch>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DiscogsSearch discogsSearch) {

                if (discogsSearch.getResults().size() == 0) {
                    myListView.setAdapter(null);
                    return;
                }

                ArrayList<DiscogsResult> discResults = new ArrayList<>();

                for (DiscogsResult result : discogsSearch.getResults()) {
                    discResults.add(result);
                }

                resultsAdapter = new ResultsAdapter(MainActivity.this, 0, discResults);

                myListView.setAdapter(resultsAdapter);
/*
                Toast.makeText(MainActivity.this,
                        "1st result: " + discogsSearch.getResults().get(0).getTitle(),
                        Toast.LENGTH_LONG).show();
*/
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void searchInit() {

        RxTextView.textChanges(editTextRelease)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<CharSequence>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        if (!charSequence.toString().equals("")) {
                            getReleaseByTitle(charSequence.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}