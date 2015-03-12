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


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String FORECASTFRAGMENT_TAG = "FFTAG";

    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mLocation = Utility.getPreferredLocation( this );

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG )
                    .commit();
        }

        Log.d(LOG_TAG, "onCreate");
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
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag( FORECASTFRAGMENT_TAG );

            if( ff != null )
            {
                ff.onLocationChanged();
            }

            mLocation = location;
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
