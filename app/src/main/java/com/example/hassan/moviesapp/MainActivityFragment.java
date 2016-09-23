package com.example.hassan.moviesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hassan on 9/23/2016.
 */

public class MainActivityFragment extends Fragment {

    String choice = "";
    Movie[] items, copy;
    GridView gridView;
    movieImageAdapter image_adapter;
    DatabaseHelper db;
    int mNoOfSeen;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.moviesGrid);
        db = new DatabaseHelper(getActivity());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        choice = prefs.getString(getString(R.string.pref_key), getString(R.string.pref_popular_default));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                final Movie movie = image_adapter.getItem(position);
                ((PanesHandler) getActivity()).setSelectedName(movie);


            }


        });


        return rootView;
    }

    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void favourite() {
        List<Movie> list;
        list = db.getAllMovies();


        for (Movie movie1 : list) {
            Log.i("Movie", movie1.getRelease_date());
        }
        image_adapter = new movieImageAdapter(getActivity(), list);
        gridView.setAdapter(image_adapter);
    }

    private void updateMovie() {

        movieDataFetch MovieTask = new movieDataFetch();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        choice = prefs.getString(getString(R.string.pref_key), getString(R.string.pref_popular_default));

        if (choice.equals("Favourite")) {
            favourite();
        } else

        {
            MovieTask.execute(choice);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        choice = prefs.getString(getString(R.string.pref_key), getString(R.string.pref_popular_default));

        mNoOfSeen++;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        choice = prefs.getString(getString(R.string.pref_key), getString(R.string.pref_popular_default));

        if (!networkAvailable() && !choice.equals("Favourite")) {

            Toast.makeText(getActivity(), "No Internet connection", Toast.LENGTH_LONG).show();

        } else {
            updateMovie();

        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seenNum", mNoOfSeen);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mNoOfSeen = savedInstanceState.getInt("seenNum");
        }
    }

    public class movieDataFetch extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = movieDataFetch.class.getSimpleName();


        public movieDataFetch() {

        }

        private Movie[] MoviesJasonParse(String moviesPosterStr) throws JSONException {


            JSONObject moviesJson = new JSONObject(moviesPosterStr);
            JSONArray resultsArray = moviesJson.getJSONArray("results");
            items = new Movie[resultsArray.length()];

            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject movies = resultsArray.getJSONObject(i);
                items[i] = new Movie(movies.getString("id"), movies.getString("poster_path"), movies.getString("title"), movies.getString("vote_average"), movies.getString("release_date"), movies.getString("overview"));
            }

            return items;
        }


        @Override
        protected Movie[] doInBackground(String... params) {
           HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final String API_key = "89cd78f15f3aea1811fb3a323860fe20";
            final String API_param = "api_key";
            final String link = "http://api.themoviedb.org/3/movie/";
            String movieJsonStr = null;

            try {

                Uri buildUri = Uri.parse(link).buildUpon().appendPath(choice).appendQueryParameter(API_param, API_key).build();
                Log.e("TNT", buildUri.toString());
                URL url = new URL(buildUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    } catch (final RuntimeException i) {
                        i.printStackTrace();
                    }
                }
            }

            try {
                return MoviesJasonParse(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (final RuntimeException i) {
                i.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            copy = new Movie[result.length];
            if (result != null) {
                for (int i = 0; i < result.length; i++)
                    copy[i] = result[i];
                image_adapter = new movieImageAdapter(getActivity(), Arrays.asList(result));
                gridView.setAdapter(image_adapter);
            }
        }
    }
}
