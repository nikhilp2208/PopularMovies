package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nikhil.p on 08/02/16.
 */
public class ReviewDataParser {
    private static final String KEY_RESULTS = "results";
    private static final String KEY_REVIEW_ID = "id";
    private static final String KEY_REVIEW_AUTHOR = "author";
    private static final String KEY_REVIEW_CONTENT = "content";
    private static final String KEY_REVIEW_URL = "url";
    private static final String LOG_TAG = "ReviewDataParser";

    public static ArrayList<ReviewData> fetch(String movieReviewsResponse,Context context) {
        ArrayList<ReviewData> reviewList = new ArrayList<ReviewData>();
        if (movieReviewsResponse == null) {
            return null;
        }
        try {
            JSONObject trailers = new JSONObject(movieReviewsResponse);
            JSONArray trailersList = trailers.getJSONArray(KEY_RESULTS);
            int trailersCount = trailersList.length();
            for (int i = 0; i < trailersCount ; i++ ) {
                ReviewData reviewData = new ReviewData();
                JSONObject trailerJsonObject = trailersList.getJSONObject(i);
                reviewData.review_id = trailerJsonObject.getString(KEY_REVIEW_ID);
                reviewData.author = trailerJsonObject.getString(KEY_REVIEW_AUTHOR);
                reviewData.content = trailerJsonObject.getString(KEY_REVIEW_CONTENT);
                reviewData.url = trailerJsonObject.getString(KEY_REVIEW_URL);
                reviewList.add(reviewData);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
        return reviewList;
    }
}