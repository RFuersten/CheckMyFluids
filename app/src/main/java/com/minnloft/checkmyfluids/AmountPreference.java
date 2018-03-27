package com.minnloft.checkmyfluids;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.minnloft.checkmyfluids.notifications.ReminderManager;

/**
 * Created by Ryan on 10/23/2015.
 */
public class AmountPreference extends DialogPreference{
    public static final String DEFAULT_VALUE = ".5";
    public static final String DEFAULT_VALUE_STRING = "1/2";

    private ListView list;
    private ArrayAdapter<String> adapter;
    private String[] text_amounts;
    private int selectedPosition;
    private String value;
    private String stringValue;

    public AmountPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPositiveButtonText("SET");
        setNegativeButtonText("CANCEL");
        setTitle("When to notify");
    }


    @Override
    protected View onCreateDialogView() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.amount_picker, null);

        final TextView title = (TextView) view.findViewById(R.id.popup_title);
        title.setText("Notification Amount");

        list = (ListView) view.findViewById(R.id.amount_list_picker);

        text_amounts = getContext().getResources().getStringArray(R.array.quarts);

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, text_amounts);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedPosition = position;

                for (int i = 0; i < list.getChildCount(); i++) {
                    View listItem = list.getChildAt(i);
                    listItem.setBackgroundColor(Color.TRANSPARENT);
                }

                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.NotifyAmountSelector));

            }

        });

        //Make a runnable to make the background of the current value highlighted
        list.post(new Runnable() {
            @Override
            public void run() {
                //Highlight the current value listview item
                int i = 1;

                switch (getPersistedString(DEFAULT_VALUE)) {
                    case ".25":
                        i = 0;
                        selectedPosition = 0;
                        break;
                    case ".5":
                        i = 1;
                        selectedPosition = 1;
                        break;
                    case ".75":
                        i = 2;
                        selectedPosition = 2;
                        break;
                    case "1":
                        i = 3;
                        selectedPosition = 3;
                        break;
                }

                View defaultview = list.getChildAt(i);

                defaultview.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.NotifyAmountSelector));
            }
        });

        return view;
    }



    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

            switch (selectedPosition) {
                case 0:
                    value = ".25";
                    stringValue = " 1/4";
                    break;
                case 1:
                    value = ".5";
                    stringValue = " 1/2";
                    break;
                case 2:
                    value = ".75";
                    stringValue = " 3/4";
                    break;
                case 3:
                    value = "1";
                    stringValue = " 1";
                    break;
            }

            persistString(value);

            setSummary(stringValue + " quart low");

            //Create a new alarm with updated information
            String key = getKey();

            switch(key) {
                case "oil_amount":
                    //Update the check notification with new time
                    ReminderManager.setRepeatingOilNotification(getContext());
                    Toast.makeText(getContext(), "Oil notification updated",
                            Toast.LENGTH_LONG).show();
                    break;
                case "coolant_amount":
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
        if (restorePersistedValue) {
            //Restore existing state
            value = getPersistedString(DEFAULT_VALUE);

            switch (value){
                case ".25":
                    stringValue = " 1/4";
                    break;
                case ".5":
                    stringValue = " 1/2";
                    break;
                case ".75":
                    stringValue = " 3/4";
                    break;
                case "1":
                    stringValue = " 1";
                    break;
            }

            setSummary(stringValue + " quart low");
        } else {
            //Set default state from the XML attribute
            value = (String) defaultValue;

            persistString(value);
            setSummary(DEFAULT_VALUE_STRING + " quart low");
        }
    }


    public String getValue() {
        return this.value;
    }


}
