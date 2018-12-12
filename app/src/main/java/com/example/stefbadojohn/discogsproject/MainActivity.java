package com.example.stefbadojohn.discogsproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity {

    private TextView textViewArtist;
    private TextView textViewTitle;
    private Button buttonFetch;
    private EditText editTextId;
    private ListView artistsListView;
    private ImageView imageView;

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
        imageView = findViewById(R.id.imageView01);

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
                } else { // Success
                    DiscogsRelease release = response.body();
                    List<DiscogsArtist> artistsList = release.getArtists();
                    List<DiscogsImages> imagesList = release.getImages();

                    String imgUrl = imagesList.get(0).getImageUrl();
                    loadImageByInternetUrl(imgUrl);

                    displayRelease(artistsList, release.getTitle());

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

    private void displayRelease(List<DiscogsArtist> artistsList, String title) {
        textViewTitle.setText(getString(R.string.title, title));
        textViewArtist.setVisibility(View.VISIBLE);
        populateArtistListView(artistsList);
    }

    private void populateArtistListView(List<DiscogsArtist> artistsList) {
        final Intent intent = new Intent(MainActivity.this, ArtistActivity.class);

        ArrayList<String> artistsNames = new ArrayList<>();

        for (DiscogsArtist artist : artistsList) {
            artistsNames.add(artist.getName());
        }

        intent.putExtra("artistObj", artistsNames);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                artistsNames
        );

        artistsListView.setAdapter(adapter);

        artistsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent.putExtra("arrayListPosition", i);
                startActivity(intent);
            }
        });
    }

    private void loadImageByInternetUrl(String imgUrl) {
        Picasso.get().load(imgUrl).into(imageView);
    }
}