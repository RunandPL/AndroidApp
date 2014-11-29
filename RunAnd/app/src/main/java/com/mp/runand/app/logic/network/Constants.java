package com.mp.runand.app.logic.network;

/**
 * Created by Mateusz on 2014-10-29.
 */
public class Constants {

    //Server
    //for testing
    //pawel
    //public static final String server ="http://89.79.234.30:3000/";
    //global
    public static final String server = "http://api.runand.greeters.pl:3500/";
    public static final int timeoutConnection = 4000;

    //JSON types
    public static final String type = "type";
    public static final String GLogInType = "login/google";
    public static final String LogInType = "login";
    public static final String RegisterType = "register";
    public static final String GetTrainerList = "api/connect";
    public static final String RejectTrainer = "api/connect/reject";
    public static final String AcceptTrainer = "api/connect/accept";
    public static final String SendTraining = "api/workout";
    public static final String SendTrack = "api/route";
    public static final String getTrainer = "api/trainer";
    public static final String isPassword = "api/password";
    public static final String setPassword = "api/password/change";
    public static final String beginLiveTraining = "api/live/start";
    public static final String stopLiveTaining = "api/live/stop";
    public static final String liveUpdate = "api/live/update";

    //JSON login
    public static final String gmailAcc = "uname";
    public static final String mail = "username";
    public static final String password = "password";

    //accept trainer
    public static final String requestID = "requestID";

    //sending tracks and trainings
    public static final String track = "route";
    public static final String description = "description";
    public static final String title = "title";
    public static final String isPublic= "isPublic";
    public static final String length = "length";
    public static final String speedRate = "speedRate";
    public static final String burnedCalories = "burnedCalories";
    public static final String lengthTime = "lengthTime";

    //training manager
    public static String training = "training";
    public static String latitude = "x";
    public static String longitude = "y";
    public static String altitude = "z";

}
