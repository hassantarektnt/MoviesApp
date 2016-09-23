package com.example.hassan.moviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Hassan on 9/23/2016.
 */
public class DetailsActivity extends AppCompatActivity {
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (null == savedInstanceState) {
            Bundle extras = getIntent().getExtras();

            movie = (Movie) getIntent().getSerializableExtra("value");
            Log.i("Movie : ", movie.release_date);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().add(R.id.contentContainer, fragment)
                    .commit();

        }
    }
}
