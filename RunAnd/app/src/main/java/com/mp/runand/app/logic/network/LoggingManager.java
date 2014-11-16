package com.mp.runand.app.logic.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mp.runand.app.activities.Login;
import com.mp.runand.app.activities.MainActivity;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * Created by Mateusz on 2014-10-05.
 * Manage which is used to log in users
 */
public class LoggingManager extends AsyncTask<JSONObject, Void, JSONObject[]> {

    Context context;
    boolean showDialog;
    ProgressDialog progressDialog;
    Boolean isError = false;
    String error = "";

    public LoggingManager(Context context, boolean showDialog) {
        this.context = context;

        this.showDialog = showDialog;
        progressDialog = new ProgressDialog(context);
    }

    /**
     * Run progress dialog if flag is set
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Connecting to the server ...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * Get data from server
     *
     * @param jsonObjects 1st is request param
     * @return server response as json null when some errors have been noticed
     */
    @Override
    protected JSONObject[] doInBackground(JSONObject... jsonObjects) {
        try {
            //only for debugging
            //android.os.Debug.waitForDebugger();
            //setting timeouts for connection
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.timeoutConnection);
            //connecting
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(Constants.server + jsonObjects[0].get(Constants.type).toString());
            //adding params to request
            JSONObject param = jsonObjects[0];
            StringEntity stringEntity = new StringEntity(param.toString());
            stringEntity.setContentType("application/json");
            request.setEntity(stringEntity);
            //execute and obtaining response
            HttpResponse serverResponse = httpClient.execute(request);
            HttpEntity entity = serverResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            //returning
            JSONObject[] ret = new JSONObject[2];
            ret[0] = param;
            ret[1] = new JSONObject(responseString);
            return ret;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            isError=true;
            error+="internal error";
        } catch (JSONException e) {
            e.printStackTrace();
            isError=true;
            error+="internal error";
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            isError=true;
            error+="internal error";
        } catch (IOException e) {
            e.printStackTrace();
            isError=true;
            error+="Can not connect to the server!";
        }
        return null;
    }

    /**
     * Add user as logged when got request
     * @param jsonObjects request and answer
     */
    @Override
    protected void onPostExecute(JSONObject[] jsonObjects) {
        super.onPostExecute(jsonObjects);
        if(jsonObjects!=null) {
            try {
                if (jsonObjects[0].getString(Constants.type).equals(Constants.GLogInType)
                        || jsonObjects[0].getString(Constants.type).equals(Constants.LogInType)) {
                    addUserAsLogged(jsonObjects);
                } else if (jsonObjects[0].get(Constants.type).equals(Constants.RegisterType)) {
                    Toast.makeText(context, jsonObjects[1].getString("msg"), Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, Login.class));
                    ((Activity) context).finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (showDialog) {
                    progressDialog.dismiss();
                }
            }
        }else{
            if(showDialog){
                progressDialog.dismiss();
            }
            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * add user as logged in system (db etc)
     *
     * @param jsonObjects 0 request 1 response
     * @throws JSONException
     */
    public void addUserAsLogged(JSONObject[] jsonObjects) throws JSONException {
        DataBaseHelper db = DataBaseHelper.getInstance(context);
        //g+ login
        if (jsonObjects[0].get(Constants.type).equals(Constants.GLogInType)) {
            db.addCurrentUser(new CurrentUser(
                    jsonObjects[0].getString(Constants.gmailAcc),
                    jsonObjects[1].getString("token"),
                    jsonObjects[0].getString(Constants.mail)
            ));
        } else {//normal login
            //todo check
            db.addCurrentUser( new CurrentUser(
                    jsonObjects[0].getString(Constants.gmailAcc),
                    jsonObjects[1].getString("token"),
                    jsonObjects[0].getString(Constants.mail)
            ));
        }
        context.startActivity(new Intent(context,MainActivity.class));
        ((Activity) context).finish();
    }

    @Override
    protected void onCancelled(JSONObject[] jsonObject) {
        super.onCancelled(jsonObject);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
