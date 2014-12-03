package com.mp.runand.app.logic.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Sebastian on 2014-10-16.
 */
public class DatabaseUtils {

    /**
     * Convert List f Location object to String
     * @param route List to convert
     * @return Lst as String
     */
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

    public static String ImageToBase64(String imagePath) {
        FileInputStream fileInputStream = null;
        try {
            File imageFile = new File(imagePath);
            fileInputStream = new FileInputStream(imageFile);
            byte imageData[] = new byte[(int) imageFile.length()];
            fileInputStream.read(imageData);
            return Base64.encodeToString(imageData, 0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static Bitmap StringToImage(String imageDataString) {
        byte[] imageData = Base64.decode(imageDataString, 0);
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }
}
