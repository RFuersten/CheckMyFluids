package com.minnloft.checkmyfluids.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.minnloft.checkmyfluids.LogFluidsActivity;
import com.minnloft.checkmyfluids.MainActivity;
import com.minnloft.checkmyfluids.R;

import java.util.Calendar;

/**
 * Created by Ryan on 10/15/2015.
 */
public class ReminderReceiver extends BroadcastReceiver {
    private int checkNotification = 11, oilNotification = 22, coolantNotification = 33;

    SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get the preferences
        settings = PreferenceManager.getDefaultSharedPreferences(context);

        //Get the notification id from the intent
        String id = intent.getStringExtra("notification_number");
        int intID = Integer.parseInt(id);

        String quantity = "";

        //If the intent isn't a check notification intent, get the quantity from it
        if(intID != ReminderManager.CHECK_REMINDER) {
            quantity = intent.getStringExtra("quantity");

            switch (quantity) {
                case ".25":
                    quantity = "1/4";
                    break;
                case ".5":
                    quantity = "1/2";
                    break;
                case ".75":
                    quantity = "3/4";
                    break;
            }
        }


        switch(intID){
            case ReminderManager.CHECK_REMINDER:
                @SuppressWarnings("deprecation")
                Intent startCheckIntent = new Intent(context, MainActivity.class);

                Notification check = new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Check Fluids")
                        .setContentText("Check your vehicle's oil and coolant fluid levels.")
                        .setTicker("Check your vehicle's oil and coolant fluid levels.")
                        .setContentIntent(PendingIntent.getActivity(context, checkNotification, startCheckIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .getNotification();

                //Setup a calendar object to hold when the last time this notification was sent.
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());

                NotificationManager checkNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                checkNotificationManager.notify(ReminderManager.CHECK_REMINDER, check);

                settings.edit().putLong("last_check_notification", cal.getTimeInMillis()).commit();
                break;
            case ReminderManager.OIL_REMINDER:
                @SuppressWarnings("deprecation")
                Intent startOilIntent = new Intent(context, LogFluidsActivity.class);

                Notification oil = new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Oil Low")
                        .setContentText("Your vehicle's oil is " + quantity + " quarts low.")
                        .setTicker("Your vehicle's oil is " + quantity + " quarts low.")
                        .setContentIntent(PendingIntent.getActivity(context, oilNotification, startOilIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .getNotification();

                //Setup a calendar object to hold when the last time this notification was sent.
                Calendar oilCal = Calendar.getInstance();
                oilCal.setTimeInMillis(System.currentTimeMillis());

                NotificationManager oilNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                oilNotificationManager.notify(ReminderManager.OIL_REMINDER, oil);

                settings.edit().putLong("last_oil_notification", oilCal.getTimeInMillis()).commit();

                break;
            case ReminderManager.COOLANT_REMINDER:
                @SuppressWarnings("deprecation")
                Intent startCoolantIntent = new Intent(context, LogFluidsActivity.class);

                Notification coolant = new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Coolant Low")
                        .setContentText("Your vehicle's coolant is " + quantity + " quarts low.")
                        .setTicker("Your vehicle's coolant is " + quantity + " quarts low.")
                        .setContentIntent(PendingIntent.getActivity(context, coolantNotification, startCoolantIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .getNotification();

                //Setup a calendar object to hold when the last time this notification was sent.
                Calendar coolCal = Calendar.getInstance();
                coolCal.setTimeInMillis(System.currentTimeMillis());

                NotificationManager coolantNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                coolantNotificationManager.notify(ReminderManager.COOLANT_REMINDER, coolant);

                settings.edit().putLong("last_coolant_notification", coolCal.getTimeInMillis()).commit();
                break;
        }
    }

}


