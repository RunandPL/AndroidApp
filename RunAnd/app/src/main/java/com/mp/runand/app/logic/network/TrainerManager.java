package com.mp.runand.app.logic.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.entities.Trainer;

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
 * Task which accept or reject trainer
 * Created by Mateusz on 2014-11-17.
 */
public class TrainerManager extends AsyncTask<JSONObject, Void, JSONObject[]> {

    //needed for activity update
    List<Trainer> trainerList;
    Context context;
    ListView listView;
    CurrentUser currentUser;
    ProgressDialog progressDialog;
    ArrayAdapter arrayAdapter;

    //internal variables
    boolean isError = false;
    String error = "";
    boolean success = false;

    public TrainerManager(Context context, ListView lv, ArrayAdapter aa, CurrentUser cu, List<Trainer> trainers) {
        this.context = context;
        this.listView = lv;
        this.currentUser = cu;
        this.arrayAdapter = aa;
        this.trainerList = trainers;

        progressDialog = new ProgressDialog(context);
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
    protected JSONObject[] doInBackground(JSONObject... jsonObjects) {
        try {
            //only for debugging
            //android.os.Debug.waitForDebugger();
            //setting timeouts for connection
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.timeoutConnection);

            //connecting
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(Constants.server + jsonObjects[0].getString(Constants.type));
            request.setHeader("Authorization", "Bearer " + currentUser.getToken());

            //adding params to request
            StringEntity stringEntity = new StringEntity(jsonObjects[0].toString());
            stringEntity.setContentType("application/json");
            request.setEntity(stringEntity);

            //execute and obtaining response
            HttpResponse serverResponse = httpClient.execute(request);
            if (serverResponse.getStatusLine().getStatusCode() == 200) {
                success = true;
            }
            HttpEntity entity = serverResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

            return  new JSONObject[] {jsonObjects[0], new JSONObject(responseString)};
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
    protected void onPostExecute(JSONObject[] jsonObjects) {
        if (success) {
            try {
                updateList(jsonObjects[0].getLong(Constants.requestID));
                showMessage(jsonObjects);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (isError) {
            progressDialog.dismiss();
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.dismiss();
            try {
                Toast.makeText(context, jsonObjects[1].getString("msg"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Removing trainer from the list of available trainers and updating view
     *
     * @param TrainerId id of trainer to remove
     */
    private void updateList(long TrainerId) {
        Trainer toRemove = null;
        for (Trainer t : trainerList) {
            if (t.getId() == TrainerId) {
                toRemove = t;
            }
        }
        trainerList.remove(toRemove);
        arrayAdapter = new ArrayAdapter<Trainer>(context, android.R.layout.simple_list_item_1, trainerList);
        listView.setAdapter(arrayAdapter);
        ((Activity) context).registerForContextMenu(listView);
    }

    /**
     * Show status message and close activity if action was accept
     *
     * @param jsonObjects params
     * @throws JSONException
     */
    private void showMessage(JSONObject[] jsonObjects) throws JSONException {
        progressDialog.dismiss();
        Toast.makeText(context, jsonObjects[1].getString("msg"), Toast.LENGTH_SHORT).show();
        if (jsonObjects[0].getString(Constants.type).equals(Constants.AcceptTrainer)) {
            ((Activity) context).finish();
        }
    }
}
