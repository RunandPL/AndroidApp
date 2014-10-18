package com.mp.runand.app.logic.database;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Sebastian on 2014-10-16.
 */
public class DatabaseUtils {

    public static String trackToString(double[] track) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < track.length; i+=2) {
            builder.append(track[i]);
            builder.append(" ");
            builder.append(track[i+1]);
            builder.append(" ");
        }
        return builder.toString();
    }

    public static double[] stringToTrack(String trackAsString) {
        List<Double> positionList = new LinkedList<Double>();
        Scanner in = new Scanner(trackAsString);
            while(in.hasNext()) {
                String value = in.next();
               positionList.add(Double.parseDouble(value));
        }
        Double[] positions =  positionList.toArray(new Double[positionList.size()]);

        double[] result = new double[positions.length];
        for(int i = 0; i < positions.length; i++) {
            result[i] = positions[i];
        }
        return result;
    }
}
