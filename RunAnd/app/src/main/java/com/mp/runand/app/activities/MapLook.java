package com.mp.runand.app.activities;


import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.internal.ma;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.training.TrainingConstants;
import com.mp.runand.app.logic.training.TrainingImage;
import com.squareup.picasso.Picasso;

/**
 * Created by Sebastian on 2014-10-09.
 */
public class MapLook extends Activity implements GoogleMap.OnMarkerClickListener {
    @Override
    public boolean onMarkerClick(Marker marker) {
        int position = -1;
        for(int i = 0; i < imagesMarkers.size(); i++) {
            if(imagesMarkers.get(i).equals(marker)) {
                position = i;
                break;
            }
        }
        if(position == -1)
            return false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageView imageView = new ImageView(this);
        if(!images.get(position).getImage().equals(""))
            Picasso.with(this).load(images.get(position).getImage()).into(imageView);
        else
            imageView.setImageBitmap(images.get(position).getImageInBMP());
        Toast.makeText(this, images.get(position).getImage(), Toast.LENGTH_SHORT).show();
        builder.setView(imageView);
        builder.create().show();
        return true;
    }

    private GoogleMap map = null;
    private LatLng[] positions = null;
    private Polyline polyline = null;
    private Marker startMarker = null;
    private Marker stopMarker = null;
    private ArrayList<TrainingImage> images = null;
    private ArrayList<Marker> imagesMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_look);
        prepareMap();
        Intent intent = getIntent();
        ArrayList<Location> positionList = intent.getParcelableArrayListExtra("POSITIONS");
        positions = getAsLatLngTable(positionList);
        int trainingID = intent.getIntExtra("TRAINING_ID", -1);
        images = DataBaseHelper.getInstance(this).getImagesForTraining(trainingID);
        imagesMarkers = new ArrayList<Marker>();
        putTrackOnMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_look, menu);
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

    private void putTrackOnMap() {
        if(map != null && !(positions.length == 0)) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.add(positions);
            polylineOptions.color(Color.GREEN);
            polyline = map.addPolyline(polylineOptions);

            //Place markers at begining and end of route
            LatLng start = new LatLng(positions[0].latitude, positions[0].longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("Start");
            markerOptions.position(start);
            startMarker = map.addMarker(markerOptions);
            LatLng stop = new LatLng(positions[positions.length - 1].latitude, positions[positions.length - 1].longitude);
            markerOptions.title("Stop");
            markerOptions.position(stop);
            stopMarker = map.addMarker(markerOptions);

            //Put images markers on map
            for(int i = 0; i < images.size(); i++) {
                markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(images.get(i).getLocation().getLatitude(), images.get(i).getLocation().getLongitude());
                markerOptions.position(latLng);
                imagesMarkers.add(map.addMarker(markerOptions));
            }
            //Zoom the camera to the beggining of route
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(start, 15.0f);
            map.animateCamera(cameraUpdate);
        }
    }

    private void prepareMap() {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnMarkerClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        putTrackIntoBundle(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        getTrackFromBundle(inState);
    }

    private void putTrackIntoBundle(Bundle bundle) {
        //Zapisanie znanych pozycji do Bundle
        if(!(positions.length == 0)) {
            double[] positionsAsDouble = new double[positions.length * 2];
            int position = 0;
            for(int i = 0; i < positions.length; i++) {
                positionsAsDouble[position] = positions[i].latitude;
                positionsAsDouble[position + 1] = positions[i].longitude;
                position += 2;
            }
            bundle.putDoubleArray("Position List", positionsAsDouble);
        }
        //Zapisane markera do Bundle, sama pozycja powinna wystarczyć
        if(startMarker != null) {
            LatLng markerPosition = startMarker.getPosition();
            double[] markerPositionAsDouble = new double[2];
            markerPositionAsDouble[0] = markerPosition.latitude;
            markerPositionAsDouble[1] = markerPosition.longitude;
            String title = startMarker.getTitle();
            bundle.putDoubleArray("startMarker", markerPositionAsDouble);
            bundle.putString("startMarkerTitle", title);
        }

        if(stopMarker != null) {
            LatLng markerPosition = stopMarker.getPosition();
            double[] markerPositionAsDouble = new double[2];
            markerPositionAsDouble[0] = markerPosition.latitude;
            markerPositionAsDouble[1] = markerPosition.longitude;
            String title = stopMarker.getTitle();
            bundle.putDoubleArray("stopMarker", markerPositionAsDouble);
            bundle.putString("stopMarkerTitle", title);
        }
    }

    private void getTrackFromBundle(Bundle bundle) {
        //Wczytanie zapisanej trasy
        double[] positionsAsDobule = bundle.getDoubleArray("Position List");

        //Na wypadek gdyby nie było żadnej zapisanej trasy
        if(positionsAsDobule != null) {
            int position = 0;
            for(int i = 0; i < positionsAsDobule.length; i += 2) {
                LatLng latLng = new LatLng(positionsAsDobule[i], positionsAsDobule[i+1]);
                positions[position++] = latLng;
            }
        }
        putTrackOnMap();
    }

    private LatLng[] getAsLatLngTable(ArrayList<Location> locations) {
        LatLng[] result = new LatLng[locations.size()];
        for(int i = 0; i < locations.size(); i++) {
            LatLng latLng = new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude());
            result[i] = latLng;
        }
        return result;
    }
}

