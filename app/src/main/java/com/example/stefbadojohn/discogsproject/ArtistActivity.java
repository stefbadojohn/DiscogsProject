package com.example.stefbadojohn.discogsproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ArtistActivity extends AppCompatActivity {

    int artistId;
    private TextView textViewArtistName;
    private TextView textViewArtistId;
    private ImageView imageViewArtistImage;
    private TextView textViewArtistProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        textViewArtistName = findViewById(R.id.textViewArtistName);
        textViewArtistId = findViewById(R.id.textViewArtistId);
        imageViewArtistImage = findViewById(R.id.imageViewArtistImage);
        textViewArtistProfile = findViewById(R.id.textViewArtistProfile);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            artistId = bundle.getInt("artistId");
        }

        Retrofit retrofit = RetrofitClient.getClient();

        DiscogsClient client = retrofit.create(DiscogsClient.class);

        Call<DiscogsArtist> call = client.artist(String.valueOf(artistId));

        call.enqueue(new Callback<DiscogsArtist>() {
            @Override
            public void onResponse(Call<DiscogsArtist> call, Response<DiscogsArtist> response) {
                if (response.isSuccessful()) {
                    DiscogsArtist artist = response.body();

                    textViewArtistName.setText(artist.getName());
                    textViewArtistId.setText(String.valueOf(artist.getId()));

                    if (artist.getImages() != null) {
                        if (!artist.getImages().get(0).getImageUrl().equals("")) {
                            loadImageByInternetUrl(artist.getImages().get(0).getImageUrl());
                        }
                    }

                    if (artist.getProfile() != null) {
                        //textViewArtistProfile.setText(artist.getProfile());
                        textViewArtistProfile.setText(artist.getProfile());
                    }

                }
            }

            @Override
            public void onFailure(Call<DiscogsArtist> call, Throwable t) {

            }
        });
    }

    private void loadImageByInternetUrl(String imgUrl) {
        Picasso.get()
                .load(imgUrl)
                .into(imageViewArtistImage);
    }
}
