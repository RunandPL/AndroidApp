package com.mp.runand.app.logic.database;

/**
 * Created by Sebastian on 2014-10-13.
 */
public class User {
    private String userName;
    private int sessionID;
    private String emailAddress;

    public User() {
        userName = "";
        sessionID = 0;
        emailAddress = "";
    }

    public User(String userName, int sessionID, String emailAddress) {
        this.userName = userName;
        this.sessionID = sessionID;
        this.emailAddress = emailAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
