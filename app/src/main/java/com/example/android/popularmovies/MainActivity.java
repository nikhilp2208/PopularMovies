package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String DETAILFRAGMENT_TAG = "DFTAG";
    Boolean mTwoPane;
    String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortOrder = Utility.getSortOrder(this);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG).commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortOrder = Utility.getSortOrder(this);

        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            MainActivityFragment mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if (mainActivityFragment != null) {
                mainActivityFragment.onSortPrefChanged(sortOrder);
            }
            mSortOrder = sortOrder;
        }
        Log.v(LOG_TAG,"Sort order: " + sortOrder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri movieUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI,movieUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,fragment,DETAILFRAGMENT_TAG).commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(movieUri);
            startActivity(intent);
        }
    }
}
