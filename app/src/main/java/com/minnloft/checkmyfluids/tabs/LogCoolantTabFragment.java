package com.minnloft.checkmyfluids.tabs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.minnloft.checkmyfluids.LogEntryActivity;
import com.minnloft.checkmyfluids.LogFluidsActivity;
import com.minnloft.checkmyfluids.R;
import com.minnloft.checkmyfluids.data.DataSource;
import com.minnloft.checkmyfluids.data.Log;
import com.minnloft.checkmyfluids.data.LogAdapter;
import com.minnloft.checkmyfluids.data.LogsContract.CoolantTable;
import com.minnloft.checkmyfluids.notifications.ReminderManager;

import java.util.ArrayList;


public class LogCoolantTabFragment extends Fragment{
    private DataSource mDataSource;

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
        view = inflater.inflate(R.layout.log_coolant_tab, container, false);

        mDataSource = new DataSource(getActivity());
        mDataSource.open();

        mContext = getActivity();

        settings = PreferenceManager.getDefaultSharedPreferences(mContext);

        no_logs_text = (TextView) view.findViewById(R.id.textview_no_logs);
        no_logs = (LinearLayout) view.findViewById(R.id.no_logs_view);
        divider = (ImageView) view.findViewById(R.id.divider);

        mListView = (ListView) view.findViewById(R.id.coolant_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the position number of the list item clicked
                mPosition = position;
                Log log = (Log) mAdapter.getItem(mPosition);

                Intent intent = new Intent(getActivity(), LogEntryActivity.class);
                intent.putExtra(LogFluidsActivity.EXTRA_LOG_MODE, LogFluidsActivity.LOG_MODE_EDIT);
                intent.putExtra(LogFluidsActivity.EXTRA_LOG_FLUID_TYPE, CoolantTable.TAG);
                intent.putExtra(LogFluidsActivity.EXTRA_LOG_OBJECT, log);
                startActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Get the position number of the list item long clicked
                mPosition = position;
                deleteLog(); //Show the confirm delete log dialog
                return true;
            }
        });

        //Construct the data source
        arrayOfLogs = new ArrayList<Log>();
        mAdapter = new LogAdapter(getActivity(), arrayOfLogs);
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        mDataSource.open();
        getCoolantData();
        super.onResume();
    }

    @Override
    public void onPause() {
        mDataSource.close();
        super.onPause();
    }

    //Gets the coolant logs data from the database and then populates the listview adapter with the data.
    private void getCoolantData(){
        //Get the data
        mAdapter.clear();
        arrayOfLogs = mDataSource.getAllLogs(CoolantTable.TAG);
        mAdapter.addAll(arrayOfLogs);
        mAdapter.notifyDataSetChanged();

        //If there are no logs, show the no logs text
        //Else hide the no logs text and display the logs
        if (mDataSource.getLength(CoolantTable.TAG) < 1){
            no_logs.setVisibility(View.VISIBLE);
            no_logs_text.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            no_logs.setVisibility(View.GONE);
            no_logs_text.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
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
                if (mDataSource.getLength(CoolantTable.TAG) <= 1) {
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

}