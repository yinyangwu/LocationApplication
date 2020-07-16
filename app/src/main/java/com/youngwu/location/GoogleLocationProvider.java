package com.youngwu.location;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * @author jinpingchen
 * @date 2019/3/6
 */
public class GoogleLocationProvider implements LocationProvider, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GoogleLocationProvider.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient fusedLocationClient;
    private XLocationListener mLocationListener;

    private static Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = null;

    @Override
    public void startGetLocation(Context context) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void startGetLocation(Context context, int time) {
        startGetLocation(context);
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                stopGetLocation();
            }
        }, time);
    }

    @Override
    public void stopGetLocation() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void setLocationListener(XLocationListener locationListener) {
        mLocationListener = locationListener;
    }

    @Override
    public void removeLocationListener(XLocationListener locationListener) {
        mLocationListener = locationListener;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mGoogleApiClient.getContext());
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.e(TAG, "onLocationResult");
            if (mLocationListener != null) {
                if (locationResult != null) {
                    mLocationListener.onLocation(locationResult.getLastLocation());
                } else {
                    mLocationListener.onFail("onLocationResult:failed");
                }
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            Log.e(TAG, "onLocationAvailability");
            if (mLocationListener != null) {
                mLocationListener.onFail(locationAvailability.toString());
            }
        }
    };

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
        if (mLocationListener != null) {
            mLocationListener.onFail("onConnectionSuspended");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed" + connectionResult.getErrorMessage());
        if (mLocationListener != null) {
            mLocationListener.onFail("onConnectionFailed");
        }
    }
}
