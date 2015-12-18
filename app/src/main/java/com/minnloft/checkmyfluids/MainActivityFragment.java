package com.minnloft.checkmyfluids;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class MainActivityFragment extends Fragment implements View.OnClickListener{

   private LinearLayout logFluids, statistics, settings;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        logFluids = (LinearLayout) view.findViewById(R.id.button_log_fluids);
        logFluids.setOnClickListener(this);
        statistics = (LinearLayout) view.findViewById(R.id.button_statistics);
        statistics.setOnClickListener(this);
        settings = (LinearLayout) view.findViewById(R.id.button_settings);
        settings.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_log_fluids:
                switchActivity(LogFluidsActivityFragment.TAG);
                break;
            case R.id.button_statistics:
                switchActivity(StatisticsActivityFragment.TAG);
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
            case LogFluidsActivityFragment.TAG:
                intent = new Intent(getActivity(), LogFluidsActivity.class);
                startActivity(intent);
                break;
            case StatisticsActivityFragment.TAG:
                intent = new Intent(getActivity(), StatisticsActivity.class);
                startActivity(intent);
                break;
            case SettingsActivityFragment.TAG:
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }

}
