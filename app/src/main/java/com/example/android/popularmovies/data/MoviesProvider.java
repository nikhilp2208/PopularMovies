package com.example.android.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by nikhil.p on 17/02/16.
 */
public class MoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mMoviesDbHelper;

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;
    public static final int FAV_MOVIES = 200;
    public static final int FAV_MOVIE_WITH_ID = 201;

    private static final String sMovieIdSelection = MoviesContract.MoviesEntry.TABLE_NAME + "." +
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sFavMovieIdSelection = MoviesContract.FavoriteMoviesEntry.TABLE_NAME + "." +
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ";

    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
            {
                retCursor = mMoviesDbHelper.getReadableDatabase().query(MoviesContract.MoviesEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            case MOVIE_WITH_ID: {
                String movieId = MoviesContract.MoviesEntry.getMovieIdFromUri(uri);
                retCursor = mMoviesDbHelper.getReadableDatabase().query(MoviesContract.MoviesEntry.TABLE_NAME,projection,sMovieIdSelection, new String[] {movieId}, null,null,sortOrder);
                break;
            }
            case FAV_MOVIES:
            {
                retCursor = mMoviesDbHelper.getReadableDatabase().query(MoviesContract.FavoriteMoviesEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            case FAV_MOVIE_WITH_ID: {
                String movieId = MoviesContract.FavoriteMoviesEntry.getMovieIdFromUri(uri);
                retCursor = mMoviesDbHelper.getReadableDatabase().query(MoviesContract.FavoriteMoviesEntry.TABLE_NAME,projection,sFavMovieIdSelection, new String[] {movieId}, null,null,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME,null,contentValues);
                if (_id > 0)
                    returnUri = MoviesContract.MoviesEntry.buildMoviesUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAV_MOVIES: {
                long _id = db.insert(MoviesContract.FavoriteMoviesEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = MoviesContract.FavoriteMoviesEntry.buildMoviesUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                Log.d("INSERTED", "Inserted id:"+_id);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        final SQLiteDatabase sqLiteDatabase = mMoviesDbHelper.getWritableDatabase();
        int _id;

        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                _id = sqLiteDatabase.delete(MoviesContract.MoviesEntry.TABLE_NAME, s, strings);
                break;
            }
            case FAV_MOVIES: {
                _id = sqLiteDatabase.delete(MoviesContract.FavoriteMoviesEntry.TABLE_NAME, s, strings);
                break;
            }
            case FAV_MOVIE_WITH_ID: {
                String movieId = MoviesContract.FavoriteMoviesEntry.getMovieIdFromUri(uri);
                _id = sqLiteDatabase.delete(MoviesContract.FavoriteMoviesEntry.TABLE_NAME, sFavMovieIdSelection, new String[] {movieId});
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return _id;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase sqLiteDatabase = mMoviesDbHelper.getWritableDatabase();
        int count;

        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                count = sqLiteDatabase.update(MoviesContract.MoviesEntry.TABLE_NAME, contentValues, s, strings);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for(ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME,null,value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES+"/*", MOVIE_WITH_ID);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_FAV_MOVIES, FAV_MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_FAV_MOVIES+"/*", FAV_MOVIE_WITH_ID);

        return uriMatcher;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mMoviesDbHelper.close();
        super.shutdown();
    }
}
