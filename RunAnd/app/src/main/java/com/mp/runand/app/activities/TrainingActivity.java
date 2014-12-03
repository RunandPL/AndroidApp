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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.NameBuilder;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.Track;
import com.mp.runand.app.logic.entities.Training;
import com.mp.runand.app.logic.mapsServices.GpsService;
import com.mp.runand.app.logic.mapsServices.RouteFollowService;
import com.mp.runand.app.logic.network.JSONRequestBuilder;
import com.mp.runand.app.logic.network.LiveTrainingManager;
import com.mp.runand.app.logic.training.ActivityRecongnition;
import com.mp.runand.app.logic.training.TrainingConstants;
import com.mp.runand.app.logic.training.TrainingImage;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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

    @InjectView(R.id.debugMap)
    Button debugMap;

    @OnClick(R.id.debugMap)
    void debugMapOnClick(Button button) {
        Intent intent = new Intent(this, MapDebug.class);
        startActivity(intent);
    }

    private boolean endOfTraining = false;
    private boolean trainingStarted = false;
    private boolean positionsOK = false;

    private MyReciver myReciver;

    private ArrayList<Location> locations = null;
    private int trackLength;
    private long trainingTime;
    private int burnedCalories;
    private long pace;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        ButterKnife.inject(this);

        startService(new Intent(this, GpsService.class));
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, GpsService.class));
    }

    private void startTraining() {
        Intent intent =  new Intent();
        intent.setAction(NAME);
        intent.putExtra("SET_TRAINING", "START");
        sendBroadcast(intent);
        trainingStarted = true;
    }

    private void stopTraining() {
        Intent intent = new Intent();
        intent.setAction(NAME);
        intent.putExtra("SET_TRAINING", "STOP");
        sendBroadcast(intent);
        trainingStarted = false;
    }

    private void saveTrainingToDatabase() {
        if(positionsOK) {
            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getBaseContext());
            CurrentUser currentUser = dataBaseHelper.getCurrentUser();
            Location area = new Location("none");
            area.setLatitude(12.0);
            area.setLongitude(45.0);
            long trainingID;
            if(!isRouteTraining) {
                Track track = new Track(new Date(System.currentTimeMillis()), locations, trackLength, 1, 1, area);
                training = new Training(currentUser.getEmailAddress(), trainingTime, track, burnedCalories, 0.0);
                trainingID = dataBaseHelper.addTraining(training);
            } else {
                Track track = dataBaseHelper.getTrack(getIntent().getIntExtra("trackID", -1));
                training = new Training(currentUser.getEmailAddress(), trainingTime, track, burnedCalories, 0.0);
                trainingID = dataBaseHelper.addTrainingOnExistingTrack(training, getIntent().getIntExtra("trackID", -1));
            }
            for(int i = 0; i < images.size(); i++) {
                dataBaseHelper.addImage(trainingID, images.get(i));
            }
            saveImageToFile(images.get(0).getImage());
            Toast.makeText(getBaseContext(), "Zapisano Trening", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToFile(String path) {
        String base = com.mp.runand.app.logic.database.DatabaseUtils.ImageToBase64(path.substring(7));
        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            File file  = new File(Environment.getExternalStorageDirectory() + "//base64.txt");
            if(!file.exists())
                file.createNewFile();
            fos = new FileOutputStream(file);
            pw = new PrintWriter(Environment.getExternalStorageDirectory() + "//base64.txt");
            pw.write(base);
            //pw.print(base);
            pw.flush();
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null)
                try {
                    pw.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
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
            images.add(trainingImage);
            lastPicturePath = "";
        }
    }

    private void getImageFromFile(TrainingImage trainingImage) {
        File imageFile = new File(lastPicturePath);
        if(!imageFile.exists())
            return;

        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(new FileInputStream(imageFile), 256);
            ByteArrayBuffer baf = new ByteArrayBuffer(256);
            int current;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            //trainingImage.setImage(BitmapFactory.decodeByteArray(baf.toByteArray(),0, baf.length()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void geoTagPicture(TrainingImage trainingImage) {
        try {
            ExifInterface exif = new ExifInterface(lastPicturePath);
            Location location = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);
            trainingImage.setLocation(location);
            int numLat1 =  (int) Math.floor(location.getLatitude());
            int numLat2 = (int) Math.floor((location.getLatitude() - numLat1) * 60);
            double numLat3 = (location.getLatitude() - ((double) numLat1 + ((double) numLat2/60 ))) * 3600000;

            int numLot1 = (int) Math.floor(location.getLongitude());
            int numLot2 = (int) Math.floor((location.getLongitude() - numLot1) * 60);
            double numLot3 = (location.getLongitude() - ((double) numLot1 + ((double) numLot2/60))) * 3600000;

            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, numLat1+"/1,"+numLat2+"/1,"+numLat3+"/1000");
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, numLot1+"/1,"+numLot2+"/1,"+numLot3+"/1000");

            if (location.getLatitude() > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
            }

            if (location.getLongitude() > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        startButton.setEnabled(enabled);
        stopButton.setEnabled(enabled);
        takePictureButton.setEnabled(enabled);
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
            new LiveTrainingManager(this,currentUser).execute(JSONRequestBuilder.buildStopLiveTrainingRequestAsJson(training));
        }
    }

    private void resolveEventType(int eventType) {
        if(!trainingStarted) {
            switch (eventType) {
                case GpsStatus.GPS_EVENT_STARTED:
                    Toast.makeText(this, "Gps Searching", Toast.LENGTH_LONG).show();
                    setButtonsEnabled(false);
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Toast.makeText(this, "Gps Stopped", Toast.LENGTH_SHORT).show();
                    setButtonsEnabled(false);
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Toast.makeText(this, "Gps Fixed", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    }

    private void setLastLocation(Location location) {
        if(lastLocation == null) {
            lastLocation = location;
            new LiveTrainingManager(TrainingActivity.this, currentUser)
                    .execute(JSONRequestBuilder.buildStartLiveTrainingRequestAsJson(lastLocation.getLatitude(),lastLocation.getLongitude(), 0));
            return;
        }
        lastLocation = location;
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
            }
            getTrainingData(intent);
        }
    }
}
