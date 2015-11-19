package com.locationupdateswithrx.locationupdateswithrx;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.rx.ObservableFactory;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

public class LocationUpdatesTest extends AppCompatActivity {

    private Observable<Location> locationObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_updates_test);

        final TextView mLat = (TextView) findViewById(R.id.latitude);
        final TextView mLong = (TextView) findViewById(R.id.longitude);


        //Need to set custom LocationParams for our use . Currently it receives location updates every 500ms. with distance of 0 .

        locationObservable = ObservableFactory.from(SmartLocation.with(getApplicationContext()).location().config(LocationParams.NAVIGATION));
        locationObservable.subscribe(new Subscriber<Location>() {
            @Override
            public void onCompleted() {
                Log.d("On Completed","All observable items received. No more items will be thrown by observable now.");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("On Error","Opps !!, something got fucked up.");
            }

            @Override
            public void onNext(Location location) {
                mLat.setText(Double.toString(location.getLatitude()));
                mLong.setText(Double.toString(location.getLongitude()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_updates_test, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
