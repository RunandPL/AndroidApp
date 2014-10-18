package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.CurrentUser;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.database.Track;
import com.mp.runand.app.logic.mapsServices.GpsService;

import java.sql.Date;

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

    private double[] trackedPositionsAsDouble = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        ButterKnife.inject(this);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!serviceStarted) {
                    startService(new Intent(getBaseContext(), GpsService.class));
                    serviceStarted = true;
                    endOfTraining = false;
                    positionsOK  = false;
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serviceStarted) {
                    stopService(new Intent(getBaseContext(), GpsService.class));
                    serviceStarted = false;
                    endOfTraining = true;
                }
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), listTracks.class);
                startActivity(intent);
            }
        });
    }

    private void saveTrainingToDatabase() {
        if(positionsOK) {
            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getBaseContext());
            CurrentUser currentUser = dataBaseHelper.getCurrentUser();
            double[] location = new double[]{12.0, 45.0};
            Track track = new Track(new Date(System.currentTimeMillis()), trackedPositionsAsDouble, 12.0, 1, 1, location);
            dataBaseHelper.addTrack(track, currentUser.getUserName());
            Toast.makeText(getBaseContext(), "Zapisano Trasę", Toast.LENGTH_SHORT).show();
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

    private void getTrackedPositions(Intent intent) {
        trackedPositionsAsDouble = intent.getDoubleArrayExtra("POSITIONS");
        if(trackedPositionsAsDouble != null && trackedPositionsAsDouble.length > 0) {
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
            getTrackedPositions(intent);
        }
    }
}
