package com.example.stefbadojohn.discogsproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewArtist = findViewById(R.id.textView_artist);
        textViewTitle = findViewById(R.id.textView_title);
        buttonFetch = findViewById(R.id.button_fetch);
        editTextId = findViewById(R.id.editText_id);

        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String releaseId = editTextId.getText().toString();
                getReleaseInfo(releaseId);
            }
        });

    }

    public void getReleaseInfo(String releaseId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.discogs.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DiscogsClient client = retrofit.create(DiscogsClient.class);
        Call<DiscogsRelease> call = client.release(releaseId);

        call.enqueue(new Callback<DiscogsRelease>() {
            @Override
            public void onResponse(Call<DiscogsRelease> call, Response<DiscogsRelease> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 404) {
                        Toast.makeText(MainActivity.this,
                                "We couldn't find a release with the specified ID!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Error Code: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    DiscogsRelease release = response.body();
                    textViewArtist.setText(getString(R.string.artist, release.getArtists_sort()));
                    textViewTitle.setText(getString(R.string.title, release.getTitle()));
                }
            }

            @Override
            public void onFailure(Call<DiscogsRelease> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Error" + t.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
