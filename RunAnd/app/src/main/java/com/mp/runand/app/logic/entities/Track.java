package com.mp.runand.app.logic.entities;

import android.location.Location;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by Sebastian on 2014-10-14.
 */
public class Track {
    private int id;
    private Date lastUpdate;
    //Every location of this track
    private ArrayList<Location> route;
    private double length;
    //Current best time on this Track from every users
    private long bestTime;
    //ID of user who possess the Track record
    private long userID;
    //Counted middle point of Track in formatted String
    private Location area;
    //Only to track choose activity
    private boolean choosen;


    public Track() {
        //Always need to be rewritten
        this.lastUpdate = new Date(System.currentTimeMillis());
        this.route = null;
        this.length = 0;
        this.bestTime = 0;
        this.userID = 0;
        this.area = null;
        this.choosen = false;
        this.id = 0;
    }

    public Track(int id, Date lastUpdate, ArrayList<Location> route, double length, long bestTime, long userID, Location area) {
        this.lastUpdate = lastUpdate;
        this.route = route;
        this.length = length;
        this.bestTime = bestTime;
        this.userID = userID;
        this.area = area;
        this.choosen = false;
        this.id = id;
    }

    public Track(Date lastUpdate, ArrayList<Location> route, double length, long bestTime, long userID, Location area) {
        this(-1, lastUpdate, route, length, bestTime, userID, area);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = Date.valueOf(lastUpdate);
    }

    public ArrayList<Location> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Location> route) {
        this.route = route;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public long getBestTime() {
        return bestTime;
    }

    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public Location getArea() {
        return area;
    }

    public void setLocation(Location area) {
        this.area = area;
    }

    public boolean isChoosen() {
        return choosen;
    }

    public void setChoosen(boolean choosen) {
        this.choosen = choosen;
    }

    public String getSendableRoute() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0; i<route.size(); i++){
            sb.append("{")
                .append("\"x\":").append(route.get(i).getLatitude()).append(", ")
                .append("\"y\":").append(route.get(i).getLongitude()).append(", ")
                .append("\"z\":").append(route.get(i).getAltitude())
                .append("}");
            if(i!=route.size()-1){
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /*
        Just to debug
         */
    public String toString() {
        String result = "Data: " + lastUpdate.toString();
        return result;
    }
}
