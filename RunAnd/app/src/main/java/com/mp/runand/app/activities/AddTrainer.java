package com.mp.runand.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.entities.Trainer;
import com.mp.runand.app.logic.network.JSONRequestBuilder;
import com.mp.runand.app.logic.network.TrainerListUpdater;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddTrainer extends Activity {

    List<Trainer> trainerList = new ArrayList<Trainer>();
    ArrayAdapter<Trainer> arrayAdapter;
    Trainer pickedOne = null;
    @InjectView(R.id.trainersListView)
    ListView trainersListView;
    CurrentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trainer);
        ButterKnife.inject(this);
        currentUser = DataBaseHelper.getInstance(this).getCurrentUser();
        arrayAdapter = new ArrayAdapter<Trainer>(this,android.R.layout.simple_list_item_1,trainerList);
        new TrainerListUpdater(this, trainersListView, arrayAdapter, currentUser, trainerList).execute(
                JSONRequestBuilder.buildGetAvailableTrainersRequestAsJson());
//        trainerList.add(new Trainer("jhasjdhajsd",12));
//        trainerList.add(new Trainer("asdasdasd",13));
//        arrayAdapter = new ArrayAdapter<Trainer>(this,android.R.layout.simple_list_item_1,trainerList);
//        trainersListView.setAdapter(arrayAdapter);
//        registerForContextMenu(trainersListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info){
        super.onCreateContextMenu(menu,view,info);

        AdapterView.AdapterContextMenuInfo aInfo = (AdapterView.AdapterContextMenuInfo) info;

        Trainer t = arrayAdapter.getItem(aInfo.position);
        pickedOne = t;

        menu.setHeaderTitle("Options for: "+t.getEmail());
        menu.add(1,1,1,"Accept");
        menu.add(1,2,2,"Reject");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_trainer, menu);
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
    public boolean onContextItemSelected(MenuItem item){
        int itemId = item.getItemId();
        Toast.makeText(this,"Item id " + itemId + "choosed" + pickedOne.toString(),Toast.LENGTH_SHORT).show();
        //todo accept and reject
        return true;
    }
}
