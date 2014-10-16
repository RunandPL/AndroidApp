package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.database.CurrentUserDAO;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_logout:
                CurrentUserDAO.getCurrentUser(this).logOut();
                Intent i = new Intent(this, Login.class);
                startActivity(i);
                finish();
//            case R.id.help:
//                showHelp();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
