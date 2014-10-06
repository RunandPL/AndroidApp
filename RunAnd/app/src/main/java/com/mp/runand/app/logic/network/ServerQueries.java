package com.mp.runand.app.logic.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateusz on 2014-10-05.
 */
public class ServerQueries {

    JSONParser jsonParser;

    private final static String loginURL = "http://adres do logowania/";//po nim dokladane parametry

    /**
     * Checking connection with servers
     * @param cm
     * @return
     */
    protected boolean checkConnectionWithInternet(ConnectivityManager cm, URL url){
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            try {
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(3000);
                urlc.connect();
                if(urlc.getResponseCode()==200){
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    protected JSONObject loginUser(String email, String password){
        // Building Parameters
        List params = new ArrayList();
        //params.add(new BasicNameValuePair("sessionId", sessionId));jakis sposob tworzenia id sesjii na podstawie jakis danych z urzadzenia
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }
}
