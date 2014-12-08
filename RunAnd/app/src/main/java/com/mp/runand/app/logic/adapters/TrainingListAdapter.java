package com.mp.runand.app.logic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.entities.Training;

import java.util.ArrayList;

/**
 * Created by Sebastian on 2014-10-15.
 */
public class TrainingListAdapter extends ArrayAdapter<Training> {
    private final Context context;
    private final ArrayList<Training> values;

    public TrainingListAdapter(Context context, ArrayList<Training> values) {
        super(context, R.layout.activity_list_trainings, values.toArray(new Training[values.size()]));
        if(values.isEmpty()) {
            //Toast.makeText(context, "Puste zapytanie", Toast.LENGTH_SHORT).show();
        }
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.label);
        TextView descriptionView = (TextView) convertView.findViewById(R.id.secondLine);
        textView.setText(values.get(position).toString());
        descriptionView.setText(values.get(position).getDescription());
        return convertView;
    }
}
