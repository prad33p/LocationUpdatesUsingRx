package com.locationupdateswithrx.locationupdateswithrx;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import io.nlopez.smartlocation.rx.ObservableFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class LocationUpdatesTest extends AppCompatActivity {

    RxLocationProvider locationGooglePlayServicesProvider;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private PublishSubject<Boolean> publishSubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_updates_test);

        final TextView mLat = (TextView) findViewById(R.id.latitude);
        final TextView mLong = (TextView) findViewById(R.id.longitude);

        //Need to set custom LocationParams for our use . Currently it receives location updates every 500ms. with distance of 0 .

        RxFactory.enableLocation(this).flatMap(new Func1<RxLocationProvider.ProviderWrapper, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(RxLocationProvider.ProviderWrapper providerWrapper) {
                if (providerWrapper.locationStatus == RxLocationProvider.LocationStatus.ENABLED) {
                    return Observable.just(true);
                } else {
                    return publishSubject;
                }
            }
        })
                .filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean;
                    }
                })
                .flatMap(new Func1<Boolean, Observable<Location>>() {
                    @Override
                    public Observable<Location> call(Boolean aBoolean) {
                            return ObservableFactory.from(SmartLocation.with(LocationUpdatesTest.this)
                                    .location(new LocationGooglePlayServicesProvider()).config(LocationParams.LAZY));

                    }
                })
//                .first()
//                .timeout(10, TimeUnit.SECONDS)
//                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Location>>() {
//                    @Override
//                    public Observable<? extends Location> call(Throwable throwable) {
//                        Log.d("Location","Timeout, fetching again.");
//                        return ObservableFactory.from(SmartLocation.with(LocationUpdatesTest.this)
//                                .location(new LocationGooglePlayServicesProvider()).config(LocationParams.NAVIGATION));
//                    }
//                })
                .subscribe(new Subscriber<Location>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Location location) {
                Log.d("Location","Location fixed.");

                mLat.setText(Double.toString(location.getLatitude()));
                mLong.setText(Double.toString(location.getLongitude()));
            }
        });

        /*RxFactory.enableLocation(this).subscribe(new Subscriber<RxLocationProvider.ProviderWrapper>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(RxLocationProvider.ProviderWrapper providerWrapper) {
                System.out.print(providerWrapper.locationStatus + " - Values");
                locationGooglePlayServicesProvider = providerWrapper.locationProvider;
            }
        });*/
    }


    @Override
    protected void onStart() {
        super.onStart();


        /*locationGooglePlayServicesProvider = new LocationGooglePlayServicesProvider();
        locationGooglePlayServicesProvider.setCheckLocationSettings(true);

        Subscription subscription = ObservableFactory.from(SmartLocation.with(this)
                .location(locationGooglePlayServicesProvider).config(LocationParams.NAVIGATION))
                .switchMap(new Func1<Location, Observable<List<Address>>>() {
                    @Override
                    public Observable<List<Address>> call(Location location) {
                        return ObservableFactory.fromLocation(LocationUpdatesTest.this, location, 5);
                    }
                }).subscribe(new Subscriber<List<Address>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLat.setText(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Address> o) {
                        StringBuilder sb = new StringBuilder();
                        for (Address address: o) {
                            sb.append(address.getSubLocality()).append("\n\n\n");
                        }
                        mLat.setText(sb.toString());
                    }
                });

        compositeSubscription.add(subscription);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeSubscription.clear();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RxLocationProvider.REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_OK) {
            publishSubject.onNext(true);
        } else if (requestCode == RxLocationProvider.REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_CANCELED) {
            publishSubject.onNext(false);
        }

//        locationGooglePlayServicesProvider.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
