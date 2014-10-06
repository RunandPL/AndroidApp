package com.mp.runand.app.logic.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import com.mp.runand.app.Enums.ServerAnswerStatus;
import com.mp.runand.app.Enums.ServerQueryType;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Mateusz on 2014-10-05.
 *
 * for safety create new instance of sm every time u need something from server
 * or remember to change query type and messages
 * might be a problem with messages and query type if sm has been used earlier and not initialized again
 * and had not been changed
 */
public class ServerManager extends AsyncTask {

    private ProgressDialog progressDialog;
    private Map<String,String> messages;
    private Context context;
    private Boolean showDialog;
    private ServerQueryType queryType;

    private static final String urlAddress="dupa";
    private static URL url = null;

    public ServerManager(Context context, ServerQueryType sqt){
        this(context ,sqt, true);
    }

    public ServerManager(Context context, ServerQueryType sqt, boolean showDialog){
        this(context, null, sqt, showDialog);
    }

    public ServerManager(Context context, Map<String,String> messages, ServerQueryType sqt, boolean showDialog){
        this.context=context;
        this.messages=messages;
        this.showDialog=showDialog;
        this.queryType=sqt;
        try{
            url = new URL(urlAddress);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        //we do not wanna show dialog when actualizing data in run time(fex during training)
        if(showDialog) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Checking Network");
            progressDialog.setMessage("Loading..");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
    }

    /**
     * obtaining data from server
     * @param params
     * @return
     */
    @Override
    protected ServerResponse doInBackground(Object[] params) {
        //im not sure if i need this later
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ServerQueries sq = new ServerQueries();

        //check server connection
        boolean isConnected = sq.checkConnectionWithInternet(cm, url);
        if(!isConnected){
            return new ServerResponse(ServerAnswerStatus.FAILURE, queryType);
        }

        //set dialog
        if(showDialog) {
            progressDialog.setTitle("Contacting Servers");
            progressDialog.setMessage("Obtaining data from servers...");
        }
        JSONObject json = null;
        //obtain resources
        if(queryType.equals(ServerQueryType.LOGIN)){
            json = sq.loginUser(messages.get("email"),messages.get("password"));
        }
        //if something from server needed just add here else if

        //return response
        return new ServerResponse(json, ServerAnswerStatus.SUCCESS, queryType);
    }

    /**
     * remove old messages and putting new one instead
     * @param newMessage
     */
    public void setMessages(Map<String,String> newMessage){
        messages = newMessage;
    }

    /**
     * Adding new k-v pair to messages
     * @param key
     * @param value
     */
    public void addMessage(String key, String value){
        messages.clear();
        messages.put(key,value);
    }

    public void setServerQueryType(ServerQueryType sqt){
        this.queryType=sqt;
    }
}
