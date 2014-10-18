package com.mp.runand.app.logic.database;

import java.sql.Date;

/**
 * Created by Sebastian on 2014-10-14.
 */
public class Track {
    private Date lastUpdate;
    //Every point of Track in formatted String
    private double[] route;
    private double length;
    //Current best time on this Track from every users
    private long bestTime;
    //ID of user who possess the Track record
    private long userID;
    //Counted middle point of Track in formatted String
    private double[] location;


    public Track() {
        //Always need to be rewritten
        this.lastUpdate = new Date(System.currentTimeMillis());
        this.route = null;
        this.length = 0;
        this.bestTime = 0;
        this.userID = 0;
        this.location = null;
    }

    public Track(Date lastUpdate, double[] route, double length, long bestTime, long userID, double[] location) {
        this.lastUpdate = lastUpdate;
        this.route = route;
        this.length = length;
        this.bestTime = bestTime;
        this.userID = userID;
        this.location = location;
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

    public double[] getRoute() {
        return route;
    }

    public void setRoute(double[] route) {
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

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    /*
    Just to debug
     */
    public String toString() {
        String result = "Data: " + lastUpdate.toString();
        return result;
    }
}
