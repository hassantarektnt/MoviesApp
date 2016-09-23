package com.example.hassan.moviesapp;

import java.io.Serializable;

/**
 * Created by Hassan on 9/23/2016.
 */


public class Movie implements Serializable {
    public String overview;
    public String release_date;
    public String movie_id;
    public String poster_path;
    public String title;
    public String vote_average;

    public Movie() {
    }

    public Movie(String movie_id, String poster_path, String title, String vote_average, String release_date, String overview) {
        this.poster_path = poster_path;
        this.title = title;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.overview = overview;
        this.movie_id = movie_id;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }
}
