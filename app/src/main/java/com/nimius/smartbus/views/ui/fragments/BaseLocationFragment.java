package com.nimius.smartbus.views.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.nimius.smartbus.R;
import com.nimius.smartbus.views.utility.EasyPermissions;
import com.nimius.smartbus.views.utility.Utils;

import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class BaseLocationFragment extends BaseFragment {

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 60 * 1000;  /*  10*1000= 10 secs */
    private long FASTEST_INTERVAL = UPDATE_INTERVAL / 2; /* 2000=2 sec */
    private Context mContext;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * INNIT VARIABLES
     */
    @Override
    public void initVariable() {
    }

    @Override
    public void loadData() {

    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                locationCallback,
                Looper.myLooper() /* Looper */);


//        getFusedLocationProviderClient(mContext).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
    }


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            // do work here
            onLocationChanged(locationResult.getLastLocation());
        }
    };

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated LocationModel: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Log.e(TAG, "onLocationChanged: msg=" + msg);
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void onLocationPermissionGranted() {

    }


    /**
     * LOCATION PERMISSION
     */
    public void locationPermission() {
        /***
         * permission for location
         */
        EasyPermissions.requestPermissions(mContext, new EasyPermissions.PermissionCallbacks() {
            @Override
            public void onPermissionsGranted(int requestCode, List<String> perms) {
                Log.e(TAG, "onPermissionsGranted:");
                /**start location update service**/

                startLocationUpdates();
                final LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Utils.buildAlertMessageNoGps(mContext);
                }



            }

            @Override
            public void onPermissionsDenied(int requestCode, List<String> perms) {
                Log.e(TAG, "onPermissionsDenied:");
                showSnackBar(mContext, getResources().getString(R.string.msg_location_permission_denied));

            }

            @Override
            public void onPermissionsPermanentlyDeclined(int requestCode, List<String> perms) {
                Log.e(TAG, "onPermissionsPermanentlyDeclined:");
                showPermissionDialog(getResources().getString(R.string.msg_permanent_denied_phone_permission), mContext);

            }
        }, getResources().getString(R.string.msg_location), 100, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: LocationModel update is stop");
        getFusedLocationProviderClient(mContext).removeLocationUpdates(locationCallback);
    }
}
