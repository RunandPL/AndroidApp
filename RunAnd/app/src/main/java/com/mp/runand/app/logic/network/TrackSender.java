package com.mp.runand.app.logic.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.activities.Login;
import com.mp.runand.app.activities.MainActivity;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.entities.Track;

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
 * Created by Mateusz on 2014-11-26.
 */
public class TrackSender extends AsyncTask<Track,Void,JSONObject> {

    Context context;
    CurrentUser currentUser = null;
    ProgressDialog progressDialog;

    //internal variables
    Boolean isError = false;
    String error = "";
    boolean tokenFailure = false;
    boolean success = false;

    public TrackSender(Context ctx, CurrentUser cu){
        context = ctx;
        currentUser = cu;

        progressDialog = new ProgressDialog(ctx);
    }

    /**
     * create progress dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Sending data to server ...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * Sending track to server and obtaining answer status
     * @param track track to send
     * @return response to show notification
     */
    @Override
    protected JSONObject doInBackground(Track... track) {
        try {
            //uncomment only for debugging
            //android.os.Debug.waitForDebugger();

            //creating Json
            JSONObject requestJson = JSONRequestBuilder.buildSendTrackRequestAsJson(track[0], currentUser);

            //setting timeouts for connection
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.timeoutConnection);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(Constants.server + requestJson.getString(Constants.type));
            request.setHeader("Authorization", "Bearer " + currentUser.getToken());

            //adding params to request
            StringEntity stringEntity = new StringEntity(requestJson.toString());
            stringEntity.setContentType("application/json");
            request.setEntity(stringEntity);

            //execute and obtaining response
            HttpResponse serverResponse = httpClient.execute(request);
            if(serverResponse.getStatusLine().getStatusCode()==200){
                success=true;
                HttpEntity entity = serverResponse.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");

                return new JSONObject(responseString);
            }if(serverResponse.getStatusLine().getStatusCode()==401){
                isError = true;
                tokenFailure = true;
                error = "zaloguj się ponownie, twoj token autoryzacyjny wygasł";
                return null;
            }
//            HttpEntity entity = serverResponse.getEntity();
//            String responseString = EntityUtils.toString(entity, "UTF-8");
//
//            return new JSONObject(responseString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            isError = true;
            error += "internal error";
        } catch (JSONException e) {
            e.printStackTrace();
            isError = true;
            error += "internal error";
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            isError = true;
            error += "internal error";
        } catch (IOException e) {
            e.printStackTrace();
            isError = true;
            error += "Can not connect to the server!";
        }
        return null;
    }

    /**
     * Show message and close dialog
     * @param jsonObject server response
     */
    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        progressDialog.dismiss();
        if (success) {
            Toast.makeText(context, context.getString(R.string.TrackSavedMessage), Toast.LENGTH_SHORT).show();
        } else if (isError) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            if(tokenFailure){
                DataBaseHelper.getInstance(context).deleteCurrentUser();
                context.startActivity(new Intent(context,Login.class));
                ((Activity) context).finish();
            }
        } else {
            try {
                Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {
        super.onCancelled(jsonObject);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
