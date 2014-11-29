package com.mp.runand.app.logic;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sebastian on 2014-11-29.
 */
public class NameBuilder {
    public static File createImageFile(File directory, String userName) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = userName + timeStamp + "_";
        try {
            File image = File.createTempFile(imageFileName, ".jpeg", directory);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
