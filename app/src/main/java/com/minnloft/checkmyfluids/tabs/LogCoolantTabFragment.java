package com.minnloft.checkmyfluids.tabs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.minnloft.checkmyfluids.R;
import com.minnloft.checkmyfluids.data.DataSource;
import com.minnloft.checkmyfluids.data.Log;
import com.minnloft.checkmyfluids.data.LogAdapter;
import com.minnloft.checkmyfluids.data.LogsContract;
import com.minnloft.checkmyfluids.data.LogsContract.CoolantTable;
import com.minnloft.checkmyfluids.notifications.ReminderManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogCoolantTabFragment extends Fragment  implements View.OnClickListener{
    private DataSource mDataSource;

    private LinearLayout mAddButton;
    private LinearLayout mEditButton;
    private LinearLayout mDeleteButton;

    private LinearLayout no_logs;
    private TextView no_logs_text;
    private ImageView divider;

    private Context mContext;
    private LogAdapter mAdapter;
    private ListView mListView;
    private Integer mPosition = ListView.INVALID_POSITION;
    private ArrayList<Log> arrayOfLogs;

    private SharedPreferences settings;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;

        mDataSource = new DataSource(getActivity());
        mDataSource.open();

        view = inflater.inflate(R.layout.log_coolant_tab, container, false);

        mContext = getActivity();

        settings = PreferenceManager.getDefaultSharedPreferences(mContext);

        no_logs_text = (TextView) view.findViewById(R.id.textview_no_logs);
        no_logs = (LinearLayout) view.findViewById(R.id.no_logs_view);
        divider = (ImageView) view.findViewById(R.id.divider);

        mAddButton = (LinearLayout) view.findViewById(R.id.button_coolant_add);
        mEditButton = (LinearLayout) view.findViewById(R.id.button_coolant_edit);
        mDeleteButton = (LinearLayout) view.findViewById(R.id.button_coolant_delete);

        mAddButton.setOnClickListener(this);
        mEditButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);

        mListView = (ListView) view.findViewById(R.id.coolant_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the position number of the list item clicked
                mPosition = position;
            }
        });

        // Construct the data source
        arrayOfLogs = new ArrayList<Log>();
        arrayOfLogs = mDataSource.getAllLogs(CoolantTable.TAG);
        mAdapter = new LogAdapter(getActivity(), arrayOfLogs);
        mListView.setAdapter(mAdapter);

        //If there are no logs show the no logs image
        if (mDataSource.getLength(CoolantTable.TAG) < 1){
            no_logs.setVisibility(View.VISIBLE);
            no_logs_text.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_coolant_add:
                addLog();
                break;
            case R.id.button_coolant_edit:
                if(mPosition >= 0 && mPosition < mAdapter.getCount()){
                    editLog();
                }
                break;
            case R.id.button_coolant_delete:
                if(mPosition >= 0 && mPosition < mAdapter.getCount()){
                    deleteLog();
                }
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


    //Method to build and display a dialog to prompt the user if they want to add a log to the database.
    private void addLog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.add_edit_log, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setView(view);

        final TextView title = (TextView) view.findViewById(R.id.popup_title);
        title.setText("Add Coolant Log");
        final EditText dateInput = (EditText) view.findViewById(R.id.edittext_add_date);
        final ImageButton todayButton = (ImageButton) view.findViewById(R.id.button_today);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String strDate = sdf.format(c.getTime());
                dateInput.setText(strDate);
            }
        });
        final EditText amountInput = (EditText) view.findViewById(R.id.edittext_add_amount);
        final EditText milesInput = (EditText) view.findViewById(R.id.edittext_add_miles);

        alert.setPositiveButton("ADD LOG", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String date = dateInput.getText().toString();
                String amount = amountInput.getText().toString();
                String miles = milesInput.getText().toString();

                int error_count = 0;
                String error_message = "";

                //Error check date field
                if (!verifyValidDate(date)){
                    error_count = 1;
                    error_message = error_message.concat("Date");
                }
                //Error check amount field
                if (!verifyValidAmount(amount)){
                    error_count = error_count + 1;
                    if (error_count == 2)
                        error_message = error_message.concat(" and quarts filled");
                    else
                        error_message = error_message.concat("Quarts filled");
                }
                //Error check miles field
                if (!verifyValidMiles(miles)){
                    error_count = error_count + 1;
                    if (error_count == 2 || error_count == 3)
                        error_message = error_message.concat(" and mileage");
                    else
                        error_message = error_message.concat("Mileage");
                }
                if (error_count == 0){
                    Log log = new Log();
                    log.setmDate(date);
                    log.setmAmount(amount);
                    log.setmMiles(miles);

                    mDataSource.insert(date, amount, miles, CoolantTable.TAG);

                    log.setmId(mDataSource.getLastLogID(CoolantTable.TAG));

                    mAdapter.add(log);
                    mAdapter.notifyDataSetChanged();
                    mListView.setItemChecked(mAdapter.getCount() - 1, true);
                    mPosition = mAdapter.getCount()-1;
                    mListView.smoothScrollToPosition(mPosition);

                    //If the log successfully entered the database we must now hide the no logs image
                    //and set the listView back to viewable
                    if(mDataSource.getLength(CoolantTable.TAG) >= 1){
                        no_logs.setVisibility(View.GONE);
                        no_logs_text.setVisibility(View.GONE);
                        divider.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                    }


                    //If the datasource now has two or more coolant logs and coolant notifications are on
                    //we can set the auto coolant low notification
                    if(mDataSource.getLength(LogsContract.CoolantTable.TAG) >= 2 &&
                            settings.getBoolean("checkbox_coolant_auto_notify", true)){
                        ReminderManager.setRepeatingCoolantNotification(mContext);
                    }

                }

                Toast toast;
                switch(error_count){
                    case 1:
                        error_message = error_message.concat(" value is invalid.");
                        toast = Toast.makeText(mContext,
                                error_message, Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    case 2:
                        error_message = error_message.concat(" values are invalid.");
                        toast = Toast.makeText(mContext,
                                error_message, Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    case 3:
                        error_message = "All values are invalid.";
                        toast = Toast.makeText(mContext,
                                error_message, Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                }

            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();

    }

    //Method to build and display a dialog to prompt the user if they want to edit the selected log.
    //If they edit the fields it will update the log in the database
    private void editLog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.add_edit_log, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setView(view);

        final TextView title = (TextView) view.findViewById(R.id.popup_title);
        title.setText("Edit Coolant Log");
        final EditText dateInput = (EditText) view.findViewById(R.id.edittext_add_date);
        final ImageButton todayButton = (ImageButton) view.findViewById(R.id.button_today);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String strDate = sdf.format(c.getTime());
                dateInput.setText(strDate);
            }
        });
        final EditText amountInput = (EditText) view.findViewById(R.id.edittext_add_amount);
        final EditText milesInput = (EditText) view.findViewById(R.id.edittext_add_miles);

        Log log = null;
        //Pre fill in the edit text views with the data already in the database
        if (mAdapter.getCount() > 0 && mPosition >= 0) {
            log = (Log) mAdapter.getItem(mPosition);
            dateInput.setText(log.getmDate());
            amountInput.setText(log.getmAmount());
            milesInput.setText(log.getmMiles());
        }

        alert.setPositiveButton("EDIT LOG", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String date = dateInput.getText().toString();
                String amount = amountInput.getText().toString();
                String miles = milesInput.getText().toString();

                int error_count = 0;
                String error_message = "";

                //Error check date field
                if (!verifyValidDate(date)){
                    error_count = 1;
                    error_message = error_message.concat("Date");
                }
                //Error check amount field
                if (!verifyValidAmount(amount)){
                    error_count = error_count + 1;
                    if (error_count == 2)
                        error_message = error_message.concat(" and quarts filled");
                    else
                        error_message = error_message.concat("Quarts filled");
                }
                //Error check miles field
                if (!verifyValidMiles(miles)){
                    error_count = error_count + 1;
                    if (error_count == 2 || error_count == 3)
                        error_message = error_message.concat(" and mileage");
                    else
                        error_message = error_message.concat("Mileage");
                }
                if (error_count == 0){
                    Log log = (Log) mAdapter.getItem(mPosition);

                    mDataSource.edit(log.getmId(), date, amount, miles, CoolantTable.TAG);
                    mAdapter.clear();
                    arrayOfLogs = new ArrayList<Log>();
                    arrayOfLogs = mDataSource.getAllLogs(CoolantTable.TAG);
                    mAdapter.addAll(arrayOfLogs);
                    mAdapter.notifyDataSetChanged();
                }


                //If the datasource now has two or more coolant logs, coolant notifications are enabled
                // and an edit has been made we should update and reset the auto coolant low notification
                if(mDataSource.getLength(LogsContract.CoolantTable.TAG) >= 2 &&
                        settings.getBoolean("checkbox_coolant_auto_notify", true)){
                    ReminderManager.setRepeatingCoolantNotification(mContext);
                }


                Toast toast;
                switch(error_count){
                    case 1:
                        error_message = error_message.concat(" value is invalid.");
                        toast = Toast.makeText(mContext,
                                error_message, Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    case 2:
                        error_message = error_message.concat(" values are invalid.");
                        toast = Toast.makeText(mContext,
                                error_message, Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    case 3:
                        error_message = "All values are invalid.";
                        toast = Toast.makeText(mContext,
                                error_message, Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                }

            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    //Method to build and display a dialog to prompt the user if they want to delete the selected log
    private void deleteLog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.delete_log, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setView(view);

        final TextView title = (TextView) view.findViewById(R.id.popup_title);
        title.setText("Delete Coolant Log?");

        alert.setPositiveButton("DELETE LOG", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log log = null;

                if (mAdapter.getCount() > 0) {
                    log = (Log) mAdapter.getItem(mPosition);

                    mDataSource.delete(log, CoolantTable.TAG);
                    mAdapter.remove(log);
                    mAdapter.notifyDataSetChanged();

                    //Show the no logs text again
                    if(mDataSource.getLength(CoolantTable.TAG) < 1){
                        no_logs.setVisibility(View.VISIBLE);
                        no_logs_text.setVisibility(View.VISIBLE);
                        divider.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    }

                }

                //If the datasource now has less than two coolant logs we should cancel the auto coolant notification
                if (mDataSource.getLength(LogsContract.CoolantTable.TAG) <= 1) {
                    ReminderManager.cancelRepeatingCoolantNotification(mContext);
                } else {
                    //Recalculate when the new alarm should be due to the new data.
                    ReminderManager.setRepeatingCoolantNotification(mContext);
                }


            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }


    //Helper method to verify the date entered in the dialog to add or edit a log is valid
    private boolean verifyValidDate(String date){
        Pattern pattern;
        Matcher matcher;
        boolean matches = true;

        //Regex string for a valid date input
        final String DATE_PATTERN =
                "(0?[1-9]|1[012])[/-](0?[1-9]|[12][0-9]|3[01])[/-]((19|20)\\d\\d)";

        pattern = Pattern.compile(DATE_PATTERN);
        matcher = pattern.matcher(date);

        if(matcher.matches()){
            matcher.reset();

            if(matcher.find()){
                String month = matcher.group(1);
                String day = matcher.group(2);
                int year = Integer.parseInt(matcher.group(3));

                if (day.equals("31") && (month.equals("4") || month.equals("6") || month.equals("9") ||
                        month.equals("11") || month.equals("04") ||
                        month.equals("06") || month.equals("09"))) {
                    //Only months 1, 3, 5, 7, 8, 10, 12 have 31 days
                    matches = false;
                }
                else if (month.equals("2") || month.equals("02")) {
                    //Leap year
                    if(year % 4==0){
                        if(day.equals("30") || day.equals("31"))
                            matches = false;
                    } else {
                        if(day.equals("29")||day.equals("30")||day.equals("31"))
                            matches = false;
                    }
                }
            }
            else
                matches = false;
        }
        else
            matches = false;


        if(date.length() >= 8 && matches)
            return true;
        else
            return false;
    }


    //Helper method to verify the fluid amount entered in the dialog to add or edit a log is valid
    private boolean verifyValidAmount(String amount){
        boolean parsable = true;

        try{
            Double.parseDouble(amount);
        }catch(NumberFormatException e){
            parsable = false;
        }

        if(amount.length() > 0 && parsable)
            return true;
        else
            return false;
    }


    //Helper method to verify the miles amount entered in the dialog to add or edit a log is valid
    private boolean verifyValidMiles(String miles){
        boolean parsable = true;

        try{
            Integer.parseInt(miles);
        }catch(NumberFormatException e){
            parsable = false;
        }

        if(miles.length() > 0 && parsable)
            return true;
        else
            return false;
    }
}