package com.locationupdateswithrx.locationupdateswithrx;

import android.content.Context;

import io.nlopez.smartlocation.utils.Logger;
import io.nlopez.smartlocation.utils.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by adarshpandey on 11/24/15.
 */
public class RxFactory {

    public static Observable<RxLocationProvider.ProviderWrapper> enableLocation(final Context context) {
        return Observable.create(new Observable.OnSubscribe<RxLocationProvider.ProviderWrapper>() {
            @Override
            public void call(Subscriber<? super RxLocationProvider.ProviderWrapper> subscriber) {
                RxLocationProvider locationProvider = new RxLocationProvider(subscriber);
                locationProvider.init(context, LoggerFactory.buildLogger(true));
            }
        });
    }
}
