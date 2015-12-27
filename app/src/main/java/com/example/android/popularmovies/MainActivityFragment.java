package com.example.android.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    MovieTilesAdapter mMovieTilesAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieTilesAdapter = new MovieTilesAdapter(getActivity(),R.layout.movie_tile,new ArrayList<MovieData>());
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(mMovieTilesAdapter);
        gridView.setNumColumns((Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) ? 2 : 4);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detailIntent = new Intent(getActivity(),DetailActivity.class);
                MovieData movieData = mMovieTilesAdapter.getItem(i);
                detailIntent.putExtra("movie_data",movieData);
                startActivity(detailIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        FetchPopularMoviesTask fetchPopularMoviesTask = new FetchPopularMoviesTask();
        fetchPopularMoviesTask.execute("popularity.desc");
    }

    public class FetchPopularMoviesTask extends AsyncTask<String,Void,ArrayList<MovieData>> {

        @Override
        protected ArrayList<MovieData> doInBackground(String... sort_by) {
            final String API_KEY_PARAM = "api_key";
            final String SORT_BY_PARAM = "sort_by";
            final String API_KEY = getString(R.string.tmdb_api_key);
            final String BASE_URI = "http://api.themoviedb.org/3/discover/movie";
            final String LOG_TAG = this.getClass().getSimpleName();
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String popularMovies = null;
            try {
                Uri uri = Uri.parse(BASE_URI).buildUpon().appendQueryParameter(SORT_BY_PARAM,sort_by[0]).appendQueryParameter(API_KEY_PARAM,API_KEY).build();
                URL url = new URL(uri.toString());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                if(inputStream == null) {
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuffer stringBuffer = new StringBuffer();

                if ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                popularMovies = stringBuffer.toString();
                Log.v(LOG_TAG,popularMovies);
                try {
                    return MovieDataParser.fetch(popularMovies);
                } catch (Exception e) {
                    Log.e(LOG_TAG,e.getMessage(),e);
                    e.printStackTrace();
                    return null;
                }


            } catch (IOException e) {
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
                return null;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG,e.getMessage(),e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> moviesData) {
            super.onPostExecute(moviesData);
            if (moviesData != null) {
                mMovieTilesAdapter.clear();
                mMovieTilesAdapter.addAll(moviesData);
                mMovieTilesAdapter.notifyDataSetChanged();
            }
        }
    }
}
