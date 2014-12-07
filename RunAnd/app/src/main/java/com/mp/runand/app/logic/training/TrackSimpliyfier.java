package com.mp.runand.app.logic.training;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Sebastian on 2014-12-06.
 */
public class TrackSimpliyfier {

    public static List<Location> simplify(List<Location> route) {
        if(route.size() <= 1)
            return route;

        double sqTolerance = 0.5;

        int length = route.size();
        int[] markers = new int[length];
        int first = 0;
        int last = length - 1;
        Stack<Integer> stack = new Stack<Integer>();
        ArrayList<Location> result = new ArrayList<Location>();
        double maxSqDist, sqDist;
        int index = -1;

            maxSqDist = 0;
            for(int i = first + 1; i < last; i++) {
                sqDist = getSqSegDist(route.get(i), route.get(first), route.get(last));

                if(sqDist > maxSqDist) {
                    index = i;
                    maxSqDist = sqDist;
                }
            }

            if(maxSqDist > sqTolerance) {

               List<Location> recResult = simplify(route.subList(0, index));
               List<Location> recResult2 = simplify(route.subList(index, route.size()));
                result.addAll(recResult);
                result.addAll(recResult2);
            } else {
                result.add(route.get(0));
                result.add(route.get(route.size() - 1));
            }
        return result;
    }

    private static double getSqSegDist(Location l1, Location l2, Location l3) {
        double x = l2.getLatitude();
        double y = l2.getLongitude();
        double dx = l3.getLatitude() - x;
        double dy = l3.getLongitude() - y;

        if(dx != 0 || dy != 0) {
            double t = ((l1.getLatitude() - x) * dx + (l1.getLongitude() - y) * dy) / (dx * dx + dy* dy);
            if(t > 1) {
                x = l3.getLatitude();
                y = l3.getLongitude();
            } else if(t > 0) {
                x += dx * t;
                y += dy * t;
            }
        }
        dx = l1.getLatitude() - x;
        dy = l1.getLongitude() - y;

        return dx * dx + dy * dy;
    }
}
