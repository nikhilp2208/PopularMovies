package com.example.android.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] strings = {"http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"};
        List<String> urlList = new ArrayList<String>(Arrays.asList(strings));
        MovieTilesAdapter movieTilesAdapter = new MovieTilesAdapter(getActivity(),R.layout.movie_tile,urlList);
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieTilesAdapter);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
