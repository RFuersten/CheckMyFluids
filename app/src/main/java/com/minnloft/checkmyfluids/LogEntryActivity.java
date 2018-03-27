package com.minnloft.checkmyfluids;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.minnloft.checkmyfluids.data.DataSource;
import com.minnloft.checkmyfluids.data.Log;
import com.minnloft.checkmyfluids.data.LogsContract.OilTable;
import com.minnloft.checkmyfluids.data.LogsContract.CoolantTable;
import com.minnloft.checkmyfluids.notifications.ReminderManager;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.util.Calendar;

public class LogEntryActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private DataSource mDataSource;
    private SharedPreferences settings;
    private String logMode;
    private String logFluidType;
    private Log log;

    private EditText dateInput;
    private EditText amountInput;
    private TextInputLayout amountTextInputLayout;
    private EditText milesInput;
    private TextInputLayout milesTextInputLayout;
    private Button logEntryButton;

    private String autoNotifyFluidTypeKey;
    private String fluidTypeString;

    private DatePickerDialog datepickerdialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_entry);

        Intent intent = getIntent();
        logMode = intent.getStringExtra(LogFluidsActivity.EXTRA_LOG_MODE); //Used to know if we are adding a new record to the database or updating an existing one
        logFluidType = intent.getStringExtra(LogFluidsActivity.EXTRA_LOG_FLUID_TYPE); //Used to know if this is an Oil log or Coolant log
        log = intent.getParcelableExtra(LogFluidsActivity.EXTRA_LOG_OBJECT); //Get the log record that's being updated if logMode is log_mode_edit

        mDataSource = new DataSource(this);
        mDataSource.open();

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        //Update the title on the actionbar to represent which fluid type we're dealing with
        if(logFluidType != null) {
            switch(logFluidType){
                case OilTable.TAG:
                    autoNotifyFluidTypeKey = "checkbox_oil_auto_notify";
                    getSupportActionBar().setTitle("Oil Log Entry");
                    fluidTypeString = "Oil";
                    break;
                case CoolantTable.TAG:
                    autoNotifyFluidTypeKey = "checkbox_coolant_auto_notify";
                    getSupportActionBar().setTitle("Coolant Log Entry");
                    fluidTypeString = "Coolant";
                    break;
            }
        }

        initializeViews();

        //Update the logEntryButton text with the appropriate text depending on if we are
        //adding a new log entry or updating an existing one
        switch(logMode){
            case LogFluidsActivity.LOG_MODE_ADD:
                logEntryButton.setText("Add Log");
                break;
            case LogFluidsActivity.LOG_MODE_EDIT:
                logEntryButton.setText("Update Log");
                dateInput.setText(log.getmDate());
                amountInput.setText(log.getmAmount());
                milesInput.setText(log.getmMiles());
                break;
        }
    }

    @Override
    public void onResume() {
        mDataSource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        mDataSource.close();
        super.onPause();
    }


    //Initialize all views needed for LogEntryActivity
    private void initializeViews(){
        milesTextInputLayout = (TextInputLayout) findViewById(R.id.textinputlayout_miles) ;
        milesInput = (EditText) findViewById(R.id.edittext_add_miles);

        amountTextInputLayout = (TextInputLayout) findViewById(R.id.textinputlayout_amount);
        amountInput = (EditText) findViewById(R.id.edittext_add_amount);

        dateInput = (EditText) findViewById(R.id.edittext_add_date);
        dateInput.setOnClickListener(this);

        logEntryButton = (Button) findViewById(R.id.button_log_entry);
        logEntryButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu when we are in log_mode_edit; this adds items to the action bar if it is present.
        switch(logMode){
            case LogFluidsActivity.LOG_MODE_EDIT:
                getMenuInflater().inflate(R.menu.menu_delete, menu);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //If click delete menu option, delete the current log
        if (id == R.id.action_delete) {
            deleteLog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //dateInput view is clicked
            case R.id.edittext_add_date:
                //Get an instance of current time/date
                Calendar now = Calendar.getInstance();
                datepickerdialog = DatePickerDialog.newInstance(
                        LogEntryActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datepickerdialog.setVersion(DatePickerDialog.Version.VERSION_2);
                datepickerdialog.show(getFragmentManager(), "Datepickerdialog");
                break;
            //logEntryButton view is clicked
            case R.id.button_log_entry:
                switch(logMode){
                    case LogFluidsActivity.LOG_MODE_ADD:
                        addLog();
                        break;
                    case LogFluidsActivity.LOG_MODE_EDIT:
                        updateLog();
                        break;
                }
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(monthOfYear + 1); //Need to add one to the monthOfYear since Jan is index 0, Dec is index 11
        stringBuilder.append("/");
        stringBuilder.append(dayOfMonth);
        stringBuilder.append("/");
        stringBuilder.append(year);

        //Set the dateInput text to the date the user selected
        dateInput.setText(stringBuilder.toString());
    }


    //Add the log entry into the database if all user input is confirmed valid
    private void addLog(){
        String date = dateInput.getText().toString();
        String amount = amountInput.getText().toString();
        String miles = milesInput.getText().toString();

        String error_message = Validate.validateData(date, amount, miles);
        //If error_message has no length then there was no error. Add the log.
        if (error_message.length() == 0) {
            mDataSource.insert(date, amount, miles, logFluidType);

            checkAndSetRepeatingNotification();

            Toast.makeText(this, fluidTypeString + " log added", Toast.LENGTH_LONG).show();
            finish(); //We are done with this activity, call finish() and let the OS clean up the activity and return to the previous one
        } else {
            Toast.makeText(this,
                    error_message, Toast.LENGTH_SHORT).show();
        }

    }

    //Update the log entry being edited if all user input is confirmed valid
    private void updateLog(){
        String date = dateInput.getText().toString();
        String amount = amountInput.getText().toString();
        String miles = milesInput.getText().toString();

        String error_message = Validate.validateData(date, amount, miles);
        if (error_message.length() == 0) {
            mDataSource.edit(log.getmId(), date, amount, miles, logFluidType);

            checkAndSetRepeatingNotification();

            Toast.makeText(this, fluidTypeString + " log updated", Toast.LENGTH_LONG).show();
            finish(); //We are done with this activity, call finish() and let the OS clean up the activity and return to the previous one
        } else {
            Toast.makeText(this,
                        error_message, Toast.LENGTH_SHORT).show();
        }

    }

    //Method to build and display a dialog to prompt the user if they want to delete the selected log
    private void deleteLog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.delete_log, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setView(view);

        final TextView title = (TextView) view.findViewById(R.id.popup_title);
        title.setText("Delete Oil Log?");

        alert.setPositiveButton("DELETE LOG", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mDataSource.delete(log, logFluidType);

                //If the datasource now has less than two oil logs we should cancel the auto notification
                if (mDataSource.getLength(logFluidType) <= 1) {
                    ReminderManager.cancelRepeatingOilNotification(getApplicationContext());
                } else {
                    //Recalculate when the new alarm should be due to the new data.
                    ReminderManager.setRepeatingOilNotification(getApplicationContext());
                }

                Toast.makeText(getApplicationContext(), fluidTypeString + " log deleted", Toast.LENGTH_LONG).show();
                finish();

            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    //Checks to see if we need to set the repeating notification for the appropriate logFluidType
    //If we do then it will set the notification, else does nothing.
    private void checkAndSetRepeatingNotification(){
        //If the datasource now has two or more logs for the logFluidType and repeating notifications are enabled
        //we can set/reset the auto low notification
        if (mDataSource.getLength(logFluidType) >= 2 &&
                settings.getBoolean(autoNotifyFluidTypeKey, true)) {
            switch (logFluidType) {
                case OilTable.TAG:
                    ReminderManager.setRepeatingOilNotification(this);
                    break;
                case CoolantTable.TAG:
                    ReminderManager.setRepeatingCoolantNotification(this);
                    break;
            }

        }
    }

}
