package com.mp.runand.app.logic.training;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Sebastian on 2014-11-27.
 */
public class ActivityRecognitionIntentService extends IntentService {
    public static final String NAME = "ActivityRecognitionIntentService";

    public ActivityRecognitionIntentService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //If the incoming intent gas result
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            //Get the most probable activity
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            /*
             * Get the probability that this activity is the
             * the user's actual activity
             */
            int confidence = mostProbableActivity.getConfidence();
            //Get integer describing activity type
            int activityType = mostProbableActivity.getType();
            String activityName = getNameFromType(activityType);
            Intent messageIntent = new Intent();
            messageIntent.setAction(NAME);
            messageIntent.putExtra(TrainingConstants.ACTIVITY_TYPE, activityName);
            sendBroadcast(messageIntent);
        }
    }

    /**
     * Map detected activity type to String
     * @param activityType detected activity type
     * @return A user-readable name for the type
     */
    private String getNameFromType(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.RUNNING:
                return "running";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }
}
