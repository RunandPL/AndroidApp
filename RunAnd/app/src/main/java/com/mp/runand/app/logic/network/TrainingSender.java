package com.mp.runand.app.logic.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.entities.Training;
import com.mp.runand.app.logic.training.TrainingImage;

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
import java.util.List;

/**
 * Task which sends trainings to server
 * Created by Mateusz on 2014-11-25.
 */
public class TrainingSender extends AsyncTask<Training,Void,JSONObject> {

    Context context;
    CurrentUser currentUser;
    ProgressDialog progressDialog;
    List<TrainingImage> images;

    //internal variables
    Boolean isError = false;
    String error = "";
    boolean success = false;

    public TrainingSender(Context ctx, CurrentUser cu){
        context=ctx;
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
     * Sending training to server and obtaining answer status
     * @param training training object
     * @return response to show notification
     */
    @Override
    protected JSONObject doInBackground(Training... training) {
        try {
            //uncomment only for debugging
            //android.os.Debug.waitForDebugger();

            //creating Json
            JSONObject requestJson = JSONRequestBuilder.buildSendTrainingRequestAsJson(training[0], currentUser);

            //setting timeouts for connection
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters,Constants.timeoutConnection);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(Constants.server + requestJson.getString(Constants.type));
            request.setHeader("Authorization", "Bearer " + currentUser.getToken());

            //adding params to request
            StringEntity stringEntity = new StringEntity(requestJson.toString());
            stringEntity.setContentType("application/json");
            request.setEntity(stringEntity);

            //execute and obtaining response
            HttpResponse serverResponse = httpClient.execute(request);
            Log.e("TS", String.valueOf(serverResponse.getStatusLine().getStatusCode()));
            if(serverResponse.getStatusLine().getStatusCode()==200) {
                success=true;
            }
            HttpEntity entity = serverResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

            return new JSONObject(responseString);
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
             Toast.makeText(context,context.getString(R.string.TrainingSavedMessage),Toast.LENGTH_SHORT).show();
        } else if (isError) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        } else {
//            try {
//                //Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
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
