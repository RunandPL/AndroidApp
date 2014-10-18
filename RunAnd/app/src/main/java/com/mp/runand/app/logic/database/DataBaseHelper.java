package com.mp.runand.app.logic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.mp.runand.app.logic.CurrentUser;

/**
 * Created by Sebastian on 2014-10-13.
 * SQL queries
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static DataBaseHelper instance = null;
    private static String DATA_BASE_NAME = "RunAndDataBase";
    private static int DATA_BASE_VERSION = 1;
    private static Context context;


    private static final String CREATE_CURRENT_USER_TABLE = "CREATE TABLE " + DatabaseConstants.CURRENT_USER_TABLE_NAME
            + "(" + DatabaseConstants.CURRENT_USER_NAME + " TEXT NOT NULL," + DatabaseConstants.CURRENT_USER_EMAIL_ADDRESS + " TEXT PRIMARY KEY,"
            + DatabaseConstants.CURRENT_USER_TOKEN + " TEXT" + ")";


    private static final String CREATE_TRACKS_TABLE = "CREATE TABLE " + DatabaseConstants.TRACK_TABLE_NAME
            + "(" + DatabaseConstants.ID + " INTEGER PRIMARY KEY," + DatabaseConstants.TRACK_LAST_UPDATE + " DATE NOT NULL,"
            + DatabaseConstants.TRACK_ROUTE + " LONGTEXT NOT NULL," + DatabaseConstants.TRACK_LENGTH + " UNSIGNED DOUBLE NOT NULL," +
            DatabaseConstants.TRACK_BEST_TIME + " UNSIGNED LONG NOT NULL," + DatabaseConstants.TRACK_USER_ID + " UNSIGNED LONG NOT NULL,"
            + DatabaseConstants.TRACK_LOCATION + " CHAR(14) NOT NULL" + ")";

    //Create table(id integer not null AUTO_INCREMENT, userName Text not null, trackid integer not null)

    private static final String CREATE_USER_TRACK_TABLE = "CREATE TABLE " + DatabaseConstants.USER_TRACK_NAME
            + "(" + DatabaseConstants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DatabaseConstants.USER_TRACK_USER_NAME + " TEXT NOT NULL, "
            + DatabaseConstants.USER_TRACK_TRACK_ID + " INTEGER NOT NULL)";

    public static DataBaseHelper getInstance(Context context) {
        if (instance == null) {
            //Kasowanie bazy
            //context.deleteDatabase(DATA_BASE_NAME);
            //Toast.makeText(context, "Utworzenie Instance", Toast.LENGTH_SHORT).show();
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
        dataBase.execSQL(CREATE_USER_TRACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
        //Right now, I think we do not need this so I will not do anything here
        context.deleteDatabase(DatabaseConstants.DATA_BASE_NAME);
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
        dataBase.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.CURRENT_USER_TABLE_NAME);
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

    /**
     * to logout
    public void addUser(User user) {
        SQLiteDatabase dataBase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.USER_NAME, user.getUserName());
        contentValues.put(DatabaseConstants.USER_EMAIL_ADDRESS, user.getEmailAddress());
        contentValues.put(DatabaseConstants.USER_SESSION_ID, user.getSessionID());

        dataBase.insert(DatabaseConstants.USER_TABLE_NAME, null, contentValues);
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

    /**
     *Get current user from database
     *@return null if there is no user with given user name

        String[] parametersTable = new String[]{userName};
        String query = DatabaseConstants.USER_NAME + " = ?";

        dataBase.delete(DatabaseConstants.USER_TABLE_NAME, query, parametersTable);
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

//        //should not be 0 at all but leave it for sure
//        if(cursor.getCount() == 0) {
//            //There is no user with given user name
//            return null;
//        }
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
     */
    public void addTrack(Track track, String userName) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.TRACK_BEST_TIME, track.getBestTime());
        contentValues.put(DatabaseConstants.TRACK_LAST_UPDATE, track.getLastUpdate().toString());
        contentValues.put(DatabaseConstants.TRACK_LENGTH, track.getLength());
        contentValues.put(DatabaseConstants.TRACK_LOCATION,
                DatabaseUtils.trackToString(track.getLocation()));
        contentValues.put(DatabaseConstants.TRACK_ROUTE,
                DatabaseUtils.trackToString(track.getRoute()));
        contentValues.put(DatabaseConstants.TRACK_USER_ID, track.getUserID());

        database.insert(DatabaseConstants.TRACK_TABLE_NAME, null, contentValues);
        database.close();
        //I do not know if this can be done better
        int trackID = getLatestTrackID();
        addUserTrackNewEntry(trackID, userName);
    }

    private int getLatestTrackID() {
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseConstants.TRACK_TABLE_NAME + " WHERE " + DatabaseConstants.ID
                + " = (SELECT MAX(" + DatabaseConstants.ID + ") FROM " + DatabaseConstants.TRACK_TABLE_NAME +")";

        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount() == 0)
            return -1;
        cursor.moveToFirst();
        int result = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.ID));
        database.close();
        return result;
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

    /**
     * Getting every track which belong to User with given userName
     *
     * @param userName for the user to be loaded from database
     * @return List of user tracks
     */
    public List<Track> getTracksByUser(String userName) {
        //Select * from tracks tr, usertrack ut where ut.userName = userName and ut.trackid = tr.id;
        ArrayList<Track> result = new ArrayList<Track>();
        String query = "SELECT * FROM " + DatabaseConstants.TRACK_TABLE_NAME + " tr, " + DatabaseConstants.USER_TRACK_NAME + " ut "
                + "WHERE ut." + DatabaseConstants.USER_TRACK_USER_NAME + " = '" + userName + "' and ut." + DatabaseConstants.USER_TRACK_TRACK_ID
                + " = tr." + DatabaseConstants.ID;
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                result.add(getTrackFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return result;
    }

    /**
     * @param trackID  Track Id
     * @param userName UserName
     */
    private void addUserTrackNewEntry(long trackID, String userName) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.USER_TRACK_USER_NAME, userName);
        contentValues.put(DatabaseConstants.USER_TRACK_TRACK_ID, trackID);

        database.insert(DatabaseConstants.USER_TRACK_NAME, null, contentValues);
        database.close();
    }

    /**
     * Get Track object from cursor
     *
     * @param cursor Cursor to database, must not be null
     * @return Track object loaded from cursor
     */
    private Track getTrackFromCursor(Cursor cursor) {
        Track track = new Track();
        track.setBestTime(cursor.getLong(cursor.getColumnIndex(DatabaseConstants.TRACK_BEST_TIME)));
        track.setLastUpdate(cursor.getString(cursor.getColumnIndex(DatabaseConstants.TRACK_LAST_UPDATE)));
        track.setLength(cursor.getDouble(cursor.getColumnIndex(DatabaseConstants.TRACK_LENGTH)));
        String location = cursor.getString(cursor.getColumnIndex(DatabaseConstants.TRACK_LOCATION));
        track.setLocation(DatabaseUtils.stringToTrack(location));
        String route = cursor.getString(cursor.getColumnIndex(DatabaseConstants.TRACK_ROUTE));
        track.setRoute(DatabaseUtils.stringToTrack(route));
        track.setUserID(cursor.getLong(cursor.getColumnIndex(DatabaseConstants.TRACK_USER_ID)));

        return track;
    }
}
