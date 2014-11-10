package com.mp.runand.app.logic.entities;

import java.sql.Date;

/**
 * Created by Sebastian on 2014-10-25.
 */
public class Training {
    private int id;
    private String userEmail;
    //How long the training was, in miliseconds
    private long lengthTime;
    private Track track;
    private int burnedCalories;
    private double speedRate;
    //When training took place
    private Date date;

    public Training(String userEmail, long lengthTime, Track track, int burnedCalories, double speedRate) {
        this.userEmail = userEmail;
        this.lengthTime = lengthTime;
        this.track = track;
        this.burnedCalories = burnedCalories;
        this.speedRate = speedRate;
        this.date = new Date(System.currentTimeMillis());
    }

    //Better constructor to read from database
    public Training(int id, String userEmail, int lengthTime, int burnedCalories, double speedRate, String date) {
        this.id = id;
        this.userEmail = userEmail;
        this.lengthTime = lengthTime;
        this.burnedCalories = burnedCalories;
        this.speedRate = speedRate;
        this.date = Date.valueOf(date);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getLengthTime() {
        return lengthTime;
    }

    public void setLengthTime(long lengthTime) {
        this.lengthTime = lengthTime;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public int getBurnedCalories() {
        return burnedCalories;
    }

    public void setBurnedCalories(int burnedCalories) {
        this.burnedCalories = burnedCalories;
    }

    public double getSpeedRate() {
        return speedRate;
    }

    public void setSpeedRate(double speedRate) {
        this.speedRate = speedRate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String toString() {
        return "Bieg - " + date.toString();
    }

    public String getDescription() {
        return "Czas - " + getFormatedTime() + ", Długość - " + getLengthInKm();
    }

    private String getLengthInKm() {
        double lengthInKm = track.getLength() / 1000;
        return String.format("%.2f", lengthInKm) + " km";
    }

    private String getFormatedTime() {
        int lengthTimeInSeconds = (int) lengthTime / 1000;
        int seconds = lengthTimeInSeconds % 60;
        int minutes = (lengthTimeInSeconds - seconds) / 60;
        return minutes + "m " + seconds + "s";
    }
}
