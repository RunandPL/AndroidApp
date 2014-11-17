package com.mp.runand.app.logic.network;

import com.mp.runand.app.logic.entities.CurrentUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mateusz on 2014-10-29.
 * Json request builder. Bunch of static method to create json requests
 */
public class JSONRequestBuilder {

    /**
     * LogInType without google +
     * @param mail usermail
     * @param password password
     * @return log in request as json
     */
    public static JSONObject buildLogInRequestAsJson(String mail, String password, String username){
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Constants.type,Constants.LogInType);
            jsonObj.put(Constants.mail, mail);
            jsonObj.put(Constants.password, password);
            jsonObj.put(Constants.gmailAcc, username);
            jsonObj.put("isTrainer",false);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObj;
    }

    /**
     * LogInType with google plus
     * @param username username
     * @param mail mail
     * @return log in with g+ request as json
     */
    public static JSONObject buildGPlusLogInRequestAsJson(String username, String mail){
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Constants.type,Constants.GLogInType);
            jsonObj.put(Constants.gmailAcc,username); //username
            jsonObj.put("isTrainer",false);
            jsonObj.put(Constants.mail,mail); //mail
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObj;
    }

    /**
     * Register new user or create password for G+ user
     * @param username email
     * @param password password
     * @return register request as json
     */
    public static JSONObject buildRegisterRequestAsJson(String username, String password){
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Constants.type,Constants.RegisterType);
            jsonObj.put("isTrainer",false);
            jsonObj.put(Constants.mail,username);
            jsonObj.put(Constants.password,password);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObj;
    }

    /**
     * Get the list of trainers for user
     * @return jsonObject with request
     */
    public static JSONObject buildGetAvailableTrainersRequestAsJson(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(Constants.type,Constants.GetTrainerList);
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Accept trainer
     * @param trainerId id of trainer to accept
     * @return jsonObject with request
     */
    public static JSONObject buildAcceptTrainerRequestAsJson(long trainerId){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(Constants.type,Constants.AcceptTrainer);
            jsonObject.put(Constants.requestID,trainerId);
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Reject trainer
     * @param trainerId id of trainer to reject
     * @return jsonObject with request
     */
    public static JSONObject buildRejectTrainerRequestAsJson(long trainerId){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(Constants.type,Constants.RejectTrainer);
            jsonObject.put(Constants.requestID,trainerId);
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Getting weather for locale
     * @param width
     * @param height
     * @param currentUser
     * @return
     */
    public static JSONObject buildWeatherInJson(double width,double height, CurrentUser currentUser){
        JSONObject jsonObj = new JSONObject();
        // try {
            //jsonObject.put
        //}
        return jsonObj;
    }
}
