package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mp.runand.app.R;

public class MapDebug extends Activity {
    private GoogleMap map;
    private Marker marker;
    private Circle circle;
    private final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_debug);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapDebug)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(Location location) {
                if(marker != null)
                    marker.remove();
                if(circle != null)
                    circle.remove();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                marker = map.addMarker(markerOptions);
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(new LatLng(location.getLatitude(), location.getLongitude()));
                circleOptions.fillColor(0x5500ff00);
                circle = map.addCircle(circleOptions);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f);
                map.animateCamera(cameraUpdate);
            }
        };
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, locationListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_debug, menu);
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
