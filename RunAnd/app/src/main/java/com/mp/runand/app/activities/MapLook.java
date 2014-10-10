package com.mp.runand.app.activities;


import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.internal.ma;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mp.runand.app.R;

/**
 * Created by Sebastian on 2014-10-09.
 */
public class MapLook extends Activity {

    private GoogleMap map = null;
    private List<LatLng> positionList = null;
    private Polyline polyline = null;
    private Marker startMarker = null;
    private Marker stopMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_look);
        prepareMap();
        Intent intent = getIntent();
        readPositions(intent.getDoubleArrayExtra("POSITIONS"));
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

    private void readPositions(double[] positionsAsDouble) {
        positionList = new ArrayList<LatLng>();
        if(positionsAsDouble == null || positionsAsDouble.length == 0) {
            Toast.makeText(this, "Pozycje są puste", Toast.LENGTH_SHORT).show();
            return;
        }
        int length = positionsAsDouble.length;
        for(int i = 0; i < (length - 1); i+=2) {
            double lat = positionsAsDouble[i];
            double ltg = positionsAsDouble[i + 1];
            LatLng latLng = new LatLng(lat, ltg);
            positionList.add(latLng);

        }

    }

    private void putTrackOnMap() {
        if(map != null && !positionList.isEmpty()) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.add(positionList.toArray(new LatLng[positionList.size()]));
            polylineOptions.color(Color.GREEN);
            polyline = map.addPolyline(polylineOptions);

            LatLng latLng = positionList.get(0);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("Start");
            markerOptions.position(latLng);
            startMarker = map.addMarker(markerOptions);
            latLng = positionList.get(positionList.size() - 1);
            markerOptions.title("Stop");
            markerOptions.position(latLng);
            stopMarker = map.addMarker(markerOptions);
        }
    }

    private void prepareMap() {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    	/*//Ustawiam ostatnią znaną pozycję na mapie
    	Location location = locationManager.getLastKnownLocation(LOCATION_PROVIDER);
    	if(location != null) {
    		updateMap(location, map);
    		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    		positionList.add(latLng);
    		//Ruch za markerem
    		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
    		putMarkerOnMap(map, "Ja", latLng);
    		currentBestLocation = location;
    	}*/
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
        if(!positionList.isEmpty()) {
            double[] positionsAsDouble = new double[positionList.size() * 2];
            int position = 0;
            for(int i = 0; i < positionList.size(); i++) {
                positionsAsDouble[position] = positionList.get(i).latitude;
                positionsAsDouble[position + 1] = positionList.get(i).longitude;
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
            for(int i = 0; i < positionsAsDobule.length; i += 2) {
                LatLng latLng = new LatLng(positionsAsDobule[i], positionsAsDobule[i+1]);
                positionList.add(latLng);
            }
        }
        putTrackOnMap();
	    	/*//Wczytanie markera
	    	double[] markerPositionAsDobule = bundle.getDoubleArray("Marker");

	    	//Na wypadek gdyby nie było zapisanego markera
	    	if(markerPositionAsDobule != null) {
	    		LatLng markerPosition = new LatLng(markerPositionAsDobule[0], markerPositionAsDobule[1]);
	    		putMarkerOnMap(map, "Tytuł", markerPosition);
	    	}*/
    }
}

