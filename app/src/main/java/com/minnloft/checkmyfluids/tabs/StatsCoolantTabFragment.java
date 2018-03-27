package com.minnloft.checkmyfluids.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minnloft.checkmyfluids.PieGraph;
import com.minnloft.checkmyfluids.PieSlice;
import com.minnloft.checkmyfluids.R;
import com.minnloft.checkmyfluids.Utility;
import com.minnloft.checkmyfluids.data.DataSource;
import com.minnloft.checkmyfluids.data.LogsContract.CoolantTable;
import com.minnloft.checkmyfluids.data.LogsContract.OilTable;

import java.text.DecimalFormat;


public class StatsCoolantTabFragment extends Fragment {
    private DataSource mDataSource;

    TextView totalFluidsView;
    PieGraph pg;
    TextView milesToQuarterView;
    TextView milesToHalfView;
    TextView milesToThreeQuartersView;
    TextView milesToOneView;
    TextView daysToQuarterView;
    TextView daysToHalfView;
    TextView daysToThreeQuartersView;
    TextView daysToOneView;
    TextView averageCoolantMonthView;
    TextView averageMilesDayView;

    LinearLayout projectedUsage;
    LinearLayout projectedUsageNotEnough;

    LinearLayout averageMiles;
    LinearLayout averageMilesNotEnough;

    LinearLayout averageQuarts;
    LinearLayout averageQuartsNotEnough;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.stats_coolant_tab, container, false);

        mDataSource = new DataSource(getActivity());
        mDataSource.open();

        initializeViews(view);

        calculateAndSetViews();

        return view;
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

    //Initialize all views needed for StatsCoolantTabFragment
    private void initializeViews(View view){
        totalFluidsView = (TextView) view.findViewById(R.id.textview_total_fluids);
        pg = (PieGraph) view.findViewById(R.id.graph);

        milesToQuarterView = (TextView) view.findViewById(R.id.textview_miles_to_quarter);
        milesToHalfView = (TextView) view.findViewById(R.id.textview_miles_to_half);
        milesToThreeQuartersView = (TextView) view.findViewById(R.id.textview_miles_to_three_quarters);
        milesToOneView = (TextView) view.findViewById(R.id.textview_miles_to_one);
        daysToQuarterView = (TextView) view.findViewById(R.id.textview_days_to_quarter);
        daysToHalfView = (TextView) view.findViewById(R.id.textview_days_to_half);
        daysToThreeQuartersView = (TextView) view.findViewById(R.id.textview_days_to_three_quarters);
        daysToOneView = (TextView) view.findViewById(R.id.textview_days_to_one);
        averageCoolantMonthView = (TextView) view.findViewById(R.id.textview_average_oil_month);
        averageMilesDayView = (TextView) view.findViewById(R.id.textview_average_miles_day);

        projectedUsageNotEnough = (LinearLayout) view.findViewById(R.id.projected_usage_not_enough);
        projectedUsage = (LinearLayout) view.findViewById(R.id.projected_usage);

        averageMilesNotEnough = (LinearLayout) view.findViewById(R.id.average_miles_not_enough);
        averageMiles = (LinearLayout) view.findViewById(R.id.average_miles);

        averageQuartsNotEnough = (LinearLayout) view.findViewById(R.id.average_quarts_not_enough);
        averageQuarts = (LinearLayout) view.findViewById(R.id.average_quarts);
    }

    //Perform all necessary calculations to be able to set the views with the statistics results
    //and then set the views with the results
    private void calculateAndSetViews(){
        int databaseLength = mDataSource.getLength(CoolantTable.TAG);

        String[] dates = mDataSource.getAllDates(CoolantTable.TAG);
        String[] amounts = mDataSource.getAllAmounts(CoolantTable.TAG);
        String[] miles = mDataSource.getAllMiles(CoolantTable.TAG);

        Double amountsTotal;
        Double milesTotal;
        Double daysTotal;
        Double avgCoolant;
        Double avgMiles;

        //Perform calculations to get the data to set all the fields on the statistics page

        //For Total Fluids Lost
        amountsTotal = Utility.calculateTotalFluidLost(amounts);
        totalFluidsView.setText(truncateResults(amountsTotal));

        if(databaseLength >= 2) {

            //For Miles until 1/4, 1/2, 3/4 and 1 Quarts Lost
            Double tempMiles;
            milesTotal = Utility.calculateMilesToLoseHalf(amounts, miles); //Will hold the value to get 1/2 quart

            // 1/4
            tempMiles = milesTotal / 2;
            milesToQuarterView.setText(truncateResults(tempMiles));

            // 3/4
            tempMiles = tempMiles + milesTotal;
            milesToThreeQuartersView.setText(truncateResults(tempMiles));

            // 1/2
            tempMiles = milesTotal;
            milesToHalfView.setText(truncateResults(tempMiles));

            // 1
            tempMiles = milesTotal * 2;
            milesToOneView.setText(truncateResults(tempMiles));


            //For Days until 1/4, 1/2, 3/4  and 1 Quarts Lost
            Double tempDays;
            daysTotal = Utility.calculateDaysToLoseHalf(dates, amounts, miles);

            // 1/4
            tempDays = daysTotal / 2;
            daysToQuarterView.setText(truncateResults(tempDays));

            // 3/4
            tempDays = tempDays + daysTotal;
            daysToThreeQuartersView.setText(truncateResults(tempDays));

            // 1/2
            tempDays = daysTotal;
            daysToHalfView.setText(truncateResults(tempDays));

            // 1
            tempDays = daysTotal * 2;
            daysToOneView.setText(truncateResults(tempDays));


            //Average Coolant used per Month
            avgCoolant = Utility.calculateAverageFluidPerMonth(dates, amounts);
            averageCoolantMonthView.setText(truncateResults(avgCoolant));

            //Average Mile Driven per Day
            avgMiles = Utility.calculateAverageMilesPerDay(dates, miles);

            if(mDataSource.getLength(OilTable.TAG) > 1){
                Double avgOilMiles = Utility.calculateAverageMilesPerDay(mDataSource.getAllDates(OilTable.TAG),
                        mDataSource.getAllMiles(OilTable.TAG));
                averageMilesDayView.setText(truncateResults((avgMiles + avgOilMiles)/2));
            }
            else {
                averageMilesDayView.setText(truncateResults(avgMiles));
            }

        } else {
            projectedUsageNotEnough.setVisibility(View.VISIBLE);
            projectedUsage.setVisibility(View.GONE);

            averageMilesNotEnough.setVisibility(View.VISIBLE);
            averageMiles.setVisibility(View.GONE);

            averageQuartsNotEnough.setVisibility(View.VISIBLE);
            averageQuarts.setVisibility(View.GONE);
        }


        int oilLogAmount = mDataSource.getLength(OilTable.TAG);
        int coolantLogAmount = mDataSource.getLength(CoolantTable.TAG);
        int totalLogAmount = oilLogAmount + coolantLogAmount;

        PieSlice slice;

        if (databaseLength >= 1) {

            pg.addData(coolantLogAmount, totalLogAmount);

            slice = new PieSlice();
            slice.setColor(getResources().getColor(R.color.AccentColor));
            slice.setValue(coolantLogAmount);
            pg.addSlice(slice);

            if(totalLogAmount == coolantLogAmount){
                //Do nothing for the total slice because we want it to display all oil log count
            } else {
                slice = new PieSlice();
                slice.setColor(getResources().getColor(R.color.TextSecondary));
                slice.setValue(totalLogAmount - coolantLogAmount);
                pg.addSlice(slice);
            }

        } else {
            //There are no logs so display that there are none
            pg.addData(0, totalLogAmount);

            slice = new PieSlice();
            slice.setColor(getResources().getColor(R.color.TextSecondary));
            slice.setValue(1);
            pg.addSlice(slice);
        }
    }


    private String truncateResults(double results) {
        DecimalFormat df = new DecimalFormat("0.00");
        String result = df.format(results);

        return result;
    }

}