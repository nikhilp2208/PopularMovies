package com.example.android.popularmovies;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by nikhil.p on 13/12/15.
 */
public class MovieTilesAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> urls;
    private final int movieLayout;

    public MovieTilesAdapter(Activity context, int layoutResource ,List<String> movieTilesUrls) {
        super(context,layoutResource,movieTilesUrls);
        this.movieLayout = layoutResource;
        this.urls = movieTilesUrls;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(movieLayout, parent, false);
        }

        ImageView view = (ImageView) convertView.findViewById(R.id.movie_tile_image_view);
        String url = getItem(position);
        Picasso.with(getContext()).load(url).into(view);
        return convertView;
    }
}