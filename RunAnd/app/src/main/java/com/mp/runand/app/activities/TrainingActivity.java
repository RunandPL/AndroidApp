package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.NameBuilder;
import com.mp.runand.app.logic.database.DatabaseUtils;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.Track;
import com.mp.runand.app.logic.entities.Training;
import com.mp.runand.app.logic.mapsServices.GpsService;
import com.mp.runand.app.logic.mapsServices.RouteFollowService;
import com.mp.runand.app.logic.network.JSONRequestBuilder;
import com.mp.runand.app.logic.network.LiveTrainingManager;
import com.mp.runand.app.logic.network.TrackSender;
import com.mp.runand.app.logic.network.TrainingSender;
import com.mp.runand.app.logic.training.ActivityRecongnition;
import com.mp.runand.app.logic.training.ServiceCheckTask;
import com.mp.runand.app.logic.training.TrainingConstants;
import com.mp.runand.app.logic.training.TrainingImage;

import java.sql.Date;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TrainingActivity extends Activity {
    private final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String NAME = "trainingActivity";

    @InjectView(R.id.startTrainingButton) Button startButton;

    @OnClick(R.id.startTrainingButton)
    void startButtonOnClick(Button button) {
        startTraining();
        //Start service checking route follow
        if(isRouteTraining) {
            Intent intent = new Intent(getBaseContext(), RouteFollowService.class);
            intent.putParcelableArrayListExtra(TrainingConstants.ROUTE_TO_FOLLOW, routeToFollow);
            startService(intent);
        }
        trainingStarted = true;
        endOfTraining = false;
        positionsOK  = false;
        activityRecongnition.startUpdates();
    }

    @InjectView(R.id.stopTrainingButton) Button stopButton;

    @OnClick(R.id.stopTrainingButton)
    void stopButtonOnClick() {
        stopTraining();
        if(isRouteTraining)
            stopService(new Intent(getBaseContext(), RouteFollowService.class));
        trainingStarted = false;
        endOfTraining = true;
        activityRecongnition.stopUpdates();
        stopService(new Intent(this, GpsService.class));
    }

    @InjectView(R.id.takePictureButton)
    Button takePictureButton;

    @OnClick(R.id.takePictureButton)
    void takePictuteButtonOnClick(Button button) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            Uri uri = Uri.fromFile(NameBuilder.createImageFile(Environment.getExternalStorageDirectory(), currentUser.getUserName()));
            lastPicturePath = uri.toString();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /*@InjectView(R.id.debugMap)
    Button debugMap;

    @OnClick(R.id.debugMap)
    void debugMapOnClick(Button button) {
        Intent intent = new Intent(this, MapDebug.class);
        startActivity(intent);
    }*/

    private boolean endOfTraining = false;
    private boolean trainingStarted = false;
    private boolean positionsOK = false;

    private MyReciver myReciver;

    private ArrayList<Location> locations = null;
    private int trackLength;
    private long trainingTime;
    private int burnedCalories;

    private Training training;

    //Training configuration
    boolean isRouteTraining;
    boolean isUserLoggedIn;
    private ArrayList<Location> routeToFollow = null;
    private ActivityRecongnition activityRecongnition;
    private DataBaseHelper databaseHelper;
    private String lastPicturePath;
    private ArrayList<String> imagePaths;
    private ArrayList<Location> imagesLocations;
    private ArrayList<TrainingImage> images;
    private Location lastLocation;
    private CurrentUser currentUser;
    private boolean trainingResolved = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        ButterKnife.inject(this);


        Intent intent = getIntent();
        isRouteTraining = intent.getBooleanExtra(TrainingConstants.IS_ROUTE_TRAINING, false);
        isUserLoggedIn = intent.getBooleanExtra(TrainingConstants.IS_USER_LOGGED_IN, false);

        if(isRouteTraining)
            routeToFollow = intent.getParcelableArrayListExtra(TrainingConstants.ROUTE_TO_FOLLOW);

        activityRecongnition = new ActivityRecongnition(this);
        databaseHelper = DataBaseHelper.getInstance(this);
        imagePaths = new ArrayList<String>();
        imagesLocations = new ArrayList<Location>();
        images = new ArrayList<TrainingImage>();
        currentUser = databaseHelper.getCurrentUser();
        setButtonsEnabled(false);
        ServiceCheckTask serviceCheckTask = new ServiceCheckTask(this);
        serviceCheckTask.execute();
    }

    public void checkTrainingStatus() {
        Intent intent = new Intent();
        intent.setAction(NAME);
        intent.putExtra("COMMAND", "SEND_TRAINING_STATUS");
        sendBroadcast(intent);
        Log.e("TA", "Check Training Status");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void startTraining() {
        Intent intent =  new Intent();
        intent.setAction(NAME);
        intent.putExtra("SET_TRAINING", "START");
        sendBroadcast(intent);
        trainingStarted = true;
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        takePictureButton.setVisibility(View.VISIBLE);
    }

    private void stopTraining() {
        Intent intent = new Intent();
        intent.setAction(NAME);
        intent.putExtra("SET_TRAINING", "STOP");
        sendBroadcast(intent);
        trainingStarted = false;
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);
        takePictureButton.setVisibility(View.GONE);
    }

    private void saveTrainingToDatabase() {
        if(positionsOK) {
            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getBaseContext());
            Location area = new Location("none");
            area.setLatitude(12.0);
            area.setLongitude(45.0);
            long trainingID;
            if(isUserLoggedIn) {
                if (!isRouteTraining) {
                    Track track = new Track(new Date(System.currentTimeMillis()), locations, trackLength, 1, 1, area);
                    training = new Training(currentUser.getEmailAddress(), trainingTime, track, burnedCalories, 0.0);
                    trainingID = dataBaseHelper.addTraining(training, images);
                } else {
                    Track track = dataBaseHelper.getTrack(getIntent().getIntExtra("trackID", -1));
                    training = new Training(currentUser.getEmailAddress(), trainingTime, track, burnedCalories, 0.0);
                    trainingID = dataBaseHelper.addTrainingOnExistingTrack(training, getIntent().getIntExtra("trackID", -1), images);
                }
                training.setLengthTime((int) trainingID);
                Toast.makeText(getBaseContext(), "Zapisano Trening", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        myReciver = new MyReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GpsService.ACTION);
        registerReceiver(myReciver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myReciver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            TrainingImage trainingImage = new TrainingImage();
            trainingImage.setLocation(lastLocation);
            trainingImage.setImage(lastPicturePath);
            trainingImage.setBase64(DatabaseUtils.ImageToBase64(lastPicturePath));
            images.add(trainingImage);
            lastPicturePath = "";
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putBoolean("SERVICE_STARTED", trainingStarted);
        savedInstanceState.putBoolean("END_OF_TRAINING", endOfTraining);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        trainingStarted = savedInstanceState.getBoolean("SERVICE_STARTED");
        endOfTraining = savedInstanceState.getBoolean("END_OF_TRAINING");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setButtonsEnabled(boolean enabled) {
        if(!trainingStarted) {
            startButton.setVisibility(View.GONE);
            if(enabled) {
                startButton.setVisibility(View.VISIBLE);
                takePictureButton.setVisibility(View.VISIBLE);
            } else {
                startButton.setVisibility(View.GONE);
                takePictureButton.setVisibility(View.GONE);
            }

        }
    }

    private void getTrainingData(Intent intent) {
        locations  = intent.getParcelableArrayListExtra(TrainingConstants.POSITIONS);
        burnedCalories = intent.getIntExtra(TrainingConstants.BURNED_CALORIES, 0);
        trackLength = intent.getIntExtra(TrainingConstants.TRAININ_LENGTH, 0);
        trainingTime = intent.getLongExtra(TrainingConstants.TRAINING_TIME, 0);
        if(locations != null && locations.size() > 0) {
            positionsOK = true;
            saveTrainingToDatabase();
            //Starting activity to summup training
            if(currentUser != null) {
                new LiveTrainingManager(this, currentUser).execute(JSONRequestBuilder.buildStopLiveTrainingRequestAsJson(training));
                new TrackSender(this, currentUser).execute(training.getTrack());
                new TrainingSender(this, currentUser).execute(training);
            }
        }
    }

    private void resolveEventType(int eventType) {
        if(!trainingStarted) {
            switch (eventType) {
                case GpsStatus.GPS_EVENT_STARTED:
                    Toast.makeText(this, "Gps Searching", Toast.LENGTH_LONG).show();
                   // setButtonsEnabled(false);
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Toast.makeText(this, "Gps Stopped", Toast.LENGTH_SHORT).show();
                    //setButtonsEnabled(false);
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Toast.makeText(this, "Gps Fixed", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    }

    private void setLastLocation(Location location) {
        if(trainingResolved) {
            if (lastLocation == null && !trainingStarted) {
                Log.e("TA", "New Training");
                lastLocation = location;
                if(currentUser != null) {
                    new LiveTrainingManager(TrainingActivity.this, currentUser)
                            .execute(JSONRequestBuilder.buildStartLiveTrainingRequestAsJson(lastLocation.getLatitude(), lastLocation.getLongitude(), 0));
                }
                    return;
            }
            lastLocation = location;
        }
    }

    private void resolveTrainingStatus(boolean status) {
        trainingResolved = true;
        Log.e("RES", String.valueOf(status));
        trainingStarted = status;
        if(status) {
            startButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.VISIBLE);
            takePictureButton.setVisibility(View.VISIBLE);
        } else {
            startButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.GONE);
            takePictureButton.setVisibility(View.GONE);
        }
    }

    private class MyReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //If data is null this means that we get tracked positions from GpsService
            if(intent.hasExtra("GPS_INFO")) {
                String data = intent.getStringExtra("GPS_INFO");
                Toast.makeText(TrainingActivity.this, data, Toast.LENGTH_SHORT).show();
                return;
            } else if(intent.hasExtra("EVENT_TYPE")) {
                int eventType = intent.getIntExtra("EVENT_TYPE", -1);
                resolveEventType(eventType);
            } else if(intent.hasExtra("LAST_LOCATION")) {
                setLastLocation((Location) intent.getParcelableExtra("LAST_LOCATION"));
            } else if(intent.hasExtra("TRAINING_STATUS")) {
                resolveTrainingStatus(intent.getBooleanExtra("TRAINING_STATUS", false));
            }
            getTrainingData(intent);
        }
    }
}
