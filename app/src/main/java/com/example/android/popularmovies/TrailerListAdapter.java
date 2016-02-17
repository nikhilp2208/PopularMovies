package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.Inflater;

/**
 * Created by nikhil.p on 08/02/16.
 */
public class TrailerListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<TrailerData> trailerData;
    private final int trailerLayout;

    public TrailerListAdapter(Context context, int trailerLayout, ArrayList<TrailerData> trailerData) {
        this.trailerLayout = trailerLayout;
        this.trailerData = trailerData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return trailerData.size();
    }

    @Override
    public TrailerData getItem(int i) {
        return trailerData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addAll(Collection<TrailerData> items) {
        trailerData.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        trailerData.clear();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(context).inflate(trailerLayout, viewGroup, false);
        }
        TextView textView = (TextView) view.findViewById(R.id.textview_trailer_caption);
        textView.setText(getItem(i).name);
        return view;
    }
}
