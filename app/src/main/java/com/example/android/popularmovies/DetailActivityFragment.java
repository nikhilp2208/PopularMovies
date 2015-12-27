package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        Activity activity = getActivity();
        Intent intent = activity.getIntent();
        MovieData movie = (intent.getParcelableExtra("movie_data"));
        TextView titleTextView = (TextView)view.findViewById(R.id.Title);
        TextView releaseTextView = (TextView)view.findViewById(R.id.release_date);
        TextView ratingTextView = (TextView)view.findViewById(R.id.rating);
        TextView descrptionTextView = (TextView)view.findViewById(R.id.description);
        ImageView backDropImageView = (ImageView)view.findViewById(R.id.backDropImageView);
        ImageView posterImageView = (ImageView)view.findViewById(R.id.posterImageView);
        Context context = getContext();
        Picasso.with(context).load(movie.backdrop_path).into(backDropImageView);
        Picasso.with(context).load(movie.poster_path).into(posterImageView);
        titleTextView.setText(movie.title);
        releaseTextView.setText("Release Date: " + movie.release_date);
        ratingTextView.setText(Float.toString(movie.vote_average));
        descrptionTextView.setText(movie.overview);

        return view;
    }
}
