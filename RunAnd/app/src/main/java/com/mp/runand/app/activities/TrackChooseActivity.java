package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.adapters.TrackListAdapter;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.Track;
import com.mp.runand.app.logic.training.TrainingConstants;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TrackChooseActivity extends Activity implements AdapterView.OnItemClickListener {

    @InjectView(R.id.trackListView)
    ListView listView;
    @InjectView(R.id.trackChooseButton)
    Button button;
    @OnClick(R.id.trackChooseButton)
    void chooseTrack(View view) {
        boolean choosen = false;
        int choosenNumber = -1;
        for(int i = 0; i < tracks.size(); i++) {
            //Two tracks cannot be choosen
            if(choosen && tracks.get(i).isChoosen()) {
                Toast.makeText(getBaseContext(), "Nie można wybrać kilku tras", Toast.LENGTH_SHORT).show();
                return;
            }
            if(tracks.get(i).isChoosen()) {
                choosen = true;
                choosenNumber = i;
            }
        }
        if(choosenNumber == -1)
            return;
        Intent intent = new Intent(getBaseContext(), TrainingActivity.class);
        intent.putExtra(TrainingConstants.IS_USER_LOGGED_IN, true);
        intent.putExtra(TrainingConstants.IS_ROUTE_TRAINING, true);
        intent.putParcelableArrayListExtra(TrainingConstants.ROUTE_TO_FOLLOW, tracks.get(choosenNumber).getRoute());
        intent.putExtra("trackID", tracks.get(choosenNumber).getId());
        startActivity(intent);
    }
    private DataBaseHelper dataBaseHelper;
    private ArrayList<Track> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_choose);
        ButterKnife.inject(this);
        dataBaseHelper = DataBaseHelper.getInstance(getBaseContext());
        tracks = dataBaseHelper.getAllTracks();
        TrackListAdapter trackListAdapter = new TrackListAdapter(getBaseContext(), tracks);
        listView.setAdapter(trackListAdapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track_choose, menu);
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
        Intent intent = new Intent(getBaseContext(), MapLook.class);
        intent.putExtra("POSITIONS", tracks.get((int) l).getRoute());
        startActivity(intent);
    }
}
