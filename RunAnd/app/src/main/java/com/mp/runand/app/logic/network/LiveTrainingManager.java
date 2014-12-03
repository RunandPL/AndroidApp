package com.mp.runand.app.logic.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mp.runand.app.activities.Login;
import com.mp.runand.app.activities.TrainingActivity;
import com.mp.runand.app.activities.TrainingSummation;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.entities.Training;
import com.mp.runand.app.logic.training.TrainingConstants;

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

import static com.mp.runand.app.logic.network.Constants.server;
import static com.mp.runand.app.logic.network.Constants.timeoutConnection;

/**
 * Task which begins and ends live training
 * Created by Mateusz on 2014-11-29.
 */
public class LiveTrainingManager extends AsyncTask<JSONObject, Boolean, JSONObject> {

    Context context;
    CurrentUser currentUser;
    ProgressDialog progressDialog;


    //internal variables
    boolean success;
    boolean isError = false;
    String error = "";
    boolean tokenFailure = false;
    String type;
    JSONObject j = null;

    public LiveTrainingManager(Context ctx, CurrentUser cu) {
        this.currentUser = cu;
        this.context = ctx;
        progressDialog = new ProgressDialog(ctx);
    }

    @Override
    protected JSONObject doInBackground(JSONObject... jsonObjects) {
        try {
            //only for debugging
            android.os.Debug.waitForDebugger();
            type = jsonObjects[0].getString("type");
            j=jsonObjects[0];
            success = doAction(jsonObjects[0]);
            return jsonObjects[0];
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Connecting to the server ...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        if (success) {
            publishProgress(true);
        } else if (isError) {
            if (tokenFailure) {
                unauthorized();
            } else {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        }
        progressDialog.dismiss();
    }

    /**
     * begin training
     *
     * @param values true if ok else false
     */
    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        if (values[0]) {
            if (type.equals(Constants.beginLiveTraining)) {
                ((TrainingActivity) context).setButtonsEnabled(true);
            } else {
                try {
                    Training training = (Training)j.get(Constants.training);
                    Intent newIntent = new Intent(context, TrainingSummation.class);
                    newIntent.putExtra(TrainingConstants.TRAINING, training);
                    context.startActivity(newIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    /**
     * begins or ends traning depending on json parameters
     *
     * @param jsonObject request parameters
     * @return true if success false if errors
     * @throws JSONException
     * @throws IOException
     */
    public boolean doAction(JSONObject jsonObject) throws JSONException, IOException {
        //only for debugging
        //android.os.Debug.waitForDebugger();
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

        HttpPost request = new HttpPost(server + jsonObject.getString(Constants.type));
        request.setHeader("Authorization", "Bearer " + currentUser.getToken());

        StringEntity stringEntity = new StringEntity(jsonObject.toString());
        stringEntity.setContentType("application/json");
        request.setEntity(stringEntity);

        HttpResponse serverResponse = httpClient.execute(request);
        if (serverResponse.getStatusLine().getStatusCode() == 200) {
            return true;
        } else if (serverResponse.getStatusLine().getStatusCode() == 401) {
            isError = true;
            tokenFailure = true;
            error = "zaloguj się ponownie, twoj token autoryzacyjny wygasł";
            return false;
        } else {
            HttpEntity entity = serverResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            JSONObject response = new JSONObject(responseString);
            return false;
        }
    }

    /**
     * redirect to login if auth token is outdated
     */
    private void unauthorized() {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
        DataBaseHelper.getInstance(context).deleteCurrentUser();
        context.startActivity(new Intent(context, Login.class));
        ((Activity) context).finish();
    }
}
