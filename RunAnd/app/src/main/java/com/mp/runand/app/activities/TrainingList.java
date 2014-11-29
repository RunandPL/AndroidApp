package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.TrainingListAdapter;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.Training;
import com.mp.runand.app.logic.training.TrainingConstants;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TrainingList extends Activity implements AdapterView.OnItemClickListener {
    @InjectView(R.id.listView) ListView listView;
    private DataBaseHelper dataBaseHelper;
    private ArrayList<Training> trainings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tracks);
        ButterKnife.inject(this);
        dataBaseHelper = DataBaseHelper.getInstance(null);
        CurrentUser currentUser = dataBaseHelper.getCurrentUser();
        trainings = (ArrayList) dataBaseHelper.getUserTrainings(currentUser.getEmailAddress());
        TrainingListAdapter trainingListAdapter = new TrainingListAdapter(getBaseContext(), trainings);
        listView.setAdapter(trainingListAdapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_tracks, menu);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, TrainingSummation.class);
        intent.putExtra(TrainingConstants.TRAINING, trainings.get((int) l));
        startActivity(intent);
    }
}
