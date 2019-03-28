package com.example.stefbadojohn.discogsproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ArtistActivity extends AppCompatActivity {

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

        long artistId;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            artistId = bundle.getLong("artistId");
            getArtist(artistId);
        }
    }

    private void getArtist(long artistId) {
        NetworkInterface network = new RetrofitNetwork(ArtistActivity.this);

        Observable<DiscogsArtist> obsArtist = network.getArtistsById(artistId);

        obsArtist.subscribe(new Observer<DiscogsArtist>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DiscogsArtist discogsArtist) {
                displayArtist(discogsArtist);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ArtistActivity.this,
                        NetworkUtils.networkExceptionHandle(e),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void displayArtist(DiscogsArtist discogsArtist) {
        String artistIdString = String.valueOf(discogsArtist.getId());
        textViewArtistId.setText(artistIdString);
        textViewArtistName.setText(discogsArtist.getName());
        textViewArtistProfile.setText(discogsArtist.getProfile());
        List<DiscogsImage> releaseImages = discogsArtist.getImages();
        if (releaseImages != null) {
            String artistImageUrl = discogsArtist.getImages().get(0).getImageUrl();
            if (!artistImageUrl.equals("")) {
                NetworkUtils.loadImageByInternetUrl(artistImageUrl, imageViewArtistImage);
            }
        }
    }

}
