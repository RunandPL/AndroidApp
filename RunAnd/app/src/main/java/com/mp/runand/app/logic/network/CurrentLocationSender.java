package com.mp.runand.app.logic.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.training.MessagesList;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.mp.runand.app.logic.network.Constants.server;
import static com.mp.runand.app.logic.network.Constants.timeoutConnection;

/**
 * Sends current location during training
 * don't have to be checked if finished correctly
 * (next position will be sent anyway)
 * Created by Mateusz on 2014-11-29.
 */
public class CurrentLocationSender extends AsyncTask<JSONObject, Void, Boolean> {

    Context context;
    CurrentUser currentUser;

    private boolean isError = false;

    boolean tokenFailure = false;
    String error = "";

    public CurrentLocationSender(Context ctx, CurrentUser cu) {
        context = ctx;
        currentUser = cu;
    }

    @Override
    protected Boolean doInBackground(JSONObject... jsonObjects) {

        try {
            return !sendLocation(jsonObjects[0]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            isError = true;
        } catch (JSONException e) {
            e.printStackTrace();
            isError = true;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            isError = true;
        } catch (IOException e) {
            e.printStackTrace();
            isError = true;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (tokenFailure) {
            unauthorized();
        }
    }

    boolean sendLocation(JSONObject jsonObject) throws JSONException, IOException {
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
        Log.e("CLS", String.valueOf(serverResponse.getStatusLine().getStatusCode()));
        if (serverResponse.getStatusLine().getStatusCode() == 200) {

            Log.e("CLS", "Resopnse 200");
            HttpEntity entity = serverResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            JSONObject response = new JSONObject(responseString);
            JSONObject msg = (JSONObject) response.get("msg");
            JSONArray msgs = (JSONArray) msg.get("messages");
            int length = msgs.length();
            if(length>0) {
                JSONObject toRead = msgs.getJSONObject(length - 1);
                MessagesList.getInstance().putMessages(toRead.getString("msg"));
                Log.e("CLS", toRead.getString("msg"));
            }
            return true;
        } else if (serverResponse.getStatusLine().getStatusCode() == 401) {
            tokenFailure = true;
            error = "zaloguj się ponownie, twoj token autoryzacyjny wygasł";
            return false;
        } else {
            return false;
        }
    }

    private void unauthorized() {
        Toast.makeText(context, "Twoj token autoryzacyjny wygasł! " +
                        "\nWyniki nie będą aktualizowane!",
                Toast.LENGTH_LONG).show();
    }
}
