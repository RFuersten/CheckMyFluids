package com.minnloft.checkmyfluids.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.minnloft.checkmyfluids.R;

import java.util.ArrayList;

/**
 * Created by Ryan on 9/27/2015.
 */
public class LogAdapter extends ArrayAdapter<Log> {

    //View lookup cache
    private static class ViewHolder {
        TextView dateView;
        TextView amountView;
        TextView milesView;

        public ViewHolder(View view) {
            dateView = (TextView) view.findViewById(R.id.textview_date);
            amountView = (TextView) view.findViewById(R.id.textview_amount);
            milesView = (TextView) view.findViewById(R.id.textview_miles);
        }
    }

    //Declaring our ArrayList of items
    private ArrayList<Log> logs;

    public LogAdapter(Context context, ArrayList<Log> logs) {
        super(context, R.layout.list_item_log, logs);
        this.logs = logs;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        // Get the data item for this position
        Log log = getItem(position);

        //Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_log, parent, false);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Populate the data into the template view using the data object
        viewHolder.dateView.setText(log.getmDateAsString());
        viewHolder.amountView.setText(log.getmAmount());
        viewHolder. milesView.setText(log.getmMiles());

        //Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount(){
        return logs.size();
    }

}