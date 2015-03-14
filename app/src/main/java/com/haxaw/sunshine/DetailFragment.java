package com.haxaw.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.TextView;

import com.haxaw.sunshine.data.WeatherContract;
import com.haxaw.sunshine.data.WeatherContract.WeatherEntry;
import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor >
{
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private android.widget.ShareActionProvider mShareActionProvider;
    private String mForecast;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;


    // Collection of views in fragment_detail.xml
    private ImageView   mIconView;
    private TextView    mDateView;
    private TextView    mFriendlyDateView;
    private TextView    mDescriptionView;
    private TextView    mHighTempView;
    private TextView    mLowTempView;
    private TextView    mHumidityView;
    private TextView    mWindView;
    private TextView    mPressureView;

    public DetailFragment() {
        setHasOptionsMenu( true );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate( R.layout.fragment_detail, container, false );

        mIconView = (ImageView) rootView.findViewById( R.id.detail_icon );
        mDateView = (TextView) rootView.findViewById( R.id.detail_date_textview );
        mFriendlyDateView = (TextView) rootView.findViewById( R.id.detail_day_textview );
        mDescriptionView = (TextView) rootView.findViewById( R.id.detail_forecast_textview );
        mHighTempView = (TextView) rootView.findViewById( R.id.detail_high_textview );
        mLowTempView = (TextView) rootView.findViewById( R.id.detail_low_textview );
        mHumidityView = (TextView) rootView.findViewById( R.id.detail_humidity_textview );
        mWindView = (TextView) rootView.findViewById( R.id.detail_wind_textview );
        mPressureView = (TextView) rootView.findViewById( R.id.detail_pressure_textview );

        return rootView;
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
            Log.d( LOG_TAG, "Share Action Provider is null?" );
        }
    }

    private Intent createShareForecastIntent()
    {
        Intent shareIntent = new Intent( Intent.ACTION_SEND );
        shareIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET );
        shareIntent.setType("text/plain");
        shareIntent.putExtra( Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG );

        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated( savedInstanceState );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v( LOG_TAG, "In onLoadFinished" );
        if( data != null && data.moveToFirst())
        {
            // Read weather icon ID from cursor
            int weatherId = data.getInt( COL_WEATHER_CONDITION_ID );

            // Use weather art image
            mIconView.setImageResource( Utility.getArtResourceForWeatherCondition( weatherId ) );

            // Read user preference for metric or imperial temperature units
            boolean isMetric = Utility.isMetric( getActivity() );


            // Read date from cursor and update views for day of week and date
            long date = data.getLong( COL_WEATHER_DATE );
            String friendlyDateText = Utility.getDayName( getActivity(), date );
            String dateText = Utility.getFormattedMonthDay( getActivity(), date );
            // Place day text into mFriendlyDateView
            mFriendlyDateView.setText( friendlyDateText );
            // Place date text into mDateView
            mDateView.setText( dateText );

            // Read weather description from cursor and update view
            String description = data.getString( COL_WEATHER_DESC );
            // Find TextView and set weather forecast description on it
            mDescriptionView.setText( description );

            // Read high temperature from cursor
            double high = data.getDouble( COL_WEATHER_MAX_TEMP );
            // Place high temp text into mHighTempView
            mHighTempView.setText( Utility.formatTemperature( getActivity(), high, isMetric ) );

            // Read low temperature from cursor
            double low = data.getDouble( COL_WEATHER_MIN_TEMP );
            // Place low temp text into mLowTempView
            mLowTempView.setText( Utility.formatTemperature( getActivity(), low, isMetric ) );

            // Read humidity from cursor
            float humidity = data.getFloat( COL_WEATHER_HUMIDITY );
            mHumidityView.setText( getActivity().getString( R.string.format_humidity, humidity ) );

            // Read wind speed and direction from cursor and update view
            float windSpeed = data.getFloat( COL_WEATHER_WIND_SPEED );
            float windDir = data.getFloat( COL_WEATHER_DEGREES );
            mWindView.setText( Utility.getFormattedWind( getActivity(), windSpeed, windDir ) );

            // Read pressure from cursor and update view
            float pressure = data.getFloat( COL_WEATHER_PRESSURE );
            mPressureView.setText( getActivity().getString( R.string.format_pressure, humidity ) );

            // String for the share intent
            mForecast = String.format( "%s - %s - %s/%s", dateText, description, high, low );

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if( mShareActionProvider != null )
            {
                mShareActionProvider.setShareIntent( createShareForecastIntent() );
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

}
