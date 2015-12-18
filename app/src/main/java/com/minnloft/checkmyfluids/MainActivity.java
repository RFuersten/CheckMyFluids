package com.minnloft.checkmyfluids;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.minnloft.checkmyfluids.notifications.ReminderManager;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        // First time running app? Set a start time to start calculating when to send notifications
        if (settings.getBoolean("app_first_time_started", true)) {
            //Task to do if first time started
            ReminderManager.setRepeatingReminderNotification(this);

            settings.edit().putBoolean("first_oil_notification", true).apply();
            settings.edit().putBoolean("first_coolant_notification", true).apply();

            //Change the preference to show that the app has started at least once
            settings.edit().putBoolean("app_first_time_started", false).apply();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
