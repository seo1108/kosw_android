package kr.rapids.altimeasure;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;


/**
 * Created by kimminjib on 2018. 10. 29..
 */

public class BLocationManager {


    public interface DelegateFindLocation {
        public void  findLocation(Location loc, Address addr);
    }

    private Location curLocation = null ;
    public LocationManager mLM;
    public Context appContext;
    public DelegateFindLocation mDelegateFindLocation  = null ;

    public BLocationManager(MainActivity context ) {
        mLM = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        appContext = context;
        //registerLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    public void registerLocationUpdates() {

        Log.v("kmj", "start GPS");

        mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, mLocationListener);

        mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, mLocationListener);
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
                if (addr != null) {
                    Log.v("kmj", addr.getAddressLine(0));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mDelegateFindLocation != null && addr != null) {

                if (isBetterLocation(location,curLocation) ) {
                    curLocation = location ;
                    mDelegateFindLocation.findLocation(location, addr);
                }
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


    @SuppressLint("MissingPermission")
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


    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}
