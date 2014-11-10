package com.mp.runand.app.logic.entities;


/**
 * Created by Sebastian on 2014-10-13.
 * CurrentUser should not be editable after creation (its getting data from server and must be coherent all the time)
 */
public class CurrentUser {
    private String userName;
    private String token;
    private String emailAddress;

    /**
     * Anonymous user
     */
    public CurrentUser() {
        userName = "";
        token = "";
        emailAddress = "";
    }

    /**
     * Non anonymous logging
     * @param uName username
     * @param token session id/token
     * @param email email
     */
    public CurrentUser(String uName, String token, String email) {
        this.emailAddress = email;
        this.token = token;
        this.userName = uName;
    }

    public String getUserName() {
        return userName;
    }

    public String getToken() {
        return token;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
