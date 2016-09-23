package com.example.hassan.moviesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Hassan on 9/23/2016.
 */
public class DetailFragment extends Fragment {

    TrailerAdapter mTrailerAdapter;
    String MTrailerLinksCopy[];
    Movie movie;
    String MTrailerLinks[];
    ListView listView1;
    ListView listView2;
    ReviewAdapter mReviewAdapter;
    boolean checkMovie = true;
    DatabaseHelper database;
    int mNumOfSeen;

    public DetailFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        movie = (Movie) getArguments().getSerializable("value");
        ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.scrollerView);

        if (movie != null) {
            scrollView.setVisibility(View.VISIBLE);
        } else {
            scrollView.setVisibility(View.INVISIBLE);
        }

        listView1 = (ListView) rootView.findViewById(R.id.trailerListItem);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + MTrailerLinksCopy[position]));
                startActivity(intent);

            }

        });

        listView2 = (ListView) rootView.findViewById(R.id.reviewListView);

        ((TextView) rootView.findViewById(R.id.release))
                .setText(movie.release_date);
        ((TextView) rootView.findViewById(R.id.overview))
                .setText(movie.overview);
        ((TextView) rootView.findViewById(R.id.voteAverage))
                .setText(movie.vote_average);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.detailImage);
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w342/" + movie.poster_path).into(imageView);

        database = new DatabaseHelper(getActivity());
        Button button = (Button) rootView.findViewById(R.id.favouriteButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Movie> arrayList;
                arrayList = database.getAllMovies();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).movie_id.equals(movie.movie_id)) {
                        checkMovie = false;
                    }

                }

                if (checkMovie) {
                    database.addMovie(new Movie(movie.movie_id, movie.poster_path, movie.title, movie.vote_average, movie.release_date, movie.overview));
                    Toast.makeText(getActivity(), "Movie added to Your Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Movie already in Your Favourite", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNumOfSeen++;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seenNum", mNumOfSeen);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mNumOfSeen = savedInstanceState.getInt("seenNum");
        }
    }

    private void fetchTrailers() {
        trailerDataFetch trailerDataFetch = new trailerDataFetch();
        trailerDataFetch.execute();

    }

    private void fetchReviews() {
        reviewsDataFetch reviewsDataFetch = new reviewsDataFetch();
        reviewsDataFetch.execute();

    }

    public void onStart() {
        super.onStart();
        fetchTrailers();
        fetchReviews();
    }

    public class trailerDataFetch extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = trailerDataFetch.class.getSimpleName();


        public trailerDataFetch() {

        }

        private String[] MoviesJasonPrase(String moviesPosterStr) throws JSONException {


            JSONObject moviesJson = new JSONObject(moviesPosterStr);
            JSONArray resultsArray = moviesJson.getJSONArray("results");
            MTrailerLinks = new String[resultsArray.length()];
            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject movies = resultsArray.getJSONObject(i);
                MTrailerLinks[i] = movies.getString("key");
            }

            return MTrailerLinks;
        }


        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            final String API_key = "89cd78f15f3aea1811fb3a323860fe20";
            final String API_param = "api_key";

            String movieJsonStr = null;

            final Uri.Builder builder = new Uri.Builder();
            builder.scheme("https").authority("api.themoviedb.org").appendPath("3")
                    .appendPath("movie").appendPath(movie.movie_id).appendPath("videos")
                    .appendQueryParameter(API_param, API_key);
            try {

                URL url = new URL(builder.toString());
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
                    }
                }
            }

            try {
                return MoviesJasonPrase(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(null);
            MTrailerLinksCopy = new String[result.length];
            if (result != null) {
                for (int i = 0; i < result.length; i++) {
                    MTrailerLinksCopy[i] = result[i];
                }
                mTrailerAdapter = new TrailerAdapter(getActivity(), result);
                listView1.setAdapter(mTrailerAdapter);


            }
        }

    }

    public class reviewsDataFetch extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = trailerDataFetch.class.getSimpleName();


        public reviewsDataFetch() {

        }

        private String[] MoviesJasonPrase(String moviesPosterStr) throws JSONException {


            JSONObject moviesJson = new JSONObject(moviesPosterStr);
            JSONArray resultsArray = moviesJson.getJSONArray("results");
            MTrailerLinks = new String[resultsArray.length()];
            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject movies = resultsArray.getJSONObject(i);
                MTrailerLinks[i] = movies.getString("content");
            }

            return MTrailerLinks;
        }


        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final String API_key = "89cd78f15f3aea1811fb3a323860fe20";
            final String API_param = "api_key";

            String movieJsonStr = null;

            try {

                final Uri.Builder builder = new Uri.Builder();
                builder.scheme("https").authority("api.themoviedb.org").appendPath("3")
                        .appendPath("movie").appendPath(movie.movie_id).appendPath("reviews")
                        .appendQueryParameter(API_param, API_key);
                URL url = new URL(builder.toString());
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
                    }
                }
            }

            try {
                return MoviesJasonPrase(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (final RuntimeException i) {
                i.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(null);

            if (result != null) {
                mReviewAdapter = new ReviewAdapter(getActivity(), result);
                listView2.setAdapter(mReviewAdapter);
            }

        }
    }

}
