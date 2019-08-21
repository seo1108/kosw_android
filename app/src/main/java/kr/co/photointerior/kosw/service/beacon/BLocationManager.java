package kr.co.photointerior.kosw.service.beacon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.global.KoswApp;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by kimminjib on 2018. 10. 29..
 */

public class BLocationManager {



    public interface DelegateFindLocation {
        public void  findLocation (Location loc,Address addr);
    }

    public LocationManager mLM;
    public Context appContext;
    public DelegateFindLocation mDelegateFindLocation  = null ;

    public BLocationManager(KoswApp app ) {
        mLM = (LocationManager) app.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        appContext = app.getApplicationContext();
        //registerLocationUpdates();
    }

    public BLocationManager(Context app ) {
        mLM = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        appContext = app ;
        //registerLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    public void registerLocationUpdates() {

        Log.v("kmj", "start GPS");

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        String provider = mLM.getBestProvider(criteria, true);

        if (!mLM.isProviderEnabled(provider)
                &&mLM.getLastKnownLocation(provider)!=null) {
            mLM.requestLocationUpdates(
                    provider,
                    1000,
                    10, mLocationListener);
        } else {
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            provider = mLM.getBestProvider(criteria, true);
            mLM.requestLocationUpdates(
                    provider,
                    1000,
                    10, mLocationListener);
        }

        /*

        mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, mLocationListener);

        mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, mLocationListener);

                */
        //1000은 1초마다, 1은 1미터마다 해당 값을 갱신한다는 뜻으로, 딜레이마다 호출하기도 하지만
        //위치값을 판별하여 일정 미터단위 움직임이 발생 했을 때에도 리스너를 호출 할 수 있다.
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            double longitude = location.getLongitude();    //경도
            double latitude = location.getLatitude();         //위도
            float accuracy = location.getAccuracy();        //신뢰도

            Log.v("kmj", String.format("%f, %f, %f", longitude, latitude, accuracy));

            Address addr = null ;
            try {
                addr = getReverseGeocode(latitude,longitude) ;
                //Log.v("kmj",addr.getAddressLine(0)) ;

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mDelegateFindLocation != null) {
                mDelegateFindLocation.findLocation(location,addr);

                mLM.removeUpdates(mLocationListener);
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }


    };


    public Location getLastLocaton() {


        Location lastKnownLocation = null;

        // 수동으로 위치 구하기
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return lastKnownLocation;
        }

        lastKnownLocation = mLM.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLatitude();
            double lat = lastKnownLocation.getLatitude();
            Log.v("kmj", "longtitude=" + lng + ", latitude=" + lat);
        } else {
            Log.v("kmj","no Loaction") ;
        }


        return  lastKnownLocation ;
    }

    public Address  getReverseGeocode(Double lat,Double lng) throws IOException {
        String addr = "" ;

        Geocoder geoCoder = new Geocoder(appContext);
        List<Address> matches = geoCoder.getFromLocation(lat, lng, 1);
        Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
        return  bestMatch ;
    }


    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */


    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(appContext);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(appContext).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        double longitude = location.getLongitude();    //경도
        double latitude = location.getLatitude();         //위도
        float accuracy = location.getAccuracy();        //신뢰도

        Log.v("kmj", String.format("%f, %f, %f", longitude, latitude, accuracy));

        Address addr = null ;
        try {
            addr = getReverseGeocode(latitude,longitude) ;
            //Log.v("kmj",addr.getAddressLine(0)) ;

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mDelegateFindLocation != null) {
            mDelegateFindLocation.findLocation(location,addr);

            mLM.removeUpdates(mLocationListener);
        }



    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(appContext);

        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }
}
