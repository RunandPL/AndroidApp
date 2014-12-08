package com.mp.runand.app.logic.mapsServices;


import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.mp.runand.app.activities.TrainingActivity;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.network.CurrentLocationSender;
import com.mp.runand.app.logic.network.JSONRequestBuilder;
import com.mp.runand.app.logic.training.ActivityRecognitionIntentService;
import com.mp.runand.app.logic.training.MessagesReader;
import com.mp.runand.app.logic.training.TrainingConstants;

/**
 * Created by Sebastian on 2014-10-09.
 */
public class GpsService extends Service implements GpsStatus.Listener {
    public static final String ACTION = "GPS_ACTION";
    private final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private static final int SUFFICIENT_ACCURACY = 20;
    private static double METRIC_RUNNING_FACTOR = 1.02784823;
    private ArrayList<Location> locations = new ArrayList<Location>();
    private LocationManager locationManager = null;
    private boolean startTracking = false;
    private LocationListener locationListener = null;
    private float length = 0;
    private int burnedCalories = 0;
    private long startTime = 0;
    private long stopTime = 0;
    private long trainingTime = 0;
    private double pace;
    private Location lastLocation = null;
    private MessagesReader messagesReader;
    private long delta;
    private long t1;
    private long t2;
    private MyReceiver myReceiver;
    private boolean trainingStarted = false;
    private CurrentUser currentUser;
    private DataBaseHelper databaseHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        locationManager.removeGpsStatusListener(this);
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(locationListener);
        unregisterReciver();
    }

    private void startTraining() {
        messagesReader = new MessagesReader(getBaseContext());
        new Thread(messagesReader).start();
        t1 = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        trainingStarted = true;
        locations = new ArrayList<Location>();
        length = 0;
        burnedCalories = 0;
        lastLocation = null;
        databaseHelper = DataBaseHelper.getInstance(this);
        currentUser = databaseHelper.getCurrentUser();
        Toast.makeText(this, "Training Started", Toast.LENGTH_SHORT).show();
    }

    private void stopTraining() {
        stopTime = System.currentTimeMillis();
        //Send tracked positions back to main activity
        sendTrainingData();
        messagesReader.terminate();
        trainingStarted = false;
        Toast.makeText(this, "Training Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReciver();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.addGpsStatusListener(this);
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
                    }
                    startTracking = true;
                }
                if(startTracking && trainingStarted) {
                    updatePositionsList(location);
                } else if(startTracking) {
                    setKnownLocation(location);
                }
                //Pomiar czasu
                t2 = System.currentTimeMillis();
                delta += t2 - t1;
                t1 = t2;
                if(delta > 5000) {
                    delta = 0;
                    sendLocationPeriod();
                }
            }
        };
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 2000, 0, locationListener);
    }

    private void setKnownLocation(Location location) {
        lastLocation = location;
        sendLastLocation();
    }
    private void registerReciver() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActivityRecognitionIntentService.NAME);
        intentFilter.addAction(TrainingActivity.NAME);
        registerReceiver(myReceiver, intentFilter);
    }

    private void unregisterReciver() {
        unregisterReceiver(myReceiver);
        myReceiver = null;
    }

    private void sendData(String data) {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra("GPS_INFO", data);
        sendBroadcast(intent);
    }

    private void sendTrainingData() {
        trainingTime = stopTime - startTime;
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putParcelableArrayListExtra(TrainingConstants.POSITIONS, locations);
        intent.putExtra(TrainingConstants.TRAINING_TIME, trainingTime);
        intent.putExtra(TrainingConstants.TRAININ_LENGTH, Math.round(length));
        intent.putExtra(TrainingConstants.BURNED_CALORIES, burnedCalories);
        sendBroadcast(intent);
    }


    private void updatePositionsList(Location location) {
            locations.add(location);
            if(lastLocation == null)
                setKnownLocation(location);
            else {
                length += lastLocation.distanceTo(location);
                setKnownLocation(location);
            }
    }

    private void sendLastLocation() {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra("LAST_LOCATION", lastLocation);
        sendBroadcast(intent);
    }

    private void sendLocationPeriod() {
        //stopTime = System.currentTimeMillis();
        //trainingTime += (stopTime - startTime);
        startTime = stopTime;
        if(length != 0) {
            pace = 0;
            pace = ((int) trainingTime / 1000) / length;
        }
        if(lastLocation != null && currentUser != null) {
            Log.e("GPS", "SendLocation");
            new CurrentLocationSender(this, currentUser).execute(JSONRequestBuilder.buildSendCurrentLocationRequestAsJson(lastLocation, burnedCalories,
                    0, 0, length));
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra("EVENT_TYPE", event);
        sendBroadcast(intent);
    }

    private void sendTrainingStatus() {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra("TRAINING_STATUS", trainingStarted);
        sendBroadcast(intent);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra(TrainingConstants.ACTIVITY_TYPE);
            //If data is null this means that we get tracked positions from GpsService
            if(intent.hasExtra("SET_TRAINING")) {
                String setTraining = intent.getStringExtra("SET_TRAINING");
                if(setTraining.equals("START")) {
                    startTraining();
                } else if(setTraining.equals("STOP")) {
                    stopTraining();
                }
            } else if(intent.hasExtra("COMMAND")) {
                String command = intent.getStringExtra("COMMAND");
                if(command.equals("SEND_TRAINING_STATUS"));
                sendTrainingStatus();
            }
        }
    }
}
