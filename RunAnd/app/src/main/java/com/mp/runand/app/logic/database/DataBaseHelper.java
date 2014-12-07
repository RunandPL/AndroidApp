package com.mp.runand.app.logic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.entities.Track;
import com.mp.runand.app.logic.entities.Training;
import com.mp.runand.app.logic.training.TrainingImage;

/**
 * Created by Sebastian on 2014-10-13.
 * SQL queries
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static DataBaseHelper instance = null;
    private static String DATA_BASE_NAME = "RunAndDataBase";
    private static int DATA_BASE_VERSION = 6;
    private static Context context;


    private static final String CREATE_CURRENT_USER_TABLE = "CREATE TABLE " + DatabaseConstants.CURRENT_USER_TABLE_NAME
            + "(" + DatabaseConstants.CURRENT_USER_NAME + " TEXT NOT NULL," + DatabaseConstants.CURRENT_USER_EMAIL_ADDRESS + " TEXT PRIMARY KEY,"
            + DatabaseConstants.CURRENT_USER_TOKEN + " TEXT" + ")";


    private static final String CREATE_TRACKS_TABLE = "CREATE TABLE " + DatabaseConstants.TRACK_TABLE_NAME
            + "(" + DatabaseConstants.ID + " INTEGER PRIMARY KEY," + DatabaseConstants.TRACK_LAST_UPDATE + " DATE NOT NULL,"
            + DatabaseConstants.TRACK_ROUTE + " LONGTEXT NOT NULL," + DatabaseConstants.TRACK_LENGTH + " UNSIGNED DOUBLE NOT NULL," +
            DatabaseConstants.TRACK_BEST_TIME + " UNSIGNED LONG NOT NULL," + DatabaseConstants.TRACK_USER_ID + " LONG NOT NULL,"
            + DatabaseConstants.TRACK_AREA + " CHAR(14) NOT NULL" + ")";

    //Create table(id integer not null AUTO_INCREMENT, userName Text not null, trackid integer not null)

    private static final String CREATE_USER_TRAINING_TABLE = "CREATE TABLE " + DatabaseConstants.USER_TRAINING_TABLE_NAME
            + "(" + DatabaseConstants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DatabaseConstants.USER_TRAINING_TABLE_USER_EMAIL + " TEXT NOT NULL, "
            + DatabaseConstants.USER_TRAINING_TABLE_TRAINING_ID + " INTEGER NOT NULL)";

    private static final String CREATE_TRAINING_TABLE = "CREATE TABLE " + DatabaseConstants.TRAIN_TABLE_NAME
            + "(" + DatabaseConstants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DatabaseConstants.TRAIN_TABLE_USER_EMAIL + " TEXT NOT NULL, "
            + DatabaseConstants.TRAIN_TABLE_LENGTH_TIME + " INTEGER NOT NULL, " + DatabaseConstants.TRAIN_TABLE_TRACK_ID + " INTEGER NOT NULL, "
            + DatabaseConstants.TRAIN_TABLE_BURNED_CALORIES + " INTEGER NOT NULL, " + DatabaseConstants.TRAIN_TABLE_SPEED_RATE + " FLOAT NOT NULL,"
            + DatabaseConstants.TRAIN_TABLE_PACE + " DOUBLE NOT NULL, " + DatabaseConstants.TRAIN_TABLE_DATE + " DATE NOT NULL, "
            + DatabaseConstants.TRAIN_TABLE_IMAGES + " TEXT NOT NULL, " + DatabaseConstants.TRAIN_TABLE_IMAGES_POSITION + " TEXT NOT NULL)";

    public static DataBaseHelper getInstance(Context context) {
        if (instance == null) {
            //Kasowanie bazy
            //context.deleteDatabase(DATA_BASE_NAME);
            instance = new DataBaseHelper(context);
        }
        return instance;
    }

    private DataBaseHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase dataBase) {
        dataBase.execSQL(CREATE_CURRENT_USER_TABLE);
        dataBase.execSQL(CREATE_TRACKS_TABLE);
        dataBase.execSQL(CREATE_USER_TRAINING_TABLE);
        dataBase.execSQL(CREATE_TRAINING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
        //Right now, I think we do not need this so I will not do anything here
        //context.deleteDatabase(DatabaseConstants.DATA_BASE_NAME);
        onCreate(dataBase);
    }

    /**
     * Create new current user in db
     * @param uName username
     * @param uEmail email
     * @param uSession session id
     */
    public void addCurrentUser(String uName, String uEmail, String uSession){
        SQLiteDatabase dataBase = getWritableDatabase();

        //only one current user can be stored in db
        //taBase.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.CURRENT_USER_TABLE_NAME);
        dataBase.execSQL(CREATE_CURRENT_USER_TABLE);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.CURRENT_USER_NAME, uName);
        contentValues.put(DatabaseConstants.CURRENT_USER_EMAIL_ADDRESS, uEmail);
        contentValues.put(DatabaseConstants.CURRENT_USER_TOKEN, uSession);

        dataBase.insert(DatabaseConstants.CURRENT_USER_TABLE_NAME, null, contentValues);
        dataBase.close();
    }

    /**
     * Add current user to db
     * @param cu current user
     */
    public void addCurrentUser(CurrentUser cu) {
        SQLiteDatabase dataBase = getWritableDatabase();

        //only one current user can be stored in db
        dataBase.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.CURRENT_USER_TABLE_NAME);
        dataBase.execSQL(CREATE_CURRENT_USER_TABLE);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.CURRENT_USER_NAME, cu.getUserName());
        contentValues.put(DatabaseConstants.CURRENT_USER_EMAIL_ADDRESS, cu.getEmailAddress());
        contentValues.put(DatabaseConstants.CURRENT_USER_TOKEN, cu.getToken());

        dataBase.insert(DatabaseConstants.CURRENT_USER_TABLE_NAME, null, contentValues);
        dataBase.close();
    }

    /*
    Delete user with given User Name form database
     */
    public void deleteCurrentUser() {
        SQLiteDatabase dataBase = getWritableDatabase();
        dataBase.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.CURRENT_USER_TABLE_NAME);
        dataBase.execSQL(CREATE_CURRENT_USER_TABLE);
        dataBase.close();
    }

    /*
    Get user with given User Name from database
    @return null if there is no user with given user name
     */
    public CurrentUser getCurrentUser() {
        SQLiteDatabase dataBase = getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseConstants.CURRENT_USER_TABLE_NAME;

        //Getting cursor to database
        Cursor cursor = dataBase.rawQuery(query, null);

        cursor.moveToFirst();
        CurrentUser cu = null;
        try {
            cu = new CurrentUser(
                    cursor.getString(cursor.getColumnIndex(DatabaseConstants.CURRENT_USER_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseConstants.CURRENT_USER_TOKEN)),
                    cursor.getString(cursor.getColumnIndex(DatabaseConstants.CURRENT_USER_EMAIL_ADDRESS)));
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            e.getStackTrace();
        } finally {
            dataBase.close();
            return cu;
        }
    }

    /**
     * Save given track to database
     *
     * @param track
     * @return id of new track record
     */
    public int addTrack(Track track) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.TRACK_BEST_TIME, track.getBestTime());
        contentValues.put(DatabaseConstants.TRACK_LAST_UPDATE, track.getLastUpdate().toString());
        contentValues.put(DatabaseConstants.TRACK_LENGTH, track.getLength());
        contentValues.put(DatabaseConstants.TRACK_AREA,
                DatabaseUtils.areaToString(track.getArea()));
        contentValues.put(DatabaseConstants.TRACK_ROUTE,
                DatabaseUtils.routeToString(track.getRoute()));
        contentValues.put(DatabaseConstants.TRACK_USER_ID, 0);

        int trackID = (int) database.insert(DatabaseConstants.TRACK_TABLE_NAME, null, contentValues);
        database.close();
        return trackID;
    }

    /**
     * Delete track with given ID from database
     *
     * @param trackID
     */
    public void deleteTrack(long trackID) {
        SQLiteDatabase database = getWritableDatabase();

        String[] parametersTable = new String[]{String.valueOf(trackID)};
        String query = DatabaseConstants.ID + " = ?";

        database.delete(DatabaseConstants.TRACK_TABLE_NAME, query, parametersTable);
        database.close();
    }

    /**
     * Get track with given trackID from database
     *
     * @param trackID
     * @return Track or null if track with given ID do not exist
     */
    public Track getTrack(long trackID) {
        SQLiteDatabase database = getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseConstants.TRACK_TABLE_NAME + " WHERE " + DatabaseConstants.ID +
                " = '" + trackID + "'";

        //Getting cursor to database
        Cursor cursor = database.rawQuery(query, null);

        if (cursor == null || cursor.getCount() == 0) {
            //There is no track with given trackID
            return null;
        }
        cursor.moveToFirst();
        return getTrackFromCursor(cursor);
    }

    public ArrayList<Track> getAllTracks() {
        String query = "Select * from " + DatabaseConstants.TRACK_TABLE_NAME;
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<Track> result = new ArrayList<Track>();

        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                result.add(getTrackFromCursor(cursor));
            } while(cursor.moveToNext());
        }
        return result;
    }

    public long addTraining(Training training, List<TrainingImage> images) {
        //Writing track to database
        int trackID = addTrack(training.getTrack());
        return saveTraining(training, trackID, images);
    }

    public long addTrainingOnExistingTrack(Training training, int trackID, List<TrainingImage> images) {
        return saveTraining(training, trackID, images);
    }

    private long saveTraining(Training training, int trackID, List<TrainingImage> images) {
        SQLiteDatabase database = getWritableDatabase();

        String imagesInBase64 = convertImagesListToBase64(images);
        String imagesPositions = convertImagesPositionsToString(images);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.TRAIN_TABLE_BURNED_CALORIES, training.getBurnedCalories());
        contentValues.put(DatabaseConstants.TRAIN_TABLE_LENGTH_TIME, training.getLengthTime());
        contentValues.put(DatabaseConstants.TRAIN_TABLE_SPEED_RATE, training.getSpeedRate());
        contentValues.put(DatabaseConstants.TRAIN_TABLE_USER_EMAIL, training.getUserEmail());
        contentValues.put(DatabaseConstants.TRAIN_TABLE_TRACK_ID, trackID);
        contentValues.put(DatabaseConstants.TRAIN_TABLE_DATE, training.getDate().toString());
        contentValues.put(DatabaseConstants.TRAIN_TABLE_PACE, training.getPace());
        contentValues.put(DatabaseConstants.TRAIN_TABLE_IMAGES, imagesInBase64);
        contentValues.put(DatabaseConstants.TRAIN_TABLE_IMAGES_POSITION, imagesPositions);

        long trainingID = database.insert(DatabaseConstants.TRAIN_TABLE_NAME, null, contentValues);
        database.close();
        return  trainingID;
    }

    private String convertImagesPositionsToString(List<TrainingImage> images) {
        ArrayList<Location> locations = new ArrayList<Location>();
        for(int i = 0; i < images.size(); i++) {
            locations.add(images.get(i).getLocation());
        }
        return DatabaseUtils.routeToString(locations);
    }

    public List<Training> getUserTrainings(String emailAddress) {
        String query = "select * from " + DatabaseConstants.TRAIN_TABLE_NAME + " where " + DatabaseConstants.TRAIN_TABLE_USER_EMAIL
                + " = '" + emailAddress + "'";
        SQLiteDatabase database = getReadableDatabase();
        List<Training> trainings = new ArrayList<Training>();

        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                Training training = new Training(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseConstants.TRAIN_TABLE_USER_EMAIL)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseConstants.TRAIN_TABLE_LENGTH_TIME)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseConstants.TRAIN_TABLE_BURNED_CALORIES)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseConstants.TRAIN_TABLE_SPEED_RATE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseConstants.TRAIN_TABLE_DATE)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseConstants.TRAIN_TABLE_PACE)));
                //Getting track from database
                training.setTrack(getTrack(cursor.getLong(cursor.getColumnIndex(DatabaseConstants.TRAIN_TABLE_TRACK_ID))));
                trainings.add(training);
            } while(cursor.moveToNext());
        }
        database.close();
        return trainings;
    }

    public ArrayList<TrainingImage> getImagesForTraining(int trainingID) {
        String query = "SELECT * FROM " + DatabaseConstants.TRAIN_TABLE_NAME + " WHERE " + DatabaseConstants.ID + " = " + trainingID;
        ArrayList<TrainingImage> result = new ArrayList<TrainingImage>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        //If cursor is empty return empty List
        if(!cursor.moveToFirst()) {
            Log.e("DBH", "Pusty cursor");
            return result;
        }
        String imagesInBase64 = cursor.getString(cursor.getColumnIndex(DatabaseConstants.TRAIN_TABLE_IMAGES));
        String imagesLocations = cursor.getString(cursor.getColumnIndex(DatabaseConstants.TRAIN_TABLE_IMAGES_POSITION));
        ArrayList<Location> locations = DatabaseUtils.stringToRoute(imagesLocations);
        ArrayList<Bitmap> bitmaps = DatabaseUtils.stringToImages(imagesInBase64);
        for(int i = 0; i < bitmaps.size(); i++) {
            result.add(new TrainingImage(locations.get(i), "", bitmaps.get(i)));
        }
        database.close();
        return result;
    }

    /**
     * @param trainingID  Training ID
     * @param userEmail UserEmail
     */
    private void addUserTrainingNewEntry(long trainingID, String userEmail) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.USER_TRAINING_TABLE_USER_EMAIL, userEmail);
        contentValues.put(DatabaseConstants.USER_TRAINING_TABLE_TRAINING_ID, trainingID);

        database.insert(DatabaseConstants.USER_TRAINING_TABLE_NAME, null, contentValues);
        database.close();
    }

    /**
     * Converts all images in list to one String
     * @param images to convert
     * @return String containing converted images
     */
    private String convertImagesListToBase64(List<TrainingImage> images) {
        StringBuilder builder = new StringBuilder();
        if(images.isEmpty())
            return "";
        builder.append(DatabaseUtils.ImageToBase64(images.get(0).getImage()));
        for(int i = 1; i < images.size(); i++) {
            builder.append(":");
            builder.append(DatabaseUtils.ImageToBase64(images.get(i).getImage()));
        }
        return builder.toString();
    }

    /**
     * Get Track object from cursor
     *
     * @param cursor Cursor to database, must not be null
     * @return Track object loaded from cursor
     */
    private Track getTrackFromCursor(Cursor cursor) {
        Track track = new Track();
        track.setId(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.ID)));
        track.setBestTime(cursor.getLong(cursor.getColumnIndex(DatabaseConstants.TRACK_BEST_TIME)));
        track.setLastUpdate(cursor.getString(cursor.getColumnIndex(DatabaseConstants.TRACK_LAST_UPDATE)));
        track.setLength(cursor.getDouble(cursor.getColumnIndex(DatabaseConstants.TRACK_LENGTH)));
        String area = cursor.getString(cursor.getColumnIndex(DatabaseConstants.TRACK_AREA));
        track.setArea(DatabaseUtils.stringToArea(area));
        String route = cursor.getString(cursor.getColumnIndex(DatabaseConstants.TRACK_ROUTE));
        track.setRoute(DatabaseUtils.stringToRoute(route));

        return track;
    }
}
