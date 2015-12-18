package com.minnloft.checkmyfluids.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Ryan on 10/15/2015.
 */
public class ReminderSetter extends BroadcastReceiver {

    SharedPreferences settings;

    //To reset the alarms when the device is rebooted
    @Override
    public void onReceive(Context context, Intent intent) {

        //Get the preferences
        settings = PreferenceManager.getDefaultSharedPreferences(context);

        if(settings.getBoolean("checkbox_check_notify", true)) {
            ReminderManager.setRepeatingReminderNotification(context);
        }

        if(settings.getBoolean("checkbox_oil_auto_notify", false)) {
            ReminderManager.setRepeatingOilNotification(context);
        }

        if(settings.getBoolean("checkbox_coolant_auto_notify", false)) {
            ReminderManager.setRepeatingCoolantNotification(context);
        }
    }

}
