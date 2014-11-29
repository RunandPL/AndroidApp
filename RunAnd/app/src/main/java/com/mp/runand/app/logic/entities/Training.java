package com.mp.runand.app.logic.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

/**
 * Created by Sebastian on 2014-10-25.
 */
public class Training implements Parcelable{
    private int id;
    private String userEmail;
    //How long the training was, in miliseconds
    private long lengthTime;
    private Track track;
    private int burnedCalories;
    private double speedRate;
    //When training took place
    private Date date;
    private double pace;

    public Training(String userEmail, long lengthTime, Track track, int burnedCalories, double speedRate) {
        this.id = -1;
        this.userEmail = userEmail;
        this.lengthTime = lengthTime;
        this.track = track;
        this.burnedCalories = burnedCalories;
        this.speedRate = speedRate;
        this.date = new Date(System.currentTimeMillis());
        calculatePace();
    }

    //Better constructor to read from database
    public Training(int id, String userEmail, int lengthTime, int burnedCalories, double speedRate, String date, double pace) {
        this.id = id;
        this.userEmail = userEmail;
        this.lengthTime = lengthTime;
        this.burnedCalories = burnedCalories;
        this.speedRate = speedRate;
        this.date = Date.valueOf(date);
        this.pace = pace;
    }

    //Constructor to read from Parcel, only to use in TrainingSummation activity
    //track is not used there so it can be null
    private Training(Parcel parcel) {
        this.id = parcel.readInt();
        this.userEmail = parcel.readString();
        this.lengthTime = parcel.readLong();
        this.burnedCalories = parcel.readInt();
        this.speedRate = parcel.readDouble();
        this.date = Date.valueOf(parcel.readString());
        this.pace = parcel.readDouble();
        this.track = parcel.readParcelable(getClass().getClassLoader());
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

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }

    public String toString() {
        return "Bieg - " + date.toString();
    }

    public String getDescription() {
        return "Czas - " + getFormatedTime() + ", Długość - " + getLengthInKm();
    }

    private void calculatePace() {
        int timeInSeconds = (int) lengthTime / 1000;
        pace = timeInSeconds / track.getLength();
    }

    public String getFormatedPace() {
        //Pace in seconds
        int pace = (int) (this.pace * 1000);
        int seconds = pace % 60;
        int minutes = (pace - seconds) / 60;
        return minutes + "m " + seconds + "s/km";
    }

    public String getLengthInKm() {
        double lengthInKm = track.getLength() / 1000;
        return String.format("%.3f", lengthInKm) + " km";
    }

    public String getFormatedTime() {
        int lengthTimeInSeconds = (int) lengthTime / 1000;
        int seconds = lengthTimeInSeconds % 60;
        int minutes = (lengthTimeInSeconds - seconds) / 60;
        return minutes + "m " + seconds + "s";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(userEmail);
        parcel.writeLong(lengthTime);
        parcel.writeInt(burnedCalories);
        parcel.writeDouble(speedRate);
        parcel.writeString(date.toString());
        parcel.writeDouble(pace);
        parcel.writeParcelable(track, 0);
    }

    public static final Creator<Training> CREATOR = new Creator<Training>() {

        @Override
        public Training createFromParcel(Parcel parcel) {
            return new Training(parcel);
        }

        @Override
        public Training[] newArray(int i) {
            return new Training[i];
        }
    };
}
