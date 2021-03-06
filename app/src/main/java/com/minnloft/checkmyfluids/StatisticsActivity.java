package com.minnloft.checkmyfluids;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.minnloft.checkmyfluids.tabs.SlidingTabLayout;
import com.minnloft.checkmyfluids.tabs.ViewPagerAdapter;

public class StatisticsActivity extends AppCompatActivity {

    public static final String TAG = "SAT";

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Oil","Coolant"};
    int Numboftabs = 2;

    int Images[]= {R.drawable.stats_oil, R.drawable.stats_coolant};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        adapter =  new ViewPagerAdapter(getSupportFragmentManager(), Titles, Images, Numboftabs, ViewPagerAdapter.STATS_TAG);

        //Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.stats_pager);
        pager.setAdapter(adapter);

        //Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.stats_tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        //Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        //Set a custom tab layout view
        tabs.setCustomTabView(R.layout.tab_layout, R.id.textview_tab, R.id.imageview_tab);

        //Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
