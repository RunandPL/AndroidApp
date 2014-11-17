package com.mp.runand.app.logic.mapsServices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.mp.runand.app.logic.training.MessagesList;
import com.mp.runand.app.logic.training.TrainingConstants;

import java.util.ArrayList;

/**
 * Created by Sebastian on 2014-11-14.
 */
public class RouteFollowService extends Service {
    public static final String ACTION = "RouteFollowService";
    private final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int SUFFICIENT_ACCURACY = 10;
    private final int meterToNextPoint = 5;
    private ArrayList<Location> routeToFollow;
    private LocationManager locationManager = null;
    private boolean startTracking = false;
    private Location currentBestLocation = null;
    private LocationListener locationListener = null;
    private int nextLocation = 0;
    private float maxDistance = 0;
    private State state;
    private boolean finished = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, ACTION + " Stopped", Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        routeToFollow  = intent.getParcelableArrayListExtra(TrainingConstants.ROUTE_TO_FOLLOW);
        Toast.makeText(getBaseContext(), ACTION + " Started", Toast.LENGTH_SHORT).show();
        state = State.START;
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
                    startTracking = true;
                }
                if(startTracking) {
                    checkPosition(location);
                }
            }
        };
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, locationListener);
        return START_NOT_STICKY;
    }

    private void checkPosition(Location location) {
        if(!finished && isBetterLocation(location)) {
            if (state == State.START) {
                float distance = location.distanceTo(routeToFollow.get(0));
                if (distance <= 3) {
                    MessagesList.getInstance().putMessages("Jesteś na starcie");
                    calculateNextPoint(location);
                }
            } else if (state == State.MIDDLE) {
                if (location.distanceTo(routeToFollow.get(nextLocation)) > maxDistance) {
                    MessagesList.getInstance().putMessages("Oddalasz się");
                } else {
                    maxDistance = location.distanceTo(routeToFollow.get(nextLocation));
                    if (maxDistance <= 3) {
                        calculateNextPoint(location);
                    }
                }
            } else if (state == State.END) {
                if (location.distanceTo(routeToFollow.get(nextLocation)) < 3) {
                    MessagesList.getInstance().putMessages("Dobiegłes do końca");
                    finished = true;
                }
            }
        }
    }

    private void calculateNextPoint(Location location) {
        boolean nextIsLast = true;
        maxDistance = meterToNextPoint + location.getAccuracy() + 5;
        int lastPoint = nextLocation + 10;
        if(lastPoint > routeToFollow.size())
            lastPoint = routeToFollow.size();
        for(int i = nextLocation; i < lastPoint; i++) {
            if(location.distanceTo(routeToFollow.get(i)) >= maxDistance) {
                nextLocation = i;
                nextIsLast = false;
                break;
            }
        }
        if(nextIsLast || nextLocation == routeToFollow.size() - 1) {
            state = State.END;
        } else {
            state = State.MIDDLE;
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

    private enum State {
        START, MIDDLE, END;
    }
}
