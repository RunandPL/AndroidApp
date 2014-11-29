package com.mp.runand.app.logic.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mp.runand.app.activities.Login;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.entities.Trainer;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Task which update trainer list
 * Created by Mateusz on 2014-11-16.
 */
public class TrainerListUpdater extends AsyncTask<JSONObject, Void, JSONObject[]> {

    List<Trainer> trainerList;
    Context context;
    ListView listView;
    CurrentUser currentUser;
    ProgressDialog progressDialog;
    boolean isError = false;
    String error = "";
    ArrayAdapter arrayAdapter;
    boolean success = false;
    boolean tokenFailure = false;

    public TrainerListUpdater(Context context, ListView lv, ArrayAdapter aa, CurrentUser cu, List<Trainer> trainers) {
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
            //HttpPost request = new HttpPost(Constants.server + jsonObjects[0].get(Constants.type).toString());
            HttpGet request = new HttpGet(Constants.server + jsonObjects[0].getString(Constants.type));
            request.setHeader("Authorization", "Bearer " + currentUser.getToken());
            //execute and obtaining response
            HttpResponse serverResponse = httpClient.execute(request);
            if(serverResponse.getStatusLine().getStatusCode()==200){
                success = true;
                HttpEntity entity = serverResponse.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                //returning
                JSONObject[] ret = new JSONObject[2];
                ret[0] = jsonObjects[0];
                ret[1] = new JSONObject(responseString);
                return ret;
            } else if ( serverResponse.getStatusLine().getStatusCode()==401){
                isError = true;
                tokenFailure = true;
                error = "zaloguj się ponownie, twoj token autoryzacyjny wygasł";
                return null;
            }
            HttpEntity entity = serverResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            //returning
            JSONObject[] ret = new JSONObject[2];
            ret[0] = jsonObjects[0];
            ret[1] = new JSONObject(responseString);
            return ret;
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
                trainerList.addAll(parseJsonArray(jsonObjects[1].getJSONArray("msg")));
                arrayAdapter = new ArrayAdapter<Trainer>(context, android.R.layout.simple_list_item_1, trainerList);
                listView.setAdapter(arrayAdapter);
                ((Activity) context).registerForContextMenu(listView);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            if(tokenFailure) {
                DataBaseHelper.getInstance(context).deleteCurrentUser();
                context.startActivity(new Intent(context,Login.class));
                ((Activity) context).finish();
            }
        }
    }

    private ArrayList<Trainer> parseJsonArray(JSONArray arr) throws JSONException {
        ArrayList<Trainer> trainers = new ArrayList<Trainer>();
        for (int i = 0; i < arr.length(); i++) {
            trainers.add(convertObject(arr.getJSONObject(i)));
        }
        return trainers;
    }

    private Trainer convertObject(JSONObject jsonObject) throws JSONException {
        String mail = jsonObject.getString("username");
        long id = jsonObject.getLong("requestID");

        return new Trainer(mail, id);
    }
}
