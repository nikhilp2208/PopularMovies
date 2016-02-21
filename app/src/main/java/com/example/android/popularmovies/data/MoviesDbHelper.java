package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nikhil.p on 17/02/16.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_OVERVIEW + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT + " INTEGER, " +
                MoviesContract.MoviesEntry.COLUMN_POPULARITY + " REAL);";

        final String SQL_CREATE_FAV_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.FavoriteMoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_OVERVIEW + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT + " INTEGER, " +
                MoviesContract.MoviesEntry.COLUMN_POPULARITY + " REAL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAV_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoriteMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
