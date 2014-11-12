package com.mp.runand.app.logic;


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
     * Non anonymous user
     * @param uName username
     * @param token session id/token
     * @param email email
     */
    public CurrentUser(String uName, String token, String email) {
        this.emailAddress = email;
        this.token = token;
        this.userName = uName;
    }

    /**
     *
     * @return currently logged user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @return currently logged user session token
     */
    public String getToken() {
        return token;
    }

    /**
     *
     * @return currently logged user email address
     */
    public String getEmailAddress() {
        return emailAddress;
    }
}
