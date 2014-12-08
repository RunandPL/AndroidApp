package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.entities.Track;
import com.mp.runand.app.logic.entities.Training;
import com.mp.runand.app.logic.network.CurrentLocationSender;
import com.mp.runand.app.logic.network.JSONRequestBuilder;
import com.mp.runand.app.logic.network.LiveTrainingManager;
import com.mp.runand.app.logic.network.TrackSender;
import com.mp.runand.app.logic.network.TrainingSender;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @InjectView(R.id.treningButton)
    Button trainingButton;
    @InjectView(R.id.showHistoryButton)
    Button historyButton;
//    @InjectView(R.id.tmp)
//    Button tmp;
//
//    @OnClick(R.id.tmp)
//    void tmp(){
//        Location area = new Location("none");
//        area.setLatitude(12.0);
//        area.setLongitude(12.0);
//        area.setAltitude(0.0);
//        Location area2 = new Location("none");
//        area2.setLatitude(127.0);
//        area2.setLongitude(172.0);
//        area2.setAltitude(0.0);
//
//        ArrayList<Location> ll = new ArrayList<Location>();
//        ll.add(area);
//        ll.add(area2);
//        Track t = new Track(new Date(System.currentTimeMillis()),ll,11,11,11,area);
//        Training tt = new Training("mail",345,t,23,23);
//
//        //new LiveTrainingManager(this,currentUser).execute(JSONRequestBuilder.buildStartLiveTrainingRequestAsJson(10,10,10));
//        //new CurrentLocationSender(this,currentUser).execute(JSONRequestBuilder.buildSendCurrentLocationRequestAsJson(area));
//        //new CurrentLocationSender(this,currentUser).execute(JSONRequestBuilder.buildSendCurrentLocationRequestAsJson(area2));
//        //new LiveTrainingManager(this,currentUser).execute(JSONRequestBuilder.buildStopLiveTrainingRequestAsJson(tt));
//    }

    @OnClick(R.id.showHistoryButton)
    void onClickHistory(Button button) {
        Intent intent = new Intent(getBaseContext(), TrainingList.class);
        startActivity(intent);
    }

    CurrentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //boolean cos = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_layout);
        ButterKnife.inject(this);
        //do not touch
        currentUser = DataBaseHelper.getInstance(this).getCurrentUser();
    }

    @OnClick(R.id.treningButton)
    public void beginTrening() {
        Intent intent = new Intent(this, TrainingTypeActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        setLoggedUserInfo();
        return true;
    }

    @Override
    public void onBackPressed() {
        //do nothing in this activity
        //otherwise it will close app
        //don't add in another activities
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentUser.getUserName().equals("")) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
        } else {
            menu.getItem(3).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                return true;
            case R.id.menu_login:
                logout();
                return true;
            case R.id.menu_profile_information:
                startActivity(new Intent(this, ProfileInformation.class));
                return true;
            case R.id.menu_add_trainer:
                startActivity(new Intent(this, AddTrainer.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * setting title as logged user
     */
    private void setLoggedUserInfo() {
        if (currentUser.getUserName().equals("")) {
            //todo don't know why this isn't working
            setTitleColor(getResources().getColor(R.color.notlogged));
            setTitle(getText(R.string.not_logged));
        } else {
            setTitle(getText(R.string.user) + currentUser.getUserName());
            //TextView textView = (TextView) findViewById(R.id.titleBar);
            //textView.setText(getText(R.string.user) + currentUser.getUserName());
        }
    }

    /**
     * logout user
     */
    private void logout() {
        DataBaseHelper.getInstance(this).deleteCurrentUser();
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
