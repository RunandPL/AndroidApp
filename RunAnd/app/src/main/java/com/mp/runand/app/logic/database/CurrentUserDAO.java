package com.mp.runand.app.logic.database;

import android.content.Context;

/**
 * Created by Sebastian on 2014-10-13.
 * CurrentUser should not be editable after creation (its getting data from server and must be coherent all the time)
 */
public class CurrentUserDAO {
    private String userName;
    private int sessionID;
    private String emailAddress;
    private DataBaseHelper dataBaseHelper = null;

    /**
     * used when creating anonymous user (while skipping logging in)
     * @param uNameAnnonymous does not matter could be anything
     * @param context current context
     */
    public CurrentUserDAO(String uNameAnnonymous,Context context) {
        this.userName = "Anonymous";
        this.sessionID = 0;
        this.emailAddress = "";
        //todo is logged = false

        //it might take some time so don't create new one if there is on created
        if(dataBaseHelper==null) {
            dataBaseHelper = new DataBaseHelper(context);
        }
        //todo get currentUserFrom db
    }

    /**
     * Used while creating non anonymous user (while session is created)
     * @param userName username of verified user
     * @param sessionID granted session id
     * @param emailAddress email of verified user
     * @param context current context
     */
    public CurrentUserDAO(String userName, int sessionID, String emailAddress, Context context) {
        this.userName = userName;
        this.sessionID = sessionID;
        this.emailAddress = emailAddress;
        //todo is logged = true

        //it might take a some time so don't create new one if there is on created
        if(dataBaseHelper==null) {
            dataBaseHelper = new DataBaseHelper(context);
        }
        //remember as logged

        dataBaseHelper.addCurrentUser(userName, emailAddress, sessionID);
    }

    /**
     * some magic here
     * @param context
     */
    private CurrentUserDAO(Context context){
        //it might take a some time so don't create new one if there is on created
        if(dataBaseHelper==null) {
            dataBaseHelper = new DataBaseHelper(context);
        }
    }

    /**
     * some magic to return current user
     * @return currently logged user
     */
    public static CurrentUserDAO getCurrentUser(Context context){
        return new CurrentUserDAO(context);
    }

    public String getUserName() {
        return userName;
    }

    public int getSessionID() {
        return sessionID;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void logOut(){
        //no need to create new db helper there will be always one.(class is already instantiated)
        dataBaseHelper.deleteCurrentUser();
    }

}
