package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by nikhil.p on 08/02/16.
 */
//public class FetchTrailerTask extends AsyncTask<String,Void,ArrayList<TrailerData>>{
//
//    private final Context mContext;
//
//    public FetchTrailerTask(Context context) {
//        mContext = context;
//    }
//
//    @Override
//    protected ArrayList<TrailerData> doInBackground(String... movieIds) {
//        final String API_KEY_PARAM = mContext.getString(R.string.api_key_param);
//        final String API_KEY = mContext.getString(R.string.tmdb_api_key);
//        final String BASE_URI = mContext.getString(R.string.themoviedb_trailers_base_uri);
//        final String LOG_TAG = this.getClass().getSimpleName();
//        final String VIDEOS_PATH = "videos";
//        HttpURLConnection httpURLConnection = null;
//        BufferedReader bufferedReader = null;
//
//        String movieTrailersResponse = null;
//
//        try {
//            Uri uri = Uri.parse(BASE_URI).buildUpon().appendPath(movieIds[0]).appendPath(VIDEOS_PATH).appendQueryParameter(API_KEY_PARAM,API_KEY).build();
//            URL url = new URL(uri.toString());
//            Log.v("URL", uri.toString());
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setRequestMethod("GET");
//            httpURLConnection.connect();
//            InputStream inputStream = httpURLConnection.getInputStream();
//            if(inputStream == null) {
//                return null;
//            }
//            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
//            String line;
//            StringBuffer stringBuffer = new StringBuffer();
//
//            if ((line = bufferedReader.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//
//            movieTrailersResponse = stringBuffer.toString();
//            Log.v(LOG_TAG,movieTrailersResponse);
//            try {
//                return TrailerDataParser.fetch(movieTrailersResponse,mContext);
//            } catch (Exception e) {
//                Log.e(LOG_TAG,e.getMessage(),e);
//                e.printStackTrace();
//                return null;
//            }
//
//
//        } catch (IOException e) {
//            Log.e(LOG_TAG,e.getMessage(),e);
//            e.printStackTrace();
//            return null;
//        } finally {
//            if (httpURLConnection != null) {
//                httpURLConnection.disconnect();
//            }
//
//            if (bufferedReader != null) {
//                try {
//                    bufferedReader.close();
//                } catch (IOException e) {
//                    Log.e(LOG_TAG,e.getMessage(),e);
//                }
//            }
//        }
//
//    }
//}
