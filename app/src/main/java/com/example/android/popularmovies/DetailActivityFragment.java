package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

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
public class DetailActivityFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{
    ShareActionProvider mShareActionProvider;
    TrailerListAdapter mTrailerListAdapter;
    MovieData movie;
    ArrayList<TrailerData> mTrailers;
    ArrayList<ReviewData> mReviews;
    Uri mUri;
    Cursor mCursor;
    String YOUTUBE_BASE_URL = "www.youtube.com/watch?v=";
    String mYoutubeUrl;

    public static final String DETAIL_URI = "detail_uri";

    private static final int DETAIL_LOADER = 1;

    private static final String[] DETAIL_COLUMNS = {
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
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


    TextView mTitleTextView;
    TextView mReleaseTextView;
    TextView mRatingTextView;
    TextView mDescrptionTextView;
    ImageView mBackDropImageView;
    ImageView mPosterImageView;
    ImageView mFavoriteView;

    Boolean mIsFavorite;

    public DetailActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        Activity activity = getActivity();
//        Intent intent = activity.getIntent();
//        movie = (intent.getParcelableExtra(getString(R.string.parcellable_movie_data)));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        if (mShareActionProvider != null && mYoutubeUrl != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
        else {
            Log.d("DetailActivity","Not able to get Action provider");
        }
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Watch this trailer on Youtube: " + mYoutubeUrl);
        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
        } else {
            mUri = getActivity().getIntent().getData();
        }
//        Activity activity = getActivity();
//        Intent intent = activity.getIntent();
//        MovieData movie = (intent.getParcelableExtra(getString(R.string.parcellable_movie_data)));
        mTitleTextView = (TextView)view.findViewById(R.id.Title);
        mReleaseTextView = (TextView)view.findViewById(R.id.release_date);
        mRatingTextView = (TextView)view.findViewById(R.id.rating);
        mDescrptionTextView = (TextView)view.findViewById(R.id.description);
        mBackDropImageView = (ImageView)view.findViewById(R.id.backDropImageView);
        mPosterImageView = (ImageView)view.findViewById(R.id.posterImageView);
        mFavoriteView = (ImageView)view.findViewById(R.id.favorite_image_view);
        mFavoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView = (ImageView) view;
                if(!mIsFavorite) {
                    imageView.setImageResource(android.R.drawable.btn_star_big_on);
                    insertFavoriteToDb();
                    mIsFavorite = true;
                } else {
                    imageView.setImageResource(android.R.drawable.btn_star_big_off);
                    removeFavoriteFromDb();
                    mIsFavorite = false;
                }
            }
        });
//        mTrailerListAdapter = new TrailerListAdapter(getActivity(),R.layout.trailer,new ArrayList<TrailerData>());
//        ListView listView = (ListView) view.findViewById(R.id.trailers_listview);
//        listView.setAdapter(mTrailerListAdapter);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (null != mUri) {
            return new CursorLoader(getActivity(),mUri,DETAIL_COLUMNS,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mCursor = data;
            Context context = getContext();
            Picasso.with(context).load(data.getString(COL_BACKDROP_PATH)).into(mBackDropImageView);
            Picasso.with(context).load(data.getString(COL_POSTER_PATH)).into(mPosterImageView);
            mTitleTextView.setText(data.getString(COL_TITLE));
            mReleaseTextView.setText(getString(R.string.release_date) + data.getString(COL_RELEASE_DATE));
            mRatingTextView.setText(Float.toString(data.getFloat(COL_VOTE_AVERAGE)));
            mDescrptionTextView.setText(data.getString(COL_OVERVIEW));
            FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
            FetchReviewsTask fetchReviewsTask = new FetchReviewsTask();
            String movieId = data.getString(COL_MOVIE_ID);
            fetchTrailerTask.execute(movieId);
            fetchReviewsTask.execute(movieId);
            mIsFavorite = checkiFMovieiSFavorite(movieId);
            if (mIsFavorite) {
                mFavoriteView.setImageResource(android.R.drawable.btn_star_big_on);
            }
        }
    }


    public Boolean checkiFMovieiSFavorite(String movieId) {
        Uri uri = MoviesContract.FavoriteMoviesEntry.buildMoviesUriWithMovieId(movieId);
        Cursor favMovieCursor = getContext().getContentResolver().query(uri,null,null,null,null);
        if (favMovieCursor.getCount() == 1)
            return true;
        return false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void insertFavoriteToDb() {
        ContentValues contentValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(mCursor,contentValues);
        contentValues.remove(MoviesContract.MoviesEntry._ID);
        getContext().getContentResolver().insert(MoviesContract.FavoriteMoviesEntry.CONTENT_URI,contentValues);
    }

    private void removeFavoriteFromDb() {
        String movieId = mCursor.getString(COL_MOVIE_ID);
        getContext().getContentResolver().delete(MoviesContract.FavoriteMoviesEntry.buildMoviesUriWithMovieId(movieId),null,null);
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
                if(mTrailers != null)
                    mYoutubeUrl = YOUTUBE_BASE_URL + mTrailers.get(0).youtubePath;

                if (mShareActionProvider!= null){
                    mShareActionProvider.setShareIntent(createShareTrailerIntent());
                }
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


    public class FetchReviewsTask extends AsyncTask<String,Void,ArrayList<ReviewData>>{

        @Override
        protected ArrayList<ReviewData> doInBackground(String... movieIds) {
            final String API_KEY_PARAM = getString(R.string.api_key_param);
            final String API_KEY = getString(R.string.tmdb_api_key);
            final String BASE_URI = getString(R.string.themoviedb_trailers_base_uri);
            final String LOG_TAG = this.getClass().getSimpleName();
            final String REVIEWS_PATH = "reviews";
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            String movieReviewsResponse = null;

            try {
                Uri uri = Uri.parse(BASE_URI).buildUpon().appendPath(movieIds[0]).appendPath(REVIEWS_PATH).appendQueryParameter(API_KEY_PARAM,API_KEY).build();
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

                movieReviewsResponse = stringBuffer.toString();
                Log.v(LOG_TAG,movieReviewsResponse);
                try {
                    return ReviewDataParser.fetch(movieReviewsResponse,getActivity());
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
        protected void onPostExecute(ArrayList<ReviewData> reviewData) {
            super.onPostExecute(reviewData);
            if (reviewData != null) {
                mReviews = reviewData;
                LinearLayout reviewContainerLayout = (LinearLayout) getActivity().findViewById(R.id.reviews_container);
                for(int i = 0; i < mReviews.size();i++) {
                    LinearLayout linearLayout = new LinearLayout(getActivity());
                    final TextView authorTextView = new TextView(getActivity());
                    final TextView contentTextView = new TextView(getActivity());
                    authorTextView.setPadding(5, 5, 5, 5);
                    authorTextView.setTextSize(20);
                    contentTextView.setPadding(5, 5, 5, 10);

                    final View horizontalSeparatorView = new View(getActivity());
                    horizontalSeparatorView.setBackgroundColor(Color.parseColor("#ddcccc"));
                    horizontalSeparatorView.setMinimumHeight(1);

                    linearLayout.setId(i);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.addView(horizontalSeparatorView);
                    linearLayout.addView(authorTextView);
                    linearLayout.addView(contentTextView);
                    authorTextView.setText(mReviews.get(i).author);
                    contentTextView.setText(mReviews.get(i).content);
                    reviewContainerLayout.addView(linearLayout);
                }
            }
        }
    }
}
