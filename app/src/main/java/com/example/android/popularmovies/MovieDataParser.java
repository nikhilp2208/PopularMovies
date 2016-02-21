package com.example.android.popularmovies;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nikhil.p on 27/12/15.
 */
public class MovieDataParser {
    private static final String KEY_RESULTS = "results";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_POPULARITY = "popularity";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_IMAGE_SIZE = "w185";
    private static final String BACKDROP_IMAGE_SIZE = "w342";
    private static final String LOG_TAG = "MovieDataParser";

    public static ArrayList<MovieData> fetch(String popularMoviesJson,Context context) {
        ArrayList<MovieData> movieDataList = new ArrayList<MovieData>();
        if (popularMoviesJson == null) {
            return null;
        }
        try {
            JSONObject popularMovies = new JSONObject(popularMoviesJson);
            JSONArray popularMoviesList = popularMovies.getJSONArray(KEY_RESULTS);
            int moviesCount = popularMoviesList.length();
            for (int i = 0; i < moviesCount ; i++ ) {
                MovieData movieData = new MovieData();
                JSONObject movieJsonObject = popularMoviesList.getJSONObject(i);
                if (movieJsonObject.getString(KEY_POSTER_PATH) == "null") {
                    movieData.poster_path = context.getString(R.string.default_image);
                } else {
                    movieData.poster_path = IMAGE_BASE_URL+POSTER_IMAGE_SIZE+movieJsonObject.getString(KEY_POSTER_PATH);
                }
                movieData.overview = movieJsonObject.getString(KEY_OVERVIEW);
                movieData.release_date = movieJsonObject.getString(KEY_RELEASE_DATE);
                movieData.movie_id = movieJsonObject.getString(KEY_ID);
                movieData.title = movieJsonObject.getString(KEY_TITLE);
                movieData.backdrop_path = IMAGE_BASE_URL+BACKDROP_IMAGE_SIZE+movieJsonObject.getString(KEY_BACKDROP_PATH);
                movieData.popularity = Float.parseFloat(movieJsonObject.getString(KEY_POPULARITY));
                movieData.vote_count = Integer.parseInt(movieJsonObject.getString(KEY_VOTE_COUNT));
                movieData.vote_average = Float.parseFloat(movieJsonObject.getString(KEY_VOTE_AVERAGE));
                movieDataList.add(movieData);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }


        return movieDataList;
    }
}
