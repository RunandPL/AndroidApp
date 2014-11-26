package com.mp.runand.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileInformation extends Activity {

    CurrentUser currentUser;

    @InjectView(R.id.profileEmailAddress)
    TextView profileEmailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_information);
        ButterKnife.inject(this);
        currentUser = DataBaseHelper.getInstance(this).getCurrentUser();
        setLoggedUserInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.refreshProfileInformation:
                //todo refresh call
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void setLoggedUserInfo(){
        setTitle(getText(R.string.user) + currentUser.getUserName());
        profileEmailAddress.setText(currentUser.getEmailAddress());
    }
}
