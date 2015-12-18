package com.minnloft.checkmyfluids;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.minnloft.checkmyfluids.notifications.ReminderManager;


public class FrequencyPreference extends DialogPreference {
    //Allowed range
    public static final int MAX_VALUE = 31;
    public static final int MIN_VALUE = 1;
    public static final int DEFAULT_VALUE = 7;

    //Enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = false;

    NumberPicker picker;
    private int value;

    public FrequencyPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPositiveButtonText("SET");
        setNegativeButtonText("CANCEL");
        setTitle("Set frequency");
    }


    @Override
    protected View onCreateDialogView() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.frequency_picker, null);

        final TextView title = (TextView) view.findViewById(R.id.popup_title);
        title.setText("Notification Frequency");

        picker = (NumberPicker) view.findViewById(R.id.frequency_number_picker);

        return view;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setMinValue(MIN_VALUE);
        picker.setMaxValue(MAX_VALUE);
        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setValue(value);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            value = picker.getValue();
            persistInt(value);
            setSummary("Every " + value + " days");

            ReminderManager.setRepeatingReminderNotification(getContext());
            Toast.makeText(getContext(), "Check fluids notification updated",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            value = getPersistedInt(DEFAULT_VALUE);
            setSummary("Every " + getValue() + " days");
        } else {
            // Set default state from the XML attribute
            value = (Integer) defaultValue;
            persistInt(value);
            setSummary("Every " + DEFAULT_VALUE + " days");
        }
    }


    public int getValue() {
        return this.value;
    }

}