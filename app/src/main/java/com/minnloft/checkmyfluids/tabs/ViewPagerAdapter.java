package com.minnloft.checkmyfluids.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; //This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; //Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    String tag;

    int Images[];

    public final static String LOG_TAG = "LOG_TAG";
    public final static String STATS_TAG = "STATS_TAG";

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mImages[], int mNumbOfTabsumb, String tag) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.tag = tag;
        this.Images = mImages;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        switch (tag) {
            case ViewPagerAdapter.LOG_TAG:
                if (position == 0) // if the position is 0 we are returning the First tab
                {
                    LogOilTabFragment logOilTabFragment = new LogOilTabFragment();
                    return logOilTabFragment;
                } else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
                {
                    LogCoolantTabFragment logCoolantTabFragment = new LogCoolantTabFragment();
                    return logCoolantTabFragment;
                }
            case ViewPagerAdapter.STATS_TAG:
                if (position == 0) // if the position is 0 we are returning the First tab
                {
                    StatsOilTabFragment statsOilTabFragment = new StatsOilTabFragment();
                    return statsOilTabFragment;
                } else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
                {
                    StatsCoolantTabFragment statsCoolantTabFragment = new StatsCoolantTabFragment();
                    return statsCoolantTabFragment;
                }
            default:
                return null; // Should never get here.
        }

    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }


    public int getImageId(int position) {
        return Images[position];
    }


}