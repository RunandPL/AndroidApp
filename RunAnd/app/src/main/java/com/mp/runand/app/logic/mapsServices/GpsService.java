package com.mp.runand.app.logic.mapsServices;


import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mp.runand.app.logic.database.DataBaseHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;
/**
 * Created by Sebastian on 2014-10-09.
 */
public class GpsService extends Service {
    public static final String ACTION = "GPS_ACTION";
    private final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int SUFFICIENT_ACCURACY = 10;
    private Marker marker = null;
    private List<LatLng> positionList;
    private LocationManager locationManager = null;
    private boolean startTracking = false;
    private Location currentBestLocation = null;
    private LocationListener locationListener = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //Send tracked positions back to main activity
        sendTrackPositions();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        positionList = new ArrayList<LatLng>();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(Location location) {
                if(location.getAccuracy() < SUFFICIENT_ACCURACY) {
                    if(!startTracking)
                        sendData("Rozpoczęcie Namierzania");
                    startTracking = true;
                }
                if(startTracking) {
                    updatePositionsList(location);
                }
            }
        };
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, locationListener);
        return START_STICKY;
    }

    private void sendData(String data) {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra("GPS_INFO", data);
        sendBroadcast(intent);
    }

    private void sendTrackPositions() {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra("POSITIONS", getPositionsAsDouble());
        sendBroadcast(intent);
    }

    private double[] getPositionsAsDouble() {
        double[] positionsTable = new double[positionList.size() * 2];
        for(int i = 0; i < positionList.size(); i++) {
            positionsTable[2 * i] = positionList.get(i).latitude;
            positionsTable[2 * i + 1] = positionList.get(i).longitude;
        }
        return positionsTable;
    }


    private void updatePositionsList(Location location) {
        if(isBetterLocation(location)) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            positionList.add(latLng);
        }
    }

    private boolean isBetterLocation(Location location) {
        if(currentBestLocation == null)
            return true;
        //Sprawdzam czy lokalizacja jest nowsza czy starsza
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if(isSignificantlyNewer)
            return true;
        else if(isSignificantlyOlder)
            return false;

        //Sprawdzam czy lokalizacja jest dokładniejsza
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAcurate = accuracyDelta > 200;

        if(isMoreAccurate)
            return true;
        else if (isNewer && !isLessAccurate)
            return true;
        return false;
    }

}
