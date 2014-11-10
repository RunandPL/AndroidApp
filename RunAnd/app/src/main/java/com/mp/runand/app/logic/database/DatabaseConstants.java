package com.mp.runand.app.logic.database;

/**
 * Created by Sebastian on 2014-10-13.
 * Class containing names used in users table
 */
public final class DatabaseConstants {

    //DB GENERAL
    public static String DATA_BASE_NAME = "RunAnd_Database";
    public static int DATA_BASE_VERSION = 3;

    //Common column names
    public static final String ID = "id";

    //CURRENT USER TABLE
    public static final String CURRENT_USER_TABLE_NAME = "current_user";
    public static final String CURRENT_USER_NAME = "user_name";
    public static final String CURRENT_USER_EMAIL_ADDRESS = "email";
    public static final String CURRENT_USER_TOKEN = "sessionID";

    //Track table
    public static final String TRACK_TABLE_NAME = "tracks";
    public static final String TRACK_LAST_UPDATE = "lastUpadate";
    public static final String TRACK_ROUTE = "route";
    public static final String TRACK_LENGTH = "length";
    public static final String TRACK_BEST_TIME = "bestTime";
    public static final String TRACK_USER_ID = "userID";
    public static final String TRACK_AREA = "area";

    //User-Track table
    public static final String USER_TRAINING_TABLE_NAME = "userTraining";
    public static final String USER_TRAINING_TABLE_USER_EMAIL = "userEmail";
    public static final String USER_TRAINING_TABLE_TRAINING_ID = "trainingID";

    //Training table
    public static final String TRAIN_TABLE_NAME = "training";
    public static final String TRAIN_TABLE_USER_EMAIL = "userEmail";
    public static final String TRAIN_TABLE_LENGTH_TIME = "lengthTime";
    public static final String TRAIN_TABLE_TRACK_ID = "trackID";
    public static final String TRAIN_TABLE_DATE = "date";
    public static final String TRAIN_TABLE_BURNED_CALORIES = "burnedCalories";
    public static final String TRAIN_TABLE_SPEED_RATE = "speedRate";
}
