package com.mp.runand.app.logic.training;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognitionClient;

/**
 * Created by Sebastian on 2014-11-27.
 */
public class ActivityRecongnition implements GooglePlayServicesClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int DETECTION_INTERVAL_SECONDS = 2;
    public static final int DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;

    private enum REQUEST_TYPE {START, STOP}
    private REQUEST_TYPE requestType;

    /*
     * Store the PendingIntent used to send activity recognition events
     * back to the app
     */

    private PendingIntent pendingIntent;
    //Store the current activity recognition client
    private ActivityRecognitionClient activityRecognitionClient;
    private boolean inProgress;
    private Activity activity;

    public ActivityRecongnition(Activity activity) {
        inProgress = false;
        this.activity = activity;
        activityRecognitionClient = new ActivityRecognitionClient(activity, this, this);
        Intent intent = new Intent(activity, ActivityRecognitionIntentService.class);
        pendingIntent = PendingIntent.getService(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Request activity recognition updates based on the current
     * detection interval.
     *
     */
    public void startUpdates() {
        if(!inProgress && activityRecognitionClient != null) {
            inProgress = true;
            activityRecognitionClient.connect();
            requestType = REQUEST_TYPE.START;
        }
    }

    public void stopUpdates() {
        if(!inProgress && activityRecognitionClient != null) {
            inProgress = true;
            activityRecognitionClient.connect();
            requestType = REQUEST_TYPE.STOP;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        switch(requestType) {
            case START:
                /*
                * Request activity recognition updates using the preset
                * detection interval and PendingIntent. This call is
                * synchronous.
                */
                activityRecognitionClient.requestActivityUpdates(DETECTION_INTERVAL_MILLISECONDS, pendingIntent);
                break;
            case STOP:
                activityRecognitionClient.removeActivityUpdates(pendingIntent);
                break;
        }
        /*
         * Since the preceding call is synchronous, turn off the
         * in progress flag and disconnect the client
         */
        inProgress = false;
        activityRecognitionClient.disconnect();
    }

    @Override
    public void onDisconnected() {
        inProgress = false;
        activityRecognitionClient = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Turn off the request flag
        inProgress = false;
        if(connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch(IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode, activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if(dialog != null) {
                dialog.show();
            }
        }
    }
}
