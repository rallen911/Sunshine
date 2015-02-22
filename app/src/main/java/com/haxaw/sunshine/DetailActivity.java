package com.haxaw.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsActivityIntent;
            settingsActivityIntent = new Intent( this, SettingsActivity.class);
            startActivity(settingsActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String FORECAST_SHARE_HASTAG = " #SunshineApp";
        private String mForecastStr;

        public DetailFragment() {
            setHasOptionsMenu( true );
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();

            if(( intent != null ) &&
                    ( intent.hasExtra( Intent.EXTRA_TEXT )))
            {
                mForecastStr = intent.getStringExtra( intent.EXTRA_TEXT );
                ((TextView) rootView.findViewById( R.id.detail_text )).setText( mForecastStr );
            }

            return rootView;
        }

        private Intent createShareForecastIntent()
        {
            Intent shareIntent = new Intent( Intent.ACTION_SEND );
            shareIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET );
            shareIntent.setType("text/plain");
            shareIntent.putExtra( Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASTAG );

            return shareIntent;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate( R.menu.menu_detailfragment, menu );

            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem( R.id.action_share );

            // Get the provider and hold onto it to set/change the share intent.
            ShareActionProvider mShareActionProvider;
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider( item );

            // Attach an intent to this ShareActionProvider.  You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            if( mShareActionProvider != null )
            {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
            else
            {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }
    }
}
