package com.example.android.popularmovies;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nikhil.p on 08/02/16.
 */
public class TrailerDataParser {
    private static final String KEY_RESULTS = "results";
    private static final String KEY_TRAILER_PATH = "key";
    private static final String KEY_TRAILER_CAPTION = "name";
    private static final String KEY_TRAILER_ID = "id";
    private static final String LOG_TAG = "MovieDataParser";

    public static ArrayList<TrailerData> fetch(String movieTrailersResponse,Context context) {
        ArrayList<TrailerData> trailerList = new ArrayList<TrailerData>();
        if (movieTrailersResponse == null) {
            return null;
        }
        try {
            JSONObject trailers = new JSONObject(movieTrailersResponse);
            JSONArray trailersList = trailers.getJSONArray(KEY_RESULTS);
            int trailersCount = trailersList.length();
            for (int i = 0; i < trailersCount ; i++ ) {
                TrailerData trailerData = new TrailerData();
                JSONObject trailerJsonObject = trailersList.getJSONObject(i);
                trailerData.name = trailerJsonObject.getString(KEY_TRAILER_CAPTION);
                trailerData.youtubePath = trailerJsonObject.getString(KEY_TRAILER_PATH);
                trailerData.trailer_id = trailerJsonObject.getString(KEY_TRAILER_ID);
                trailerList.add(trailerData);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
        return trailerList;
    }
}