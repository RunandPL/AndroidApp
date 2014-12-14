package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.Training;
import com.mp.runand.app.logic.training.TrackSimpliyfier;
import com.mp.runand.app.logic.training.TrainingConstants;
import com.mp.runand.app.logic.training.TrainingImage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TrainingSummation extends Activity {
    private final String Separator = "%7C";

    @InjectView(R.id.paceTextView)
    TextView paceValue;

    @InjectView(R.id.timeTextView)
    TextView timeValue;

    @InjectView(R.id.caloriesTextView)
    TextView caloriesValue;

    @InjectView(R.id.lengthTextView)
    TextView lengthValue;

    @InjectView(R.id.trackImage)
    ImageView trackImage;

    @OnClick(R.id.trackImage)
    void trackImageOnClick(ImageView imageView) {
        Intent intent = new Intent(this, MapLook.class);
        intent.putExtra("TRAINING_ID", training.getId());
        intent.putExtra("POSITIONS", training.getTrack().getRoute());
        startActivity(intent);
    }

    private Training training;
    private DataBaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_summation);
        ButterKnife.inject(this);

        training = getIntent().getParcelableExtra(TrainingConstants.TRAINING);
        createImageUrl();
        List<Location> tmp = training.getTrack().getRoute();
        for(int i =0; i < tmp.size(); i++) {
            tmp.get(i).setLatitude(tmp.get(i).getLatitude() * 10000);
            tmp.get(i).setLongitude(tmp.get(i).getLongitude() * 10000);
        }
        List<Location> result = TrackSimpliyfier.simplify(training.getTrack().getRoute());
        training.getTrack().setRoute(new ArrayList<Location>(result));
        tmp = training.getTrack().getRoute();
        for(int i =0; i < tmp.size(); i++) {
            tmp.get(i).setLatitude(tmp.get(i).getLatitude() / 10000);
            tmp.get(i).setLongitude(tmp.get(i).getLongitude() / 10000);
        }
        databaseHelper = DataBaseHelper.getInstance(this);

        setValuesOnViews();
        Picasso.with(this).load(createImageUrl()).resize(trackImage.getMaxWidth(), trackImage.getMaxHeight()).into(trackImage);
    }

    private void setValuesOnViews() {
        paceValue.setText(training.getFormatedPace());
        timeValue.setText(training.getFormatedTime());
        caloriesValue.setText(String.valueOf(training.getBurnedCalories()));
        lengthValue.setText(training.getLengthInKm());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.training_summation, menu);
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

    private String createImageUrl() {
        StringBuilder builder = new StringBuilder();
        builder.append("https://maps.googleapis.com/maps/api/staticmap?");
        //Setting markers
        builder.append(addMarker("Start", training.getTrack().getStartLocation()));
        builder.append("&");
        builder.append(addMarker("Koniec", training.getTrack().getEndLocation()));
        builder.append("&");
        //Setting path
        builder.append(addPath());
        //Setting size
        builder.append("&size=800x800");
        Toast.makeText(this, "Dlugosc: "+ builder.length(), Toast.LENGTH_LONG).show();
        return builder.toString();
    }

    private String addMarker(String value, Location location) {
        StringBuilder builder = new StringBuilder();
        //First Marker
        builder.append("markers=");
        //Setting color
        builder.append("color:blue");
        builder.append(Separator);
        builder.append("label:");
        builder.append(value);
        builder.append(Separator);
        builder.append(location.getLatitude() + "," + location.getLongitude());
        return builder.toString();
    }

    private String addPath() {
        StringBuilder builder = new StringBuilder();
        builder.append("path=");
        //Setting color
        builder.append("color:green");
        //Setting path locations
        ArrayList<Location> route = training.getTrack().getRoute();
        for(int i = 0; i < route.size(); i++) {
            builder.append(Separator);
            builder.append(route.get(i).getLatitude());
            builder.append(",");
            builder.append(route.get(i).getLongitude());
        }
        return builder.toString();
    }
}
