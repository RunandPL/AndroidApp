package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.training.TrainingConstants;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TrainingTypeActivity extends Activity {

    @InjectView(R.id.normalTrainingButton)
    Button normalTrainingButton;

    @OnClick(R.id.normalTrainingButton)
    void normalTrainingButtonOnClick(Button button) {
        Intent intent = new Intent(getBaseContext(), TrainingActivity.class);
        intent.putExtra(TrainingConstants.IS_ROUTE_TRAINING, false);
        intent.putExtra(TrainingConstants.IS_USER_LOGGED_IN, true);
        startActivity(intent);
    }

    @InjectView(R.id.routeTrainingButton)
    Button routeTrainingButton;

    @OnClick(R.id.routeTrainingButton)
    void routeTrainingButtonOnClick(Button button) {
        Intent intent = new Intent(getBaseContext(), TrackChooseActivity.class);
        startActivity(intent);
    }

    @InjectView(R.id.notLoginTrainingButton)
    Button notLoginTrainingButton;

    @OnClick(R.id.notLoginTrainingButton)
    void notLoginOnClick(Button button) {
        Intent intent = new Intent(getBaseContext(), TrainingActivity.class);
        intent.putExtra(TrainingConstants.IS_USER_LOGGED_IN, false);
        intent.putExtra(TrainingConstants.IS_ROUTE_TRAINING, false);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_type);
        ButterKnife.inject(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.training_type, menu);
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
}
