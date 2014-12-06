package com.mp.runand.app.logic.training;

import android.content.Context;
import android.os.AsyncTask;

import com.mp.runand.app.activities.TrainingActivity;

/**
 * Created by Sebastian on 2014-12-06.
 */
public class ServiceCheckTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    public ServiceCheckTask(Context ctx) {
        context = ctx;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        long date = System.currentTimeMillis() + 2000;
        while(System.currentTimeMillis() < date){}
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ((TrainingActivity) context).checkTrainingStatus();
    }
}
