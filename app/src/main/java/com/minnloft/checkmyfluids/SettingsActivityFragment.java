package com.minnloft.checkmyfluids;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.minnloft.checkmyfluids.notifications.ReminderManager;

/**
 * Created by Ryan on 9/24/2015.
 */
public class SettingsActivityFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener{

    public static final String TAG = "SEAT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        //For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        //updated when the preference changes.
        bindCheckboxPreference(findPreference("checkbox_check_notify"));
        bindCheckboxPreference(findPreference("checkbox_oil_auto_notify"));
        bindCheckboxPreference(findPreference("checkbox_coolant_auto_notify"));
    }


    private void bindCheckboxPreference(Preference preference){
        preference.setOnPreferenceChangeListener(this);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        if (preference instanceof CheckBoxPreference){
            //For checkbox preferences
            CheckBoxPreference checkbox = (CheckBoxPreference) preference;

            String checkboxKey = checkbox.getKey();

            //See if it is currently checked or unchecked
            Boolean checkboxChecked = checkbox.isChecked();

            switch(checkboxKey){
                case "checkbox_check_notify":
                    if(!checkboxChecked){
                        //Set alarm if it wasn't checked and now is turned on
                        Toast.makeText(getActivity(), "Check fluids notification enabled",
                                Toast.LENGTH_LONG).show();
                         ReminderManager.setRepeatingReminderNotification(getActivity());
                    } else {
                        //Cancel alarm if it was checked and now is turned off
                        Toast.makeText(getActivity(), "Check fluids notification canceled",
                                Toast.LENGTH_LONG).show();
                        ReminderManager.cancelRepeatingReminderNotification(getActivity());
                    }
                    break;
                case "checkbox_oil_auto_notify":
                    if(!checkboxChecked){
                        //Set alarm if it wasn't checked and now is turned on
                        Toast.makeText(getActivity(), "Oil notification enabled",
                                Toast.LENGTH_LONG).show();
                         ReminderManager.setRepeatingOilNotification(getActivity());
                    } else {
                        //Cancel alarm if it was checked and now is turned off
                        Toast.makeText(getActivity(), "Oil notification canceled",
                                Toast.LENGTH_LONG).show();
                         ReminderManager.cancelRepeatingOilNotification(getActivity());
                    }
                    break;
                case "checkbox_coolant_auto_notify":
                    if(!checkboxChecked){
                        //Set alarm if it wasn't checked and now is turned on
                        Toast.makeText(getActivity(), "Coolant notification enabled",
                                Toast.LENGTH_LONG).show();
                         ReminderManager.setRepeatingCoolantNotification(getActivity());
                    } else {
                        //Cancel alarm if it was checked and now is turned off
                        Toast.makeText(getActivity(), "Coolant notification canceled",
                                Toast.LENGTH_LONG).show();
                         ReminderManager.cancelRepeatingCoolantNotification(getActivity());
                    }
                    break;
            }

        } else {
            // For other preferences, set the summary to the value's simple string representation.
            String stringValue = value.toString();
            preference.setSummary(stringValue);
        }

        return true;
    }

}
