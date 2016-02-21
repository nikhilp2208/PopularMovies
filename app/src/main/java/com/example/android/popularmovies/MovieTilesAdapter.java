package com.example.android.popularmovies;

import java.util.ArrayList;
import java.util.Collection;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by nikhil.p on 13/12/15.
 */
public class MovieTilesAdapter extends CursorAdapter {
    public MovieTilesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        int layout_id = R.layout.movie_tile;
        View view = LayoutInflater.from(context).inflate(layout_id, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String url = cursor.getString(MainActivityFragment.COL_POSTER_PATH);
        Picasso.with(context).load(url).into(viewHolder.imageView);
    }

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.movie_tile_image_view);
        }
    }

//    private final Context context;
//    private final ArrayList<MovieData> moviesData;
//    private final int movieLayout;
//
//    @Override
//    public MovieData getItem(int i) {
//        return moviesData.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return getItem(i).movie_id;
//    }
//
//    public MovieTilesAdapter(Activity context, int layoutResource ,ArrayList<MovieData> moviesData) {
////        super(context,R.layout.fragment_main);
//        this.movieLayout = layoutResource;
//        this.moviesData = moviesData;
//        this.context = context;
//    }
//
//    @Override
//    public int getCount() {
//        return moviesData.size();
//    }
//
//    public void addAll(Collection<MovieData> items) {
//        moviesData.addAll(items);
//        notifyDataSetChanged();
//    }
//
//    public void clear() {
//        moviesData.clear();
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(movieLayout, parent, false);
//        }
//        ImageView view = (ImageView) convertView.findViewById(R.id.movie_tile_image_view);
//        String url = getItem(position).poster_path;
//        Log.v(this.getClass().getSimpleName(),url);
//        Log.v(this.getClass().getSimpleName(),Integer.toString(position));
//        Picasso.with(context).load(url).into(view);
//        return convertView;
//    }
}