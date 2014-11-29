package com.mp.runand.app.logic.mapsServices;


import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.mp.runand.app.logic.training.ActivityRecognitionIntentService;
import com.mp.runand.app.logic.training.MessagesReader;
import com.mp.runand.app.logic.training.TrainingConstants;

/**
 * Created by Sebastian on 2014-10-09.
 */
public class GpsService extends Service {
    public static final String ACTION = "GPS_ACTION";
    private final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int SUFFICIENT_ACCURACY = 100;
    private ArrayList<Location> locations = new ArrayList<Location>();;
    private LocationManager locationManager = null;
    private boolean startTracking = false;
    private Location currentBestLocation = null;
    private LocationListener locationListener = null;
    private float length = 0;
    private int burnedCalories = 0;
    private long startTime = 0;
    private long stopTime = 0;
    private Location lastLocation = null;
    private MessagesReader messagesReader;
    private long delta;
    private long t1;
    private long t2;
    private MyReciver myReciver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //Send tracked positions back to main activity
        stopTime = System.currentTimeMillis();
        sendTrainingData();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(locationListener);
        messagesReader.terminate();
        unregisterReciver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        t1 = System.currentTimeMillis();
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        messagesReader = new MessagesReader(getBaseContext());
        new Thread(messagesReader).start();
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
                    if(!startTracking) {
                        sendData("Rozpoczęcie Namierzania");
                        startTime = System.currentTimeMillis();
                    }
                    startTracking = true;
                }
                if(startTracking) {
                    updatePositionsList(location);
                }
                //Pomiar czasu
                t2 = System.currentTimeMillis();
                delta += t2 - t1;
                t1 = t2;
                if(delta == 20000) {
                    delta = 0;
                    toJestTaFunkcjaKtoraChcialesZebymZaimplementowalCoSieWykonujeCo20SekundMamNadziejeZeChodziloCiOCosTakiegoJakNieToNapiszIDajMiZnacToWtedyCosZmienieChociazZPoczatkuPewnieNieBedeWiedzialCoAleSieCosZmieniRunAndBrunieckiDupaKamieniKupaNieChceMiSieJuzTejInzynierkiPisacIOgolnieJestemZmeczonyFunctionHaHaHaHaHaHa();
                }
            }
        };
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, locationListener);
        registerReciver();
        return START_NOT_STICKY;
    }

    private void registerReciver() {
        myReciver = new MyReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActivityRecognitionIntentService.NAME);
        registerReceiver(myReciver, intentFilter);
    }

    private void unregisterReciver() {
        unregisterReceiver(myReciver);
        myReciver = null;
    }

    private void sendData(String data) {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra("GPS_INFO", data);
        sendBroadcast(intent);
    }

    private void sendTrainingData() {
        //Countin training time
        long timeDiffrence = stopTime - startTime;
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putParcelableArrayListExtra(TrainingConstants.POSITIONS, locations);
        intent.putExtra(TrainingConstants.TRAINING_TIME, timeDiffrence);
        intent.putExtra(TrainingConstants.TRAININ_LENGTH, Math.round(length));
        intent.putExtra(TrainingConstants.BURNED_CALORIES, burnedCalories);
        sendBroadcast(intent);
    }


    private void updatePositionsList(Location location) {
        if(isBetterLocation(location)) {
            locations.add(location);
            if(lastLocation == null)
                lastLocation = location;
            else {
                length += lastLocation.distanceTo(location);
                lastLocation = location;
            }
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

    private void toJestTaFunkcjaKtoraChcialesZebymZaimplementowalCoSieWykonujeCo20SekundMamNadziejeZeChodziloCiOCosTakiegoJakNieToNapiszIDajMiZnacToWtedyCosZmienieChociazZPoczatkuPewnieNieBedeWiedzialCoAleSieCosZmieniRunAndBrunieckiDupaKamieniKupaNieChceMiSieJuzTejInzynierkiPisacIOgolnieJestemZmeczonyFunctionHaHaHaHaHaHa() {

    }

    private class MyReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra(TrainingConstants.ACTIVITY_TYPE);
            //If data is null this means that we get tracked positions from GpsService
            if(data != null) {
                Toast.makeText(GpsService.this, data, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
