package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nikhil.p on 21/02/16.
 */
public class Utility {
    public static String getSortOrder(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = sharedPreferences.getString(context.getString(R.string.pref_sort_key),context.getString(R.string.pref_sort_default));
        return sortOrder;
    }
}
