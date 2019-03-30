package com.example.stefbadojohn.discogsproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ArtistActivity extends AppCompatActivity {

    @BindView(R.id.textViewArtistName) TextView textViewArtistName;
    @BindView(R.id.textViewArtistId) TextView textViewArtistId;
    @BindView(R.id.imageViewArtistImage) ImageView imageViewArtistImage;
    @BindView(R.id.textViewArtistProfile) TextView textViewArtistProfile;

    private NetworkInterface network = NetworkInterface.instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        ButterKnife.bind(this);

        long artistId;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            artistId = bundle.getLong("artistId");
            getArtist(artistId);
        }
    }

    private void getArtist(long artistId) {
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
