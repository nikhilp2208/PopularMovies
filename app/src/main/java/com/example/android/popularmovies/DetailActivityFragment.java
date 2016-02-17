package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    TrailerListAdapter mTrailerListAdapter;
    MovieData movie;
    ArrayList<TrailerData> mTrailers;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        Intent intent = activity.getIntent();
        movie = (intent.getParcelableExtra(getString(R.string.parcellable_movie_data)));
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
        fetchTrailerTask.execute(Long.toString(movie.movie_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
//        Activity activity = getActivity();
//        Intent intent = activity.getIntent();
//        MovieData movie = (intent.getParcelableExtra(getString(R.string.parcellable_movie_data)));
        TextView titleTextView = (TextView)view.findViewById(R.id.Title);
        TextView releaseTextView = (TextView)view.findViewById(R.id.release_date);
        TextView ratingTextView = (TextView)view.findViewById(R.id.rating);
        TextView descrptionTextView = (TextView)view.findViewById(R.id.description);
        ImageView backDropImageView = (ImageView)view.findViewById(R.id.backDropImageView);
        ImageView posterImageView = (ImageView)view.findViewById(R.id.posterImageView);
        Context context = getContext();
        Picasso.with(context).load(movie.backdrop_path).into(backDropImageView);
        Picasso.with(context).load(movie.poster_path).into(posterImageView);
        titleTextView.setText(movie.title);
        releaseTextView.setText(getString(R.string.release_date) + movie.release_date);
        ratingTextView.setText(Float.toString(movie.vote_average));
        descrptionTextView.setText(movie.overview);

//        mTrailerListAdapter = new TrailerListAdapter(getActivity(),R.layout.trailer,new ArrayList<TrailerData>());
//        ListView listView = (ListView) view.findViewById(R.id.trailers_listview);
//        listView.setAdapter(mTrailerListAdapter);

        return view;
    }

    public class FetchTrailerTask extends AsyncTask<String,Void,ArrayList<TrailerData>>{

        @Override
        protected ArrayList<TrailerData> doInBackground(String... movieIds) {
            final String API_KEY_PARAM = getString(R.string.api_key_param);
            final String API_KEY = getString(R.string.tmdb_api_key);
            final String BASE_URI = getString(R.string.themoviedb_trailers_base_uri);
            final String LOG_TAG = this.getClass().getSimpleName();
            final String VIDEOS_PATH = "videos";
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            String movieTrailersResponse = null;

            try {
                Uri uri = Uri.parse(BASE_URI).buildUpon().appendPath(movieIds[0]).appendPath(VIDEOS_PATH).appendQueryParameter(API_KEY_PARAM,API_KEY).build();
                URL url = new URL(uri.toString());
                Log.v("URL", uri.toString());
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

                movieTrailersResponse = stringBuffer.toString();
                Log.v(LOG_TAG,movieTrailersResponse);
                try {
                    return TrailerDataParser.fetch(movieTrailersResponse,getActivity());
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
        protected void onPostExecute(ArrayList<TrailerData> trailerData) {
            super.onPostExecute(trailerData);
            if (trailerData != null) {
                mTrailers = trailerData;
//                mTrailerListAdapter.clear();
//                mTrailerListAdapter.addAll(trailerData);
//                mTrailerListAdapter.notifyDataSetChanged();
                LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.trailers_container);
                for(int i = 0; i < mTrailers.size();i++) {
                    final TextView textView = new TextView(getActivity());
                    textView.setClickable(true);
                    textView.setLines(1);
                    textView.setPadding(5, 5, 5, 5);
                    textView.setTextSize(30);
                    textView.setId(i);
                    ColorStateList cl = null;
                    try {
                        XmlResourceParser xpp = getResources().getXml(R.color.clickable_test_view_color);
                        cl = ColorStateList.createFromXml(getResources(), xpp);
                    } catch (Exception e) {}
                    textView.setTextColor(cl);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent;
                            TextView tv = (TextView) view;
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + mTrailers.get(tv.getId()).youtubePath));
                            intent.setPackage("com.google.android.youtube");
                            startActivity(intent);
                        }
                    });
                    textView.setText(mTrailers.get(i).name);
                    setCompoundDrawable(textView,R.drawable.play_icon);
                    linearLayout.addView(textView);
                }
            }
        }

        private void setCompoundDrawable(TextView view, int resId) {
            Drawable ratingIcon = ContextCompat.getDrawable(getActivity(), resId);
            Bitmap bitmap = ((BitmapDrawable) ratingIcon).getBitmap();
            ratingIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
            ratingIcon = DrawableCompat.wrap(ratingIcon);
            view.setCompoundDrawablesWithIntrinsicBounds(ratingIcon, null, null, null);
        }
    }
}
