package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mp.runand.app.R;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.Track;
import com.mp.runand.app.logic.entities.Training;
import com.mp.runand.app.logic.mapsServices.GpsService;

import java.sql.Date;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TrainingActivity extends Activity {

    @InjectView(R.id.startTrainingButton) Button startButton;
    @InjectView(R.id.stopTrainingButton) Button stopButton;
    @InjectView(R.id.showHistoryButton) Button historyButton;

    private boolean endOfTraining = false;
    private boolean serviceStarted = false;
    private boolean positionsOK = false;

    private MyReciver myReciver;

    private ArrayList<Location> locations = null;
    private float trackLength;
    private long trainingTime;
    private int burnedCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        ButterKnife.inject(this);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startService(new Intent(getBaseContext(), GpsService.class));
                    serviceStarted = true;
                    endOfTraining = false;
                    positionsOK  = false;
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    stopService(new Intent(getBaseContext(), GpsService.class));
                    serviceStarted = false;
                    endOfTraining = true;
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TrainingList.class);
                startActivity(intent);
            }
        });
    }

    private void saveTrainingToDatabase() {
        if(positionsOK) {
            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getBaseContext());
            CurrentUser currentUser = dataBaseHelper.getCurrentUser();
            Location area = new Location("none");
            area.setLatitude(12.0);
            area.setLongitude(45.0);
            Track track = new Track(new Date(System.currentTimeMillis()), locations, trackLength, 1, 1, area);
            Training newTraining = new Training(currentUser.getEmailAddress(), trainingTime, track, burnedCalories, 0.0);
            //dataBaseHelper.addTrack(track, currentUser.getUserName());
            dataBaseHelper.addTraining(newTraining);
            Toast.makeText(getBaseContext(), "Zapisano Trening", Toast.LENGTH_SHORT).show();
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("SERVICE_STARTED", serviceStarted);
        savedInstanceState.putBoolean("END_OF_TRAINING", endOfTraining);
        //savedInstanceState.putBoolean("POSITIONS_OK", positionsOK);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        serviceStarted = savedInstanceState.getBoolean("SERVICE_STARTED");
        endOfTraining = savedInstanceState.getBoolean("END_OF_TRAINING");
        //positionsOK = savedInstanceState.getBoolean("POSITIONS_OK");
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

    private void getTrainingData(Intent intent) {
        locations  = intent.getParcelableArrayListExtra("POSITIONS");
        burnedCalories = intent.getIntExtra("BURNED_CALORIES", 0);
        trackLength = intent.getFloatExtra("LENGTH", 0);
        trainingTime = intent.getLongExtra("TRAINING_TIME", 0);
        if(locations != null && locations.size() > 0) {
            positionsOK = true;
            saveTrainingToDatabase();
        }
    }

    private class MyReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("GPS_INFO");
            //If data is null this means that we get tracked positions from GpsService
            if(data != null) {
                Toast.makeText(TrainingActivity.this, data, Toast.LENGTH_SHORT).show();
                return;
            }
            getTrainingData(intent);
        }
    }
}