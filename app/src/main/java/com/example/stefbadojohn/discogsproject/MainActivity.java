package com.example.stefbadojohn.discogsproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textViewArtist;
    private TextView textViewTitle;
    private Button buttonFetch;
    private EditText editTextId;
    private ListView artistsListView;

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.discogs.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private DiscogsClient client = retrofit.create(DiscogsClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewArtist = findViewById(R.id.textView_artist);
        textViewTitle = findViewById(R.id.textView_title);
        buttonFetch = findViewById(R.id.button_fetch);
        editTextId = findViewById(R.id.editText_id);
        artistsListView = findViewById(R.id.artistListView);

        textViewArtist.setVisibility(View.INVISIBLE);

        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String releaseId = editTextId.getText().toString();
                getRelease(releaseId);
            }
        });

    }

    private void getRelease(String releaseId) {
        buttonFetch.setEnabled(false);

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
                } else {
                    DiscogsRelease release = response.body();
                    List<DiscogsArtist> artistsList = release.getArtists();

                    populateArtistListView(artistsList);

                    textViewTitle.setText(getString(R.string.title, release.getTitle()));
                    textViewArtist.setVisibility(View.VISIBLE);
                    //textViewArtist.setText(getString(R.string.artist, artistsList.get(0).getName()));
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

    private void populateArtistListView(List<DiscogsArtist> artistsList) {
        ArrayList<String> artistsNames = new ArrayList<>();

        for (DiscogsArtist artist : artistsList) {
            artistsNames.add(artist.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                artistsNames
        );

        artistsListView.setAdapter(adapter);
    }
}