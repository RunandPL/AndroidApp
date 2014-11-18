package com.mp.runand.app.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.entities.Track;

import java.util.ArrayList;

/**
 * Created by Sebastian on 2014-10-15.
 */
public class TrackListAdapter extends ArrayAdapter<Track> {
    private final Context context;
    private final ArrayList<Track> values;

    public TrackListAdapter(Context context, ArrayList<Track> values) {
        super(context, R.layout.activity_list_tracks, values.toArray(new Track[values.size()]));
        if(values.isEmpty()) {
            Toast.makeText(context, "Puste zapytanie", Toast.LENGTH_SHORT).show();
        }
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        TextView descriptionView = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values.get(position).toString());
        descriptionView.setText("Krótki opis trasy ");
        return rowView;
    }
}
