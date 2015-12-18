package com.minnloft.checkmyfluids.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.minnloft.checkmyfluids.Utility;
import com.minnloft.checkmyfluids.data.DataSource;
import com.minnloft.checkmyfluids.data.LogsContract;

import java.util.Calendar;

/**
 * Created by Ryan on 10/15/2015.
 */
public class ReminderManager {
    //Alarm request codes for the 3 different alarms for the app
    public final static int CHECK_REMINDER = 1;
    public final static int OIL_REMINDER = 2;
    public final static int COOLANT_REMINDER = 3;
    private final static int MILLISECS_PER_DAY = 86400000;

    private static SharedPreferences settings;

    private static String mTimeString;
    private static int mFrequency;
    private static int mFrequencyDays;
    private static int mHour;
    private static int mMinute;
    private static Calendar notificationStartTime;

    private static String mOilAmount;
    private static String mCoolantAmount;

    public static void setRepeatingReminderNotification(Context context){
        //Get the preferences
        settings = PreferenceManager.getDefaultSharedPreferences(context);

        //Get the time of day user wants notification to happen.
        mTimeString = settings.getString("set_notify_time", "12:0");
        mHour = getHour(mTimeString);
        mMinute = getMinute(mTimeString);

        setNotificationStartTime(mHour, mMinute);

        mFrequencyDays = settings.getInt("notify_frequency", 7);
        mFrequency = mFrequencyDays;

        //Calculate when the last time the auto notification when off.
        Calendar lastNotification = Calendar.getInstance();
        lastNotification.setTimeInMillis(settings.getLong("last_check_notification", lastNotification.getTimeInMillis()));
        lastNotification.set(Calendar.HOUR_OF_DAY, 0);
        lastNotification.set(Calendar.MINUTE, 0);
        lastNotification.set(Calendar.SECOND, 0);
        lastNotification.set(Calendar.MILLISECOND, 0);

        Calendar todaysDate = setTodaysDate();

        int daysSinceLastNotification = (int)(todaysDate.getTimeInMillis() - lastNotification.getTimeInMillis()) / MILLISECS_PER_DAY;

        if(daysSinceLastNotification <= 0){
            //Do nothing, reset the notification with user frequency setting
        }
        else{
            int temp = mFrequency - daysSinceLastNotification;

            if (temp > 0){
                mFrequency = temp;;
            }
        }

        notificationStartTime.add(Calendar.DAY_OF_YEAR, mFrequency);

        Intent intent = new Intent(context, ReminderReceiver.class);
            intent.putExtra("notification_number", Integer.toString(CHECK_REMINDER));

        PendingIntent pi = PendingIntent.getBroadcast(context, CHECK_REMINDER, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, notificationStartTime.getTimeInMillis(), mFrequencyDays * MILLISECS_PER_DAY, pi);

    }

    public static void cancelRepeatingReminderNotification(Context context){
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("notification_number", Integer.toString(CHECK_REMINDER));
        PendingIntent pi = PendingIntent.getBroadcast(context, CHECK_REMINDER, intent, 0);

        //If PendingIntent pi != null then you can create an alarmmanager and cancel the pi
        if (pi != null) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);
        }
    }


    public static void setRepeatingOilNotification(Context context){
        DataSource mDataSource = new DataSource(context);
        mDataSource.open();

        int databaseLength = mDataSource.getLength(LogsContract.OilTable.TAG);

        //Only set a oil auto notify if oil logs have two or more data entries so we can calculate when
        //to send the notification
        if (databaseLength >= 2) {
            String[] dates = mDataSource.getAllDates(LogsContract.OilTable.TAG);
            String[] amounts = mDataSource.getAllAmounts(LogsContract.OilTable.TAG);
            String[] miles = mDataSource.getAllMiles(LogsContract.OilTable.TAG);

            //Get the preferences
            settings = PreferenceManager.getDefaultSharedPreferences(context);

            //Get the time of day user wants notification to happen.
            mTimeString = settings.getString("auto_notify_oil_time", "12:0");
            mHour = getHour(mTimeString);
            mMinute = getMinute(mTimeString);

            setNotificationStartTime(mHour, mMinute);

            //Figure out algorithm to calculate how many days the application thinks you'll run out of data
            //based on the current logged oil logs.
            mOilAmount = settings.getString("oil_amount", ".5");

            double tempDays = 1;

            switch (mOilAmount){
                case ".25":
                    tempDays = Utility.calculateDaysToLoseHalf(dates, amounts, miles);
                    tempDays = tempDays / 2;
                    mFrequencyDays = (int)Math.round(tempDays);
                    break;
                case ".5":
                    tempDays = Utility.calculateDaysToLoseHalf(dates, amounts, miles);
                    mFrequencyDays = (int)Math.round(tempDays);
                    break;
                case".75":
                    tempDays = Utility.calculateDaysToLoseHalf(dates, amounts, miles);
                    tempDays = tempDays + (tempDays / 2);
                    mFrequencyDays = (int)Math.round(tempDays);
                    break;
                case "1":
                    tempDays = Utility.calculateDaysToLoseHalf(dates, amounts, miles);
                    tempDays = tempDays * 2;
                    mFrequencyDays = (int)Math.round(tempDays);
                    break;
            }

            mFrequency = mFrequencyDays;

            //If its not the first time a auto notification has been built set then calculate when the fire date is
            //based on when the last time it was fired. If it's the first time no changes to mFrequency will be made
            if(settings.getBoolean("first_oil_notification", true)){
                notificationStartTime.add(Calendar.DAY_OF_YEAR, mFrequency);

                //It is no longer the first time the auto notifcation was set so change the value
                settings.edit().putBoolean("first_oil_notification", false).commit();
                //No changes to mFrequency will be made because it'll use that
            } else {
                //Calculate when the last time the auto notification went off.
                Calendar lastNotification = Calendar.getInstance();
                lastNotification.setTimeInMillis(settings.getLong("last_oil_notification", lastNotification.getTimeInMillis()));
                lastNotification.set(Calendar.HOUR_OF_DAY, 0);
                lastNotification.set(Calendar.MINUTE, 0);
                lastNotification.set(Calendar.SECOND, 0);
                lastNotification.set(Calendar.MILLISECOND, 0);

                Calendar todaysDate = setTodaysDate();

                int daysSinceLastNotification = (int)(todaysDate.getTimeInMillis() - lastNotification.getTimeInMillis()) / MILLISECS_PER_DAY;

                if(daysSinceLastNotification <= 0){
                    //Do nothing, reset the notification with the statistics suggested calculated frequency
                }
                else{
                    int temp = mFrequency - daysSinceLastNotification;

                    if (temp > 0){
                        mFrequency = temp;;
                    }
                }
                notificationStartTime.add(Calendar.DAY_OF_YEAR, mFrequency);
            }

            Intent intent = new Intent(context, ReminderReceiver.class);
                intent.putExtra("notification_number", Integer.toString(OIL_REMINDER));
                intent.putExtra("quantity", mOilAmount);

            PendingIntent pi = PendingIntent.getBroadcast(context, OIL_REMINDER, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, notificationStartTime.getTimeInMillis(), mFrequencyDays * MILLISECS_PER_DAY, pi);

        }
        mDataSource.close();

    }

    public static void cancelRepeatingOilNotification(Context context){
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("notification_number", Integer.toString(OIL_REMINDER));
        PendingIntent pi = PendingIntent.getBroadcast(context, OIL_REMINDER, intent, 0);

        //If PendingIntent pi != null then you can create an alarmmanager and cancel the pi
        if (pi != null) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);
        }
    }


    public static void setRepeatingCoolantNotification(Context context){
        DataSource mDataSource = new DataSource(context);
        mDataSource.open();

        int databaseLength = mDataSource.getLength(LogsContract.CoolantTable.TAG);

        //Only set a coolant auto notify if coolant logs have two or more data entries so we can calculate when
        //to send the notification
        if (databaseLength >= 2) {
            String[] dates = mDataSource.getAllDates(LogsContract.CoolantTable.TAG);
            String[] amounts = mDataSource.getAllAmounts(LogsContract.CoolantTable.TAG);
            String[] miles = mDataSource.getAllMiles(LogsContract.CoolantTable.TAG);

            //Get the preferences
            settings = PreferenceManager.getDefaultSharedPreferences(context);

            //Get the time of day user wants notification to happen.
            mTimeString = settings.getString("auto_notify_coolant_time", "12:0");
            mHour = getHour(mTimeString);
            mMinute = getMinute(mTimeString);

            setNotificationStartTime(mHour, mMinute);

            //Figure out algorithm to calculate how many days the application thinks you'll run out of coolant
            //based on the current logged coolant logs.
            mCoolantAmount = settings.getString("coolant_amount", ".5");

            double tempDays = 1;

            switch (mCoolantAmount){
                case ".25":
                    tempDays = Utility.calculateDaysToLoseHalf(dates, amounts, miles);
                    tempDays = tempDays / 2;
                    mFrequencyDays = (int)Math.round(tempDays);
                    break;
                case ".5":
                    tempDays = Utility.calculateDaysToLoseHalf(dates, amounts, miles);
                    mFrequencyDays = (int)Math.round(tempDays);
                    break;
                case".75":
                    tempDays = Utility.calculateDaysToLoseHalf(dates, amounts, miles);
                    tempDays = tempDays + (tempDays / 2);
                    mFrequencyDays = (int)Math.round(tempDays);
                    break;
                case "1":
                    tempDays = Utility.calculateDaysToLoseHalf(dates, amounts, miles);
                    tempDays = tempDays * 2;
                    mFrequencyDays = (int)Math.round(tempDays);
                    break;
            }

            mFrequency = mFrequencyDays;

            //If its not the first time a auto notification has been built set then calculate when the fire date is
            //based on when the last time it was fired. If it's the first time no changes to mFrequency will be made
            if(settings.getBoolean("first_coolant_notification", true)){
                notificationStartTime.add(Calendar.DAY_OF_YEAR, mFrequency);

                //It is no longer the first time the auto notifcation was set so change the value
                settings.edit().putBoolean("first_coolant_notification", false).commit();
                //No changes to mFrequency will be made because it'll use that
            } else {
                //Calculate when the last time the auto notification went off.
                Calendar lastNotification = Calendar.getInstance();
                lastNotification.setTimeInMillis(settings.getLong("last_coolant_notification", lastNotification.getTimeInMillis()));
                lastNotification.set(Calendar.HOUR_OF_DAY, 0);
                lastNotification.set(Calendar.MINUTE, 0);
                lastNotification.set(Calendar.SECOND, 0);
                lastNotification.set(Calendar.MILLISECOND, 0);

                Calendar todaysDate = setTodaysDate();

                int daysSinceLastNotification = (int)(todaysDate.getTimeInMillis() - lastNotification.getTimeInMillis()) / MILLISECS_PER_DAY;

                if(daysSinceLastNotification <= 0){
                    //Do nothing, reset the notification with the statistics suggested calculated frequency
                }
                else{
                    int temp = mFrequency - daysSinceLastNotification;

                    if (temp > 0){
                        mFrequency = temp;;
                    }
                }
                notificationStartTime.add(Calendar.DAY_OF_YEAR, mFrequency);
            }

            Intent intent = new Intent(context, ReminderReceiver.class);
            intent.putExtra("notification_number", Integer.toString(COOLANT_REMINDER));
            intent.putExtra("quantity", mCoolantAmount);

            PendingIntent pi = PendingIntent.getBroadcast(context, COOLANT_REMINDER, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, notificationStartTime.getTimeInMillis(), mFrequencyDays * MILLISECS_PER_DAY, pi);
        }
        mDataSource.close();

    }

    public static void cancelRepeatingCoolantNotification(Context context){
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("notification_number", Integer.toString(COOLANT_REMINDER));
        PendingIntent pi = PendingIntent.getBroadcast(context, COOLANT_REMINDER, intent, 0);

        //If PendingIntent pi != null then you can create an alarmmanager and cancel the pi
        if (pi != null) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);
        }
    }


    //Helper method to set the notification start time
    private static void setNotificationStartTime(int hour, int minute){
        notificationStartTime = Calendar.getInstance();
        notificationStartTime.set(Calendar.HOUR_OF_DAY, mHour);
        notificationStartTime.set(Calendar.MINUTE, mMinute);
        notificationStartTime.set(Calendar.SECOND, 0);
        notificationStartTime.set(Calendar.MILLISECOND, 0);
    }


    //Helper method to set the last notification calender time
    private static Calendar setTodaysDate(){
        Calendar todaysDate = Calendar.getInstance();
        todaysDate.setTimeInMillis(System.currentTimeMillis());
        todaysDate.set(Calendar.HOUR_OF_DAY, 0);
        todaysDate.set(Calendar.MINUTE, 0);
        todaysDate.set(Calendar.SECOND, 0);
        todaysDate.set(Calendar.MILLISECOND, 0);

        return todaysDate;
    }


    //Helper method to get the hour the user wants to display the notification
    private static int getHour(String time) {
        String[] pieces=time.split(":");
        return(Integer.parseInt(pieces[0]));
    }

    //Helper method to get the minute the user wants to display the notification
    private static int getMinute(String time) {
        String[] pieces=time.split(":");
        return(Integer.parseInt(pieces[1]));
    }


}
