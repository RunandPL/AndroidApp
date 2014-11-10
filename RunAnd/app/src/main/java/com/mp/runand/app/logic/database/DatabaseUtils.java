package com.mp.runand.app.logic.database;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Sebastian on 2014-10-16.
 */
public class DatabaseUtils {

    public static String routeToString(ArrayList<Location> route) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < route.size(); i++) {
            builder.append(route.get(i).getLatitude());
            builder.append(" ");
            builder.append(route.get(i).getLongitude());
            builder.append(" ");
        }
        return builder.toString();
    }

    public static ArrayList<Location> stringToRoute(String routeAsString) {
        ArrayList<Location> positionList = new ArrayList<Location>();
        Scanner in = new Scanner(routeAsString);
        double latitude, longitude;
            while(in.hasNext()) {
                String value = in.next();
                latitude = Double.parseDouble(value);
                value = in.next();
                longitude = Double.parseDouble(value);
                Location location = new Location("none");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                positionList.add(location);
        }
        return positionList;
    }

    public static String areaToString(Location area) {
        StringBuilder builder = new StringBuilder();
        builder.append(area.getLatitude());
        builder.append(" ");
        builder.append(area.getLongitude());
        return builder.toString();
    }

    public static Location stringToArea(String areaAsString) {
        Scanner in = new Scanner(areaAsString);
        double latitude = 0, longitude = 0;
        latitude = Double.parseDouble(in.next());
        longitude = Double.parseDouble(in.next());
        Location location = new Location("none");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}
