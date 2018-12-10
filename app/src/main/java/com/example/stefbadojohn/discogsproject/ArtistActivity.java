package com.example.stefbadojohn.discogsproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ArtistActivity extends AppCompatActivity {

    private TextView textview01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        textview01 = findViewById(R.id.textView01);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int arrayListPos = bundle.getInt("arrayListPosition");
            textview01.setText(bundle.getStringArrayList("artistObj").get(arrayListPos));
        }
    }
}
