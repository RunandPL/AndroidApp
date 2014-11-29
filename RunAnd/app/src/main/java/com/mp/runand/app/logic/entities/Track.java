package com.mp.runand.app.logic.entities;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by Sebastian on 2014-10-14.
 */
public class Track implements Parcelable {
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
        this.id = -1;
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

    /**
     *
     * @param parcel to construct track object from
     */
    private Track(Parcel parcel) {
        this.id = parcel.readInt();
        this.lastUpdate = Date.valueOf(parcel.readString());
        this.route = new ArrayList<Location>();
        parcel.readList(this.route, null);
        this.length = parcel.readDouble();
        this.bestTime = parcel.readLong();
        this.userID = parcel.readLong();
        this.area = parcel.readParcelable(getClass().getClassLoader());
        boolean[] tmp = new boolean[1];
        parcel.readBooleanArray(tmp);
        this.choosen = tmp[0];
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
        for (int i = 0; i < route.size(); i++) {
            sb.append("{")
                    .append("\"x\":").append(route.get(i).getLatitude()).append(", ")
                    .append("\"y\":").append(route.get(i).getLongitude()).append(", ")
                    .append("\"z\":").append(route.get(i).getAltitude())
                    .append("}");
            if (i != route.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    public Location getStartLocation() {
        if(route != null && !route.isEmpty()) {
            return route.get(0);
        }
        return null;
    }

    public Location getEndLocation() {
        if(route != null && !route.isEmpty()) {
            return route.get(route.size() - 1);
        }
        return null;
    }

    /*
        Just to debug
         */
    public String toString() {
        String result = "Data: " + lastUpdate.toString();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(lastUpdate.toString());
        parcel.writeList(route);
        parcel.writeDouble(length);
        parcel.writeLong(bestTime);
        parcel.writeLong(userID);
        parcel.writeParcelable(area, 0);
        parcel.writeBooleanArray(new boolean[]{choosen});
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {

        @Override
        public Track createFromParcel(Parcel parcel) {
            return new Track(parcel);
        }

        @Override
        public Track[] newArray(int i) {
            return new Track[i];
        }
    };
}
