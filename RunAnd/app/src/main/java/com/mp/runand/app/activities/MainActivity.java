package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @InjectView(R.id.treningButton)
    Button trainingButton;
    @InjectView(R.id.showHistoryButton)
    Button historyButton;

    @OnClick(R.id.showHistoryButton)
    void onClickHistory(Button button) {
        Intent intent = new Intent(getBaseContext(), TrainingList.class);
        startActivity(intent);
    }

    CurrentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        //do not touch
        currentUser = DataBaseHelper.getInstance(this).getCurrentUser();
        setLoggedUserInfo();
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
            setTitle(getText(R.string.not_logged));
        } else {
            setTitle(getText(R.string.user) + currentUser.getUserName());
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
