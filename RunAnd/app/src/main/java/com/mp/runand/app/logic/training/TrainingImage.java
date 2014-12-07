package com.mp.runand.app.logic.training;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sebastian on 2014-11-29.
 */
public class TrainingImage implements Parcelable {
    private String image;
    private Location location;
    private Bitmap imageInBMP;
    private String base64;

    public TrainingImage() {

    }

    public TrainingImage(Location location, String image, Bitmap imageInBMP, String base64) {
        this.location = location;
        this.image = image;
        this.imageInBMP = imageInBMP;
        this.base64 = base64;
    }

    public TrainingImage(Parcel parcel) {
        this.image = parcel.readString();
        this.location = parcel.readParcelable(Location.class.getClassLoader());
        this.imageInBMP = parcel.readParcelable(Bitmap.class.getClassLoader());
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public Bitmap getImageInBMP() {
        return imageInBMP;
    }

    public void setImageInBMP(Bitmap imageInBMP) {
        this.imageInBMP = imageInBMP;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(image);
        parcel.writeParcelable(location, 0);
        parcel.writeParcelable(imageInBMP, 0);
    }

    public static final Creator<TrainingImage> CREATOR = new Creator<TrainingImage>() {

        @Override
        public TrainingImage createFromParcel(Parcel parcel) {
            return new TrainingImage(parcel);
        }

        @Override
        public TrainingImage[] newArray(int i) {
            return new TrainingImage[i];
        }
    };
}
