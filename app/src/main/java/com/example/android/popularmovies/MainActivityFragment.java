package com.example.android.popularmovies;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
//        String[] strings = {"http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","http://image.tmdb.org/t/p/w185/D6e8RJf2qUstnfkTslTXNTUAlT.jpg"};
//        List<String> urlList = new ArrayList<String>(Arrays.asList(strings));
        mMovieTilesAdapter = new MovieTilesAdapter(getActivity(),R.layout.movie_tile,new ArrayList<String>());
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(mMovieTilesAdapter);
        gridView.setNumColumns((Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) ? 2 : 4);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        FetchPopularMoviesTask fetchPopularMoviesTask = new FetchPopularMoviesTask();
        fetchPopularMoviesTask.execute("popularity.desc");
    }

    public class FetchPopularMoviesTask extends AsyncTask<String,Void,String[]> {

        private String[] getPosterUrls(String popularMoviesJsonString) throws JSONException {
            final String PM_RESULT = "results";
            final String PM_POSTER_PATH = "poster_path";
            final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
            JSONObject popularMovies = new JSONObject(popularMoviesJsonString);
            JSONArray popularMoviesList = popularMovies.getJSONArray(PM_RESULT);
            int moviesCount = popularMoviesList.length();
            String[] posterUrls = new String[moviesCount];
            Log.v("POSTERURL_COUNT",Integer.toString(moviesCount));
            for (int i = 0; i < moviesCount ; i++ ) {
                JSONObject movieDetails = popularMoviesList.getJSONObject(i);
                posterUrls[i] = IMAGE_BASE_URL+movieDetails.getString(PM_POSTER_PATH);
                Log.v("POSTER_URL",posterUrls[i]);
            }
            return posterUrls;
        }

        @Override
        protected String[] doInBackground(String... sort_by) {
            final String API_KEY_PARAM = "api_key";
            final String SORT_BY_PARAM = "sort_by";
            final String API_KEY = "API_KEY";
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
                    return getPosterUrls(popularMovies);
                } catch (JSONException e) {
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
        protected void onPostExecute(String[] imageUrls) {
            super.onPostExecute(imageUrls);
            if (imageUrls != null) {
                List<String> urlList = new ArrayList<String>(Arrays.asList(imageUrls));
                mMovieTilesAdapter.clear();
                mMovieTilesAdapter.addAll(urlList);
                mMovieTilesAdapter.notifyDataSetChanged();
            }
        }
    }
}
