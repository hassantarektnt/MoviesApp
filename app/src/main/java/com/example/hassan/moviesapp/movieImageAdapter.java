package com.example.hassan.moviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Hassan on 9/23/2016.
 */

public class movieImageAdapter extends BaseAdapter {
    private Context context;
    public List<Movie> movieImageItems;

    public movieImageAdapter(Context context, List<Movie> movieImageItems) {
        this.context = context;
        this.movieImageItems = movieImageItems;
    }

    @Override
    public int getCount() {
        return movieImageItems.size();

    }

    @Override
    public Movie getItem(int position) {
        return movieImageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {

        ImageView imageview;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, null);

            holder.imageview = (ImageView) convertView.findViewById(R.id.movieImage);
            convertView.setTag(holder);


        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(context).load("http://image.tmdb.org/t/p/w342/" + getItem(position).poster_path).into(holder.imageview);
        return convertView;
    }

}


