package com.example.hassan.moviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by Hassan on 9/23/2016.
 */
public class ReviewAdapter extends ArrayAdapter<String> {

    private String[] Reviews;

    public ReviewAdapter(final Context context, final String[] reviews) {
        super(context, 0, Arrays.asList(reviews));
        Reviews = reviews;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.trailer_list_item, parent, false);
        }

        final TextView reviewText = (TextView) convertView.findViewById(R.id.trailerTextView);

        reviewText.setText(Reviews[position]);

        return convertView;
    }
}

