package com.mp.runand.app.logic.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mp.runand.app.activities.Login;
import com.mp.runand.app.activities.TrackChooseActivity;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.entities.Track;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Actualize track list
 * Created by Mateusz on 2014-12-06.
 */
public class TrackGetter extends AsyncTask<Void,Void,List<Track>> {

    Context context;
    CurrentUser currentUser;

    ProgressDialog progressDialog;
    boolean isError = false;
    String error = "";
    boolean tokenFailure;

    public TrackGetter(Context ctx, CurrentUser cu){
        context = ctx;
        currentUser = cu;

        progressDialog = new ProgressDialog(ctx);
    }

    @Override
    protected List<Track> doInBackground(Void... voids) {
        try {
            //only for debugging
            //android.os.Debug.waitForDebugger();
            DataBaseHelper dbh = DataBaseHelper.getInstance(context);
            ArrayList<Track> userTracks = getUserTracks();
            ArrayList<Track> trainerTracks = getTrainerTracks();
            ArrayList<Track> allTracksToSave = new ArrayList<Track>();
            allTracksToSave.addAll(userTracks);
            allTracksToSave.addAll(trainerTracks);
            //count geometric center of route
            for(Track t : allTracksToSave){
                double x=0;
                double y=0;
                double z=0;
                for(Location l : t.getRoute()){
                    x+=l.getLongitude();
                    y+=l.getLatitude();
                    z+=l.getAltitude();
                }
                int length = t.getRoute().size();
                Location location = new Location("mid");
                location.setLongitude(x/length);
                location.setLatitude(y/length);
                location.setAltitude(z/length);

                t.setArea(location);
                dbh.addTrack(t);
            }
        }catch(Exception e){
            e.printStackTrace();
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
    protected void onPostExecute(List<Track> tracks) {
        super.onPostExecute(tracks);
        if(!isError) {
            publishProgress();
            progressDialog.dismiss();
        }else if(tokenFailure){
            progressDialog.dismiss();
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            DataBaseHelper.getInstance(context).deleteCurrentUser();
            context.startActivity(new Intent(context,Login.class));
            ((Activity) context).finish();
        }else{
            progressDialog.dismiss();
            Toast.makeText(context,error,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        ((TrackChooseActivity)context).showTracks();
    }

    private ArrayList<Track> getUserTracks() throws IOException, JSONException {
        //only for debugging
        //android.os.Debug.waitForDebugger();
        //setting timeouts for connection
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.timeoutConnection);
        //connecting
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpGet request = new HttpGet(Constants.server + Constants.getUserTracks);
        request.setHeader("Authorization", "Bearer " + currentUser.getToken());
        //execute and obtaining response
        HttpResponse serverResponse = httpClient.execute(request);
        if(serverResponse.getStatusLine().getStatusCode()==200){
            HttpEntity entity = serverResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            JSONObject tracks = new JSONObject(responseString);
            JSONArray tracksArray = (JSONArray)tracks.get("msg");

            ArrayList<Track> ALTracks = new ArrayList<Track>();
            for(int i=0; i<tracksArray.length();i++){
                JSONObject j = (JSONObject) tracksArray.get(i);

                JSONArray route = new JSONArray((String) j.get("route"));

                ArrayList<Location>locations = new ArrayList<Location>();
                for(int k=0; k<route.length();k++){
                    JSONObject tmpLoc = (JSONObject) route.get(k);
                    Location location = new Location("tmp");

                    location.setLongitude((Double) tmpLoc.get("x"));
                    location.setLatitude((Double) tmpLoc.get("y"));
                    location.setAltitude((Double) tmpLoc.get("z"));
                    locations.add(location);
                }
                Location tmp = new Location("tmp");
                tmp.setLongitude(0);
                tmp.setLatitude(0);
                tmp.setAltitude(0);
                ALTracks.add(new Track(
                        new Date(System.currentTimeMillis()),
                        locations,
                        j.getDouble("length"),
                        0,0,tmp));
            }
            return ALTracks;
        }else if ( serverResponse.getStatusLine().getStatusCode()==401){
            isError = true;
            tokenFailure = true;
            error = "zaloguj się ponownie, twoj token autoryzacyjny wygasł";
            return null;
        }
        return null;
    }

    private ArrayList<Track> getTrainerTracks() throws IOException, JSONException {
        //only for debugging
        //android.os.Debug.waitForDebugger();
        //setting timeouts for connection
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.timeoutConnection);
        //connecting
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpGet request = new HttpGet(Constants.server + Constants.getTrainerTracks);
        request.setHeader("Authorization", "Bearer " + currentUser.getToken());
        //execute and obtaining response
        HttpResponse serverResponse = httpClient.execute(request);
        if(serverResponse.getStatusLine().getStatusCode()==200){
            HttpEntity entity = serverResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            JSONObject tracks = new JSONObject(responseString);
            JSONArray tracksArray = (JSONArray)tracks.get("msg");

            ArrayList<Track> ALTracks = new ArrayList<Track>();
            for(int i=0; i<tracksArray.length();i++){
                JSONObject j = (JSONObject) tracksArray.get(i);

                JSONArray route = new JSONArray((String) j.get("route"));

                ArrayList<Location>locations = new ArrayList<Location>();
                for(int k=0; k<route.length();k++){
                    JSONObject tmpLoc = (JSONObject) route.get(k);
                    Location location = new Location("tmp");

                    location.setLongitude((Double) tmpLoc.get("x"));
                    location.setLatitude((Double) tmpLoc.get("y"));
                    location.setAltitude((Double) tmpLoc.get("z"));
                    locations.add(location);
                }
                Location tmp = new Location("tmp");
                tmp.setLongitude(0);
                tmp.setLatitude(0);
                tmp.setAltitude(0);
                ALTracks.add(new Track(
                        new Date(System.currentTimeMillis()),
                        locations,
                        j.getDouble("length"),
                        0,0,tmp));
            }
            return ALTracks;
        }else if ( serverResponse.getStatusLine().getStatusCode()==401){
            isError = true;
            tokenFailure = true;
            error = "zaloguj się ponownie, twoj token autoryzacyjny wygasł";
            return null;
        }
        return null;
    }
}
