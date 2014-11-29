package com.mp.runand.app.logic.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.activities.Login;
import com.mp.runand.app.activities.ProfileInformation;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Task which changing user password
 * Created by Mateusz on 2014-11-29.
 */
public class PasswordSetter extends AsyncTask<JSONObject, Boolean, Boolean> {

    Context context;
    CurrentUser currentUser;
    ProgressDialog progressDialog;

    //internal variables
    boolean isError = false;
    String error;
    boolean tokenFailure = false;

    public PasswordSetter(Context ctx, CurrentUser cu) {
        context = ctx;
        currentUser = cu;

        progressDialog = new ProgressDialog(ctx);
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
    protected void onPostExecute(Boolean values) {
        if (!isError) {
            publishProgress(values);
            Toast.makeText(context,context.getText(R.string.password_set_ok),Toast.LENGTH_SHORT).show();
        } else {
            if (tokenFailure) {
                unauthorized();
            } else {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        }
        progressDialog.dismiss();
    }

    @Override
    protected Boolean doInBackground(JSONObject... jsonObjects) {
        try {
            //for debug only
            //android.os.Debug.waitForDebugger();

            return setPassword(jsonObjects[0]);
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
     * UI actualize if succeed in password change
     *
     * @param values state of changing password true if succeed false otherwise
     */
    @Override
    protected void onProgressUpdate(Boolean... values) {
        if (values[0]) {
            ((ProfileInformation) context).updateView(values[0]);
        }
    }

    /**
     * change user password
     *
     * @param jsonRequest json req of password change
     * @return state of changing password true if succeed false otherwise
     * @throws IOException
     * @throws JSONException
     */
    boolean setPassword(JSONObject jsonRequest) throws IOException, JSONException {
        //for debug only
        //android.os.Debug.waitForDebugger();

        //setting timeouts for connection
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.timeoutConnection);

        //connecting
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpPost request = new HttpPost(Constants.server + Constants.setPassword);
        request.setHeader("Authorization", "Bearer " + currentUser.getToken());

        //adding params to request
        StringEntity stringEntity = new StringEntity(jsonRequest.toString());
        stringEntity.setContentType("application/json");
        request.setEntity(stringEntity);

        //execute and obtaining response
        HttpResponse serverResponse = httpClient.execute(request);
        if (serverResponse.getStatusLine().getStatusCode() == 200) {
            return true;
        } else if (serverResponse.getStatusLine().getStatusCode() == 401) {
            isError = true;
            tokenFailure = true;
            error = "zaloguj się ponownie, twoj token autoryzacyjny wygasł";
            return false;
        } else {
            return false;
        }
    }

    /**
     * If token is expired redirect to log in activity
     */
    private void unauthorized() {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
        DataBaseHelper.getInstance(context).deleteCurrentUser();
        context.startActivity(new Intent(context, Login.class));
        ((Activity) context).finish();
    }
}
