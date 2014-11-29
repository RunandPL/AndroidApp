package com.mp.runand.app.logic.network;

import com.mp.runand.app.logic.entities.Track;
import com.mp.runand.app.logic.entities.Training;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mateusz on 2014-10-29.
 * Json request builder. Bunch of static method to create json requests
 */
public class JSONRequestBuilder {

    /**
     * LogInType without google +
     *
     * @param mail     usermail
     * @param password password
     * @return log in request as json
     */
    public static JSONObject buildLogInRequestAsJson(String mail, String password, String username) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Constants.type, Constants.LogInType)
                    .put(Constants.mail, mail)
                    .put(Constants.password, password)
                    .put(Constants.gmailAcc, username)
                    .put("isTrainer", false);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObj;
    }

    /**
     * LogInType with google plus
     *
     * @param username username
     * @param mail     mail
     * @return log in with g+ request as json
     */
    public static JSONObject buildGPlusLogInRequestAsJson(String username, String mail) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Constants.type, Constants.GLogInType)
                    .put(Constants.gmailAcc, username) //username
                    .put("isTrainer", false)
                    .put(Constants.mail, mail); //mail
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObj;
    }

    /**
     * Register new user or create password for G+ user
     *
     * @param username email
     * @param password password
     * @return register request as json
     */
    public static JSONObject buildRegisterRequestAsJson(String username, String password) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Constants.type, Constants.RegisterType)
                    .put("isTrainer", false)
                    .put(Constants.mail, username)
                    .put(Constants.password, password);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObj;
    }

    /**
     * Get the list of trainers for user
     *
     * @return jsonObject with request
     */
    public static JSONObject buildGetAvailableTrainersRequestAsJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.type, Constants.GetTrainerList);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Accept trainer
     *
     * @param trainerId id of trainer to accept
     * @return jsonObject with request
     */
    public static JSONObject buildAcceptTrainerRequestAsJson(long trainerId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.type, Constants.AcceptTrainer)
                    .put(Constants.requestID, trainerId);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Reject trainer
     *
     * @param trainerId id of trainer to reject
     * @return jsonObject with request
     */
    public static JSONObject buildRejectTrainerRequestAsJson(long trainerId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.type, Constants.RejectTrainer)
                    .put(Constants.requestID, trainerId);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * track as json request
     *
     * @param t track object
     * @return json req
     */
    public static JSONObject buildSendTrackRequestAsJson(Track t) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.type, Constants.SendTrack)
                    .put(Constants.track, t.getSendableRoute())
                    .put(Constants.description, "todo get description in track")
                    .put(Constants.isPublic, true)
                    .put(Constants.length, t.getLength());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * training as json request
     *
     * @param t training
     * @return json req
     */
    public static JSONObject buildSendTrainingRequestAsJson(Training t) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.type, Constants.SendTraining)
                    .put(Constants.lengthTime, t.getLengthTime())
                    .put(Constants.burnedCalories, t.getBurnedCalories())
                    .put(Constants.speedRate, t.getSpeedRate())
                    .put(Constants.track, buildSendTrackRequestAsJson(t.getTrack()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Password change request
     *
     * @param password new password
     * @return jsonReq
     */
    public static JSONObject buildSetPasswordRequestAsJson(String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.password, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
