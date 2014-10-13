package com.mp.runand.app.logic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sebastian on 2014-10-13.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static String DATA_BASE_NAME = "RunAndDataBase";
    private static int DATA_BASE_VERSION = 1;

    //Users table names
    private static UserTableData userTableData = new UserTableData();

    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + userTableData.TABLE_NAME
            + "(" + userTableData.USER_NAME + " TEXT PRIMARY KEY," + userTableData.EMAIL_ADDRESS + " TEXT NOT NULL,"
            + userTableData.SESSION_ID + " INTEGER" + ")";

    public DataBaseHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase dataBase) {
        dataBase.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
        //Right now, I think we do not need this so I will not do anything here
    }

    /*
    Put given user in database
     */
    public void addUser(User user){
        SQLiteDatabase dataBase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(userTableData.USER_NAME, user.getUserName());
        contentValues.put(userTableData.EMAIL_ADDRESS, user.getEmailAddress());
        contentValues.put(userTableData.SESSION_ID, user.getSessionID());

        dataBase.insert(userTableData.TABLE_NAME, null, contentValues);
        dataBase.close();
    }
    /*
    Delete user with given User Name form database
     */
    public void deleteUser(String userName) {
        SQLiteDatabase dataBase = getWritableDatabase();

        String[] parametersTable = new String[]{userName};
        String query = userTableData.USER_NAME + " = ?";

        dataBase.delete(userTableData.TABLE_NAME, query, parametersTable);
        dataBase.close();
    }
    /*
    Get user with given User Name from database
    @return null if there is no user with given user name
     */
    public User getUser(String userName) {
        SQLiteDatabase dataBase = getReadableDatabase();

        String query = "SELECT * FROM " + userTableData.TABLE_NAME + " WHERE " + userTableData.USER_NAME +
                " = '" + userName + "'";

        //Getting cursor to database
        Cursor cursor = dataBase.rawQuery(query, null);

        if(cursor == null) {
            //There is no user with given user name
            return null;
        }
        cursor.moveToFirst();

        User user = new User();
        user.setEmailAddress(cursor.getString(cursor.getColumnIndex(userTableData.EMAIL_ADDRESS)));
        user.setUserName(cursor.getString(cursor.getColumnIndex(userTableData.USER_NAME)));
        user.setSessionID(cursor.getInt(cursor.getColumnIndex(userTableData.SESSION_ID)));

        dataBase.close();
        return user;
    }

}
