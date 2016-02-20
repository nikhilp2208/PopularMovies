package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.MoviesContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    MovieTilesAdapter mMovieTilesAdapter;
    SharedPreferences mPreferences;
    String mSortPref;

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
                detailIntent.putExtra(getString(R.string.parcellable_movie_data),movieData);
                startActivity(detailIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
        mSortPref = mPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        FetchPopularMoviesTask fetchPopularMoviesTask = new FetchPopularMoviesTask();
        fetchPopularMoviesTask.execute(mSortPref);
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if(s == getString(R.string.pref_sort_key)) {
                mSortPref = mPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
                FetchPopularMoviesTask fetchPopularMoviesTask = new FetchPopularMoviesTask();
                fetchPopularMoviesTask.execute(mSortPref);
            }
        }
    };

    public class FetchPopularMoviesTask extends AsyncTask<String,Void,ArrayList<MovieData>> {

        @Override
        protected ArrayList<MovieData> doInBackground(String... sort_by) {
            final String API_KEY_PARAM = getString(R.string.api_key_param);
            final String SORT_BY_PARAM = getString(R.string.sort_by_param);
            final String API_KEY = getString(R.string.tmdb_api_key);
            final String BASE_URI = getString(R.string.themoviedb_base_uri);
            final String LOG_TAG = this.getClass().getSimpleName();
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String popularMovies = null;
            try {
                Uri uri = Uri.parse(BASE_URI).buildUpon().appendQueryParameter(SORT_BY_PARAM,sort_by[0]).appendQueryParameter(API_KEY_PARAM,API_KEY).build();
                URL url = new URL(uri.toString());
                Log.v("URL",uri.toString());
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
                    ArrayList<MovieData> moviesArray = MovieDataParser.fetch(popularMovies, getContext());
                    addMoviesToDb(moviesArray);
                    return null;
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

        private void addMoviesToDb(ArrayList<MovieData> moviesData) {
            Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(moviesData.size());

            for (MovieData movieData : moviesData) {
                ContentValues movieValues = new ContentValues();

                movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movieData.movie_id);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movieData.title);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movieData.overview);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, movieData.poster_path);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH, movieData.backdrop_path);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movieData.release_date);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movieData.vote_average);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT, movieData.vote_count);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, movieData.popularity);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, 0);

                contentValuesVector.add(movieValues);

            }

            int inserted = 0;
            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValues = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValues);

                inserted = getContext().getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI,contentValues);
            }
            Log.d("DB_INSERT", "DB insert completed. count: " + inserted);
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
