package com.haxaw.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private String mLocation;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mLocation = Utility.getPreferredLocation( this );

        setContentView(R.layout.activity_main);
        if( findViewById( R.id.weather_detail_container ) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if( savedInstanceState == null )
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else
        {
            mTwoPane = false;
        }
    }

    @Override
    public void onStart( )
    {
        Log.d(LOG_TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onPause( )
    {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onResume( )
    {
        Log.d(LOG_TAG, "onResume");
        super.onResume();

        String location = Utility.getPreferredLocation( this );

        // Update the location in our second pane using the fragment manager
        if( location != null && !location.equals( mLocation ))
        {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager()
                    .findFragmentById( R.id.fragment_forecast );

            if( ff != null )
            {
                ff.onLocationChanged();
            }

            DetailFragment df = (DetailFragment)getSupportFragmentManager()
                    .findFragmentByTag( DETAILFRAGMENT_TAG );

            if( df != null )
            {
                df.onLocationChanged( location );
            }

            mLocation = location;
        }
    }

    @Override
    public void onItemSelected( Uri contentUri )
    {
        if( mTwoPane == true )
        {
            // If two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable( DetailFragment.DETAIL_URI, contentUri );

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments( args );

            getSupportFragmentManager().beginTransaction()
                    .replace( R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG )
                    .commit();
        }
        else
        {
            Intent intent = new Intent( this, DetailActivity.class )
                    .setData( contentUri );

            startActivity( intent );
        }
    }

    @Override
    public void onStop( )
    {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy( )
    {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch( id )
        {
            case R.id.action_settings:
                Intent settingsActivityIntent;
                settingsActivityIntent = new Intent( this, SettingsActivity.class);
                startActivity(settingsActivityIntent);
                return true;

            case R.id.action_map:
                openPreferredLocationInMap();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Show the user's preferred location on the map
    private void openPreferredLocationInMap()
    {
        Uri geoLocation;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
        String location = preferences.getString(
                getString( R.string.pref_location_key ),
                getString( R.string.pref_location_default ));

        String mapStr = "geo:0,0?";
        geoLocation = Uri.parse( mapStr ).buildUpon().appendQueryParameter("q", location ).build();

        Intent intent = new Intent( Intent.ACTION_VIEW );

        intent.setData( geoLocation );
        PackageManager pm = getPackageManager();

        if( intent.resolveActivity( pm ) != null )
        {
            startActivity( intent );
        }
    }
}
