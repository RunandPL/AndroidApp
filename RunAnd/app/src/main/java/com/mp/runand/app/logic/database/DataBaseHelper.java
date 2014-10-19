package com.mp.runand.app.logic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mp.runand.app.logic.CurrentUser;

/**
 * Created by Sebastian on 2014-10-13.
 * SQL queries
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String CREATE_CURRENT_USER_TABLE = "CREATE TABLE " + DatabaseConstants.CURRENT_USER_TABLE_NAME
            + "(" + DatabaseConstants.CURRENT_USER_NAME + " TEXT NOT NULL," + DatabaseConstants.CURRENT_USER_EMAIL_ADDRESS + " TEXT PRIMARY KEY,"
            + DatabaseConstants.CURRENT_USER_TOKEN + " TEXT" + ")";

    public DataBaseHelper(Context context) {
        super(context, DatabaseConstants.DATA_BASE_NAME, null, DatabaseConstants.DATA_BASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase dataBase) {
        dataBase.execSQL(CREATE_CURRENT_USER_TABLE);
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
}
