package com.mp.runand.app.logic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
        super(context, R.layout.activity_list_trainings, values.toArray(new Track[values.size()]));
        if(values.isEmpty()) {
            //Toast.makeText(context, "Puste zapytanie", Toast.LENGTH_SHORT).show();
        }
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Track track = getItem(position);
        CheckBox checkBox;
        TextView textView;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.track_row_layout, parent, false);
            textView = (TextView) convertView.findViewById(R.id.trackLabel);
            checkBox = (CheckBox) convertView.findViewById(R.id.trackCheckBox);

            // Optimization: Tag the row with it's child views, so we don't have to
            // call findViewById() later when we reuse the row.
            convertView.setTag(new TrackHolder(checkBox, textView));

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox checkBox = (CheckBox) view;
                    Track track = (Track) checkBox.getTag();
                    track.setChoosen(checkBox.isChecked());
                }
            });
        } else {
            // Reuse existing row view
            // Because we use a ViewHolder, we avoid having to call findViewById().
            TrackHolder trackHolder = (TrackHolder) convertView.getTag();
            checkBox = trackHolder.getCheckBox();
            textView = trackHolder.getTextView();
        }
        // Tag the CheckBox with the Track it is displaying, so that we can
        // access the planet in onClick() when the CheckBox is toggled.
        checkBox.setTag(track);
        checkBox.setChecked(track.isChoosen());
        textView.setText(track.toString());
        return convertView;
    }

    /*Holds view childs for one row */
    private class TrackHolder {
        private CheckBox checkBox;
        private TextView textView;

        public TrackHolder() {}
        public TrackHolder(CheckBox checkBox, TextView textView) {
            this.textView = textView;
            this.checkBox = checkBox;
        }

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }
    }
}
