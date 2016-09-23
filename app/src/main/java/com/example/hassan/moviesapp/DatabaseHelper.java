package com.example.hassan.moviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hassan on 9/23/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MY_DB";
    private static final String TABLE_Movies = "MyMovies";
    private static final String KEY_ID = "movie_id";
    private static final String KEY_overview = "overview";
    private static final String KEY_PosterPath = "poster_path";
    private static final String KEY_releaseDate = "release_date";
    private static final String KEY_vote_avg = "vote_avg";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_Movie_TABLE = "CREATE TABLE MyMovies ( " +
                "movie_id INTEGER PRIMARY KEY , " +
                "overview TEXT, " +
                "poster_path TEXT, " +
                "vote_avg TEXT," +
                "release_date TEXT  )";

        db.execSQL(CREATE_Movie_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MyMovies");
        this.onCreate(db);
    }

    public void addMovie(Movie movies) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, movies.movie_id);
        values.put(KEY_overview, movies.overview);
        values.put(KEY_PosterPath, movies.poster_path);
        values.put(KEY_releaseDate, movies.release_date);
        values.put(KEY_vote_avg, movies.vote_average);
        db.insert(TABLE_Movies, null, values);
        db.close();
    }


    public List<Movie> getAllMovies() {
        List<Movie> Movies = new ArrayList<>();

        String query = "SELECT  * FROM " + TABLE_Movies;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setMovie_id(cursor.getString(0));
                movie.setOverview(cursor.getString(1));
                movie.setPoster_path(cursor.getString(2));
                movie.setRelease_date(cursor.getString(3));
                movie.setVote_average(cursor.getString(4));
                Movies.add(movie);
            } while (cursor.moveToNext());
        }
        return Movies;
    }
}