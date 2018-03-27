package com.minnloft.checkmyfluids;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.LinearLayout;

import com.minnloft.checkmyfluids.notifications.ReminderManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout logFluidsButton, statisticsButton, settingsButton;
    SharedPreferences settings = null;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

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

        initializeViews();
    }

    private void initializeViews(){
        logFluidsButton = (LinearLayout) findViewById(R.id.button_log_fluids);
        logFluidsButton.setOnClickListener(this);
        statisticsButton = (LinearLayout) findViewById(R.id.button_statistics);
        statisticsButton.setOnClickListener(this);
        settingsButton = (LinearLayout) findViewById(R.id.button_settings);
        settingsButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_log_fluids:
                switchActivity(LogFluidsActivity.TAG);
                break;
            case R.id.button_statistics:
                switchActivity(StatisticsActivity.TAG);
                break;
            case R.id.button_settings:
                switchActivity(SettingsActivityFragment.TAG);
                break;
        }
    }

    //Switch activity based on what button they pressed
    private void switchActivity(String tag){
        Intent intent;

        switch(tag) {
            case LogFluidsActivity.TAG:
                intent = new Intent(this, LogFluidsActivity.class);
                startActivity(intent);
                break;
            case StatisticsActivity.TAG:
                intent = new Intent(this, StatisticsActivity.class);
                startActivity(intent);
                break;
            case SettingsActivityFragment.TAG:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
