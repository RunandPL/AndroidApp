package com.mp.runand.app.logic.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mp.runand.app.activities.Login;
import com.mp.runand.app.activities.ProfileInformation;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * task which actualize info about profile such as isPassword info and trainer email
 * Created by Mateusz on 2014-11-27.
 */
public class ProfileActualizer extends AsyncTask<Void, String, String[]> {

    Context context;
    CurrentUser currentUser;
    ProgressDialog progressDialog;

    //internal variables
    boolean isError = false;
    String error;
    boolean tokenFailure = false;

    public ProfileActualizer(Context ctx, CurrentUser cu) {
        context = ctx;
        currentUser = cu;

        progressDialog = new ProgressDialog(context);
    }

    /**
     * run progress dialog
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
     * get data
     * @param voids nothing
     * @return data to upload
     */
    @Override
    protected String[] doInBackground(Void... voids) {
        try {
            //for debug only
            //android.os.Debug.waitForDebugger();
            String[] response = new String[2];

            response[1] = getTrainerInfo();
            response[0] =  getPasswordInfo();

            return response;
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

        publishProgress();
        return null;
    }

    /**
     * close progress dialog begin updating
     * @param values values to update
     */
    @Override
    protected void onPostExecute(String... values) {
        if (!isError) {
            publishProgress(values);
        }else{
            if(tokenFailure){
                unauthorized();
            }else{
                Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
            }
        }
        progressDialog.dismiss();
    }

    /**
     * updating data
     * @param values values to update
     */
    @Override
    protected void onProgressUpdate(String... values) {
        if (values.length != 0) {
            String trainer = values[1];
            boolean isTrainer = values[1].length() != 0;
            boolean isPassword = values[0].equals("true");
            ((ProfileInformation) context).updateView(isPassword, isTrainer, trainer);
        } else {
            ((ProfileInformation)context).updateView();
            //((ProfileInformation) context).updateView(true, true, "test");
        }
    }

    /**
     * obtaining trainer data from server
     * @return trainer name
     * @throws IOException
     * @throws JSONException
     */
    private String getTrainerInfo() throws IOException, JSONException {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams,Constants.timeoutConnection);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

        HttpGet request = new HttpGet(Constants.server + Constants.getTrainer);
        request.setHeader("Authorization", "Bearer " + currentUser.getToken());

        HttpResponse serverResponse = httpClient.execute(request);
        if(serverResponse.getStatusLine().getStatusCode()==404) {
            return "";
        }else if(serverResponse.getStatusLine().getStatusCode()==401) {
            isError = true;
            tokenFailure = true;
            error = "zaloguj się ponownie, twoj token autoryzacyjny wygasł";
            return "";
        }else{
            HttpEntity entity = serverResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            JSONObject response = new JSONObject(responseString);
            //JSONObject trainer = response.getJSONObject("msg");
            JSONArray trainer = (JSONArray) response.get("msg");
            return trainer.get(0).toString().split("\"")[3];
        }
    }

    /**
     * Obtaining password existence data from server
     * @return true if password was set otherwise false
     * @throws IOException
     * @throws JSONException
     */
    private String getPasswordInfo() throws IOException, JSONException {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams,Constants.timeoutConnection);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

        HttpGet request = new HttpGet(Constants.server + Constants.isPassword);
        request.setHeader("Authorization", "Bearer " + currentUser.getToken());

        HttpResponse serverResponse = httpClient.execute(request);

        if(serverResponse.getStatusLine().getStatusCode()==404) {
            return "false";
        }else if(serverResponse.getStatusLine().getStatusCode()==401) {
            isError = true;
            tokenFailure = true;
            error = "zaloguj się ponownie, twoj token autoryzacyjny wygasł";
            return "";
        }else if(serverResponse.getStatusLine().getStatusCode()==200){
            return "true";
        } else {
            isError = true;
            error += "\ninternal error";
            return "false";
        }
    }

    /**
     * redirect to login if auth token is outdated
     */
    private void unauthorized(){
        Toast.makeText(context,error,Toast.LENGTH_LONG).show();
        DataBaseHelper.getInstance(context).deleteCurrentUser();
        context.startActivity(new Intent(context,Login.class));
        ((Activity) context).finish();
    }
}
