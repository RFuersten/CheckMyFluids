package com.minnloft.checkmyfluids;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.minnloft.checkmyfluids.notifications.ReminderManager;


public class TimePreference extends DialogPreference {

    public static final String DEFAULT_VALUE = "12:0";
    private int lastHour=0;
    private int lastMinute=0;

    String hourTemp = null;
    String minTemp = null;
    String AMPMTemp = null;

    private final static String AM = " AM";
    private final static String PM = " PM";

    private TimePicker picker=null;


    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        setPositiveButtonText("SET");
        setNegativeButtonText("CANCEL");
        setTitle("Set time");
    }


    @Override
    protected View onCreateDialogView() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.time_picker, null);

        final TextView title = (TextView) view.findViewById(R.id.popup_title);
        title.setText("Notification Time");

        picker = (TimePicker) view.findViewById(R.id.time_time_picker);

        return view;
    }


    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            lastHour = picker.getCurrentHour();
            lastMinute = picker.getCurrentMinute();
            String time = String.valueOf(lastHour) + ":" + String.valueOf(lastMinute);
            persistString(time);

            if(lastHour >= 12) {
                if (lastHour > 12)
                    hourTemp = Integer.toString(lastHour - 12);
                else
                    hourTemp = Integer.toString(lastHour);

                if(lastMinute < 10)
                    minTemp = "0" + Integer.toString(lastMinute);
                else
                    minTemp = Integer.toString(lastMinute);

                AMPMTemp = PM;
                setSummary(hourTemp + ":" + minTemp + AMPMTemp);

            }
            else {
                if (lastHour == 0)
                    hourTemp = "12";
                else
                    hourTemp = Integer.toString(lastHour);

                if(lastMinute < 10)
                    minTemp = "0" + Integer.toString(lastMinute);
                else
                    minTemp = Integer.toString(lastMinute);

                AMPMTemp = AM;
                setSummary(hourTemp + ":" + minTemp + AMPMTemp);
            }

            //Create a new alarm with updated information
            String key = getKey();

            switch(key){
                case "set_notify_time":
                    //Update the check notification with new time
                    ReminderManager.setRepeatingReminderNotification(getContext());
                    Toast.makeText(getContext(), "Check fluids notification updated",
                            Toast.LENGTH_LONG).show();
                    break;
                case "auto_notify_oil_time":
                    //Update the check notification with new time
                    ReminderManager.setRepeatingOilNotification(getContext());
                    Toast.makeText(getContext(), "Oil notification updated",
                            Toast.LENGTH_LONG).show();
                    break;
                case "auto_notify_coolant_time":
                    //Update the check notification with new time
                    ReminderManager.setRepeatingCoolantNotification(getContext());
                    Toast.makeText(getContext(), "Coolant notification updated",
                            Toast.LENGTH_LONG).show();
                    break;
            }

        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time = null;
        String hourTemp = null;
        String minTemp = null;

        if (restorePersistedValue){
            time = getPersistedString(DEFAULT_VALUE);
            lastHour=getHour(time);
            lastMinute=getMinute(time);
           if(lastHour >= 12) {
               if (lastHour > 12)
                    hourTemp = Integer.toString(lastHour - 12);
               else
                    hourTemp = Integer.toString(lastHour);

                if(lastMinute < 10)
                    minTemp = "0" + Integer.toString(lastMinute);
                else
                    minTemp = Integer.toString(lastMinute);

                AMPMTemp = PM;
                setSummary(hourTemp + ":" + minTemp + AMPMTemp);

            }
            else {
               if (lastHour == 0)
                   hourTemp = "12";
               else
                    hourTemp = Integer.toString(lastHour);

               if(lastMinute < 10)
                    minTemp = "0" + Integer.toString(lastMinute);
               else
                    minTemp = Integer.toString(lastMinute);

               AMPMTemp = AM;
               setSummary(hourTemp + ":" + minTemp + AMPMTemp);
            }
        }
        else{
            time= defaultValue.toString();
            persistString(time);

            lastHour=getHour(time);
            lastMinute=getMinute(time);
            hourTemp = Integer.toString(lastHour);
            if(lastMinute < 10)
                minTemp = "0" + Integer.toString(lastMinute);
            else
                minTemp = Integer.toString(lastMinute);

            AMPMTemp = PM;
            setSummary(hourTemp + ":" + minTemp + AMPMTemp);
        }
    }

    //Helper method to get the hour from the time string
    public static int getHour(String time) {
        String[] pieces=time.split(":");
        return(Integer.parseInt(pieces[0]));
    }

    //Helper method to get the minute from the time string
    public static int getMinute(String time) {
        String[] pieces=time.split(":");
        return(Integer.parseInt(pieces[1]));
    }

}