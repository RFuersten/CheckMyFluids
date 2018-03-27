package com.minnloft.checkmyfluids;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.minnloft.checkmyfluids.data.LogsContract.CoolantTable;
import com.minnloft.checkmyfluids.data.LogsContract.OilTable;
import com.minnloft.checkmyfluids.tabs.SlidingTabLayout;
import com.minnloft.checkmyfluids.tabs.ViewPagerAdapter;

public class LogFluidsActivity extends AppCompatActivity {

    public static final String TAG = "LFAT";

    public static final String EXTRA_LOG_MODE = "com.minnloft.checkmyfluids.LOG_MODE";
    public static final String EXTRA_LOG_FLUID_TYPE = "com.minnloft.checkmyfluids.LOG_FLUID_TYPE";
    public static final String EXTRA_LOG_OBJECT = "com.minnloft.checkmyfluids.LOG_OBJECT";

    //Flags used when a user navigates to the LogEntryActivity,
    //LOG_MODE_ADD is added to the intent when a user clicks on the floating action button to add a new entry
    //LOG_MODE_EDIT is added to the intent when a user clicks on a fluid log entry in the listview to edit the entry
    public static final String LOG_MODE_ADD = "log_mode_add";
    public static final String LOG_MODE_EDIT = "log_mode_edit";

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Oil","Coolant"};
    int Numboftabs =2;

    int Images[]= {R.drawable.log_oil, R.drawable.log_coolant};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_fluids);

        adapter =  new ViewPagerAdapter(getSupportFragmentManager(), Titles, Images, Numboftabs, ViewPagerAdapter.LOG_TAG);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.log_pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.log_tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        //Set a custom tab layout view
        tabs.setCustomTabView(R.layout.tab_layout, R.id.textview_tab, R.id.imageview_tab);

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           // getSupportActionBar().setElevation(0f);
        }

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.log_fluids_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPageInt = pager.getCurrentItem();

                String fluidType;

                switch(selectedPageInt){
                    //First tab is Oil tab
                    case 0:
                        fluidType = OilTable.TAG;
                        break;
                    //Second tab is Coolant tab
                    case 1:
                        fluidType = CoolantTable.TAG;
                        break;
                    default:
                        fluidType = null;
                        break;
                }

                Intent intent = new Intent(getApplicationContext(), LogEntryActivity.class);
                intent.putExtra(EXTRA_LOG_MODE, LOG_MODE_ADD);
                intent.putExtra(EXTRA_LOG_FLUID_TYPE, fluidType);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
