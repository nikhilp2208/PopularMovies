package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
public class MainActivityFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    MovieTilesAdapter mMovieTilesAdapter;
    SharedPreferences mPreferences;
    String mSortPref;
    int mCurrentPosition;
    private static final String CURSOR_CURRENT_POS = "current_pos_cursor";

    private static final int MOVIES_LOADER = 0;
    private static final int FAV_MOVIES_LOADER = 1;
//MoviesContract.MoviesEntry.TABLE_NAME + "." +
    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_POSTER_PATH,
            MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT,
            MoviesContract.MoviesEntry.COLUMN_POPULARITY,
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_POSTER_PATH = 4;
    static final int COL_BACKDROP_PATH = 5;
    static final int COL_RELEASE_DATE = 6;
    static final int COL_VOTE_AVERAGE = 7;
    static final int COL_VOTE_COUNT = 8;
    static final int COL_POPULARITY = 9;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieTilesAdapter = new MovieTilesAdapter(getActivity(),null,0);
        if (savedInstanceState != null && savedInstanceState.containsKey(CURSOR_CURRENT_POS)) {
            mCurrentPosition = savedInstanceState.getInt(CURSOR_CURRENT_POS);
        }
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(mMovieTilesAdapter);
        gridView.setNumColumns((Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) ? 2 : 3);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent detailIntent = new Intent(getActivity(),DetailActivity.class);
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    mCurrentPosition = i;
                    ((Callback) getActivity()).onItemSelected(MoviesContract.MoviesEntry.buildMoviesUriWithMovieId(cursor.getString(COL_MOVIE_ID)));
                }
//                MovieData movieData = mMovieTilesAdapter.getItem(i);
//                detailIntent.putExtra(getString(R.string.parcellable_movie_data),movieData);
//                startActivity(detailIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        getLoaderManager().initLoader(FAV_MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        mPreferences.registerOnSharedPreferenceChangeListener(listener);
//        mSortPref = mPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        mSortPref = Utility.getSortOrder(getActivity());
        FetchPopularMoviesTask fetchPopularMoviesTask = new FetchPopularMoviesTask();
        fetchPopularMoviesTask.execute(mSortPref);
    }

    @Override
    public void onResume() {
        super.onResume();
        String sortOrder = Utility.getSortOrder(getActivity());
        if (sortOrder != null && !sortOrder.equals(mSortPref)) {
            mSortPref = sortOrder;
            onSortPrefChanged(sortOrder);
        } else {
            GridView gridView = (GridView) getActivity().findViewById(R.id.movies_grid);
            gridView.smoothScrollToPosition(mCurrentPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentPosition!= GridView.INVALID_POSITION) {
            outState.putInt(CURSOR_CURRENT_POS,mCurrentPosition);
        }
        super.onSaveInstanceState(outState);
    }

    LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = this;

//    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//        @Override
//        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//            if(s == getString(R.string.pref_sort_key)) {
//                mSortPref = mPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
//                if (mSortPref.equals("favorite")) {
////                    Cursor cursor = getContext().getContentResolver().query(MoviesContract.FavoriteMoviesEntry.CONTENT_URI,null,null,null,null);
////                    mMovieTilesAdapter.swapCursor(cursor);
//                    getLoaderManager().restartLoader(FAV_MOVIES_LOADER,null, mLoaderCallbacks);
//                } else {
//                    FetchPopularMoviesTask fetchPopularMoviesTask = new FetchPopularMoviesTask();
//                    fetchPopularMoviesTask.execute(mSortPref);
//                    getLoaderManager().restartLoader(MOVIES_LOADER,null, mLoaderCallbacks);
//                }
//            }
//        }
//    };

    public void onSortPrefChanged(String sortPref) {
        if (sortPref.equals("favorite")) {
//                    Cursor cursor = getContext().getContentResolver().query(MoviesContract.FavoriteMoviesEntry.CONTENT_URI,null,null,null,null);
//                    mMovieTilesAdapter.swapCursor(cursor);
            getLoaderManager().restartLoader(FAV_MOVIES_LOADER,null, mLoaderCallbacks);
        } else {
            FetchPopularMoviesTask fetchPopularMoviesTask = new FetchPopularMoviesTask();
            fetchPopularMoviesTask.execute(sortPref);
            getLoaderManager().restartLoader(MOVIES_LOADER,null, mLoaderCallbacks);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIES_LOADER:
                return new CursorLoader(getActivity(), MoviesContract.MoviesEntry.CONTENT_URI,MOVIE_COLUMNS,null,null,null);
            case FAV_MOVIES_LOADER:
                return new CursorLoader(getActivity(), MoviesContract.FavoriteMoviesEntry.CONTENT_URI,MOVIE_COLUMNS,null,null,null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case MOVIES_LOADER: {
                Log.v("ON_MOVIES_LOAD_FINISHED", Integer.toString(data.getCount()));
                if (!mSortPref.equals("favorite"))
                    mMovieTilesAdapter.swapCursor(data);
                break;
            }
            case FAV_MOVIES_LOADER: {
                if (mSortPref.equals("favorite")) {
                    Log.v("ON_FAV_LOAD_FINISHED", Integer.toString(data.getCount()));
                    mMovieTilesAdapter.swapCursor(null);
                    mMovieTilesAdapter.swapCursor(data);
                }
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieTilesAdapter.swapCursor(null);
    }

    public class FetchPopularMoviesTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... sort_by) {
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

                contentValuesVector.add(movieValues);

            }

            int inserted = 0;
            int deleted = 0;
            deleted = getContext().getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI,null,null);
            Log.d("DB_DELETE", "DB delete completed.: " + deleted);
            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValues = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValues);

                inserted = getContext().getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI,contentValues);
            }
            Log.d("DB_INSERT", "DB insert completed. count: " + inserted);
        }
    }

    public interface Callback {
        public void onItemSelected(Uri movieUri);
    }
}
