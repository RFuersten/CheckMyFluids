package com.minnloft.checkmyfluids;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ryan on 9/29/2015.
 */
public class Utility {
    public static String calculateDateRange(String[] dates){
        Date[] dateDates = new Date[dates.length];

        Date date = new Date();
        for(int i = 0; i < dates.length; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            try {
                date = sdf.parse(dates[i]);
            } catch (ParseException e){
                //Do nothing
            }
            dateDates[i] = date;
        }

        long firstDay = dateDates[0].getTime();
        long lastDay = dateDates[0].getTime();

        for(int i = 0; i < dateDates.length; i++) {
            //Days
            if(firstDay > dateDates[i].getTime())
                firstDay = dateDates[i].getTime();
            if(lastDay < dateDates[i].getTime())
                lastDay = dateDates[i].getTime();
        }

        Calendar calFirst = Calendar.getInstance();
        Date firstDate = new Date();
        firstDate.setTime(firstDay);
        Calendar calLast = Calendar.getInstance();
        Date lastDate = new Date();
        lastDate.setTime(lastDay);

        calFirst.setTime(firstDate);
        calLast.setTime(lastDate);

        String dateRange = "";

        dateRange = getMonthName(calFirst.get(Calendar.MONTH)) + " " + calFirst.get(Calendar.YEAR) + " - " +
                getMonthName(calLast.get(Calendar.MONTH)) + " " + calLast.get(Calendar.YEAR);

        return dateRange;
    }


    public static double calculateTotalFluidLost(String[] amounts){
        Double[] decAmounts = new Double[amounts.length];

        for(int i = 0; i < amounts.length; i++) {
            decAmounts[i] = Double.parseDouble(amounts[i]);
        }

        Double total = 0.0;

        for(int i = 0; i < decAmounts.length; i++) {
            total = total + decAmounts[i];
        }

        return total;
    }

    public static double calculateMilesToLoseHalf(String[] amounts, String[] miles){
        Double[] decMiles = new Double[miles.length];

        for(int i = 0; i < amounts.length; i++) {
            decMiles[i] = Double.parseDouble(miles[i]);
        }

        //Get miles lowest and highest values so I can get the difference
        double lowest = decMiles[0];
        double highest = decMiles[0];

        for (int i = 0; i < decMiles.length; i++){
            if (lowest > decMiles[i])
                lowest = decMiles[i];
            if (highest < decMiles[i])
                highest = decMiles[i];
        }

        Double amountTotal = calculateTotalFluidLost(amounts);

        return .5 * (1 / (amountTotal/ (highest - lowest)));
    }

    public static double calculateDaysToLoseHalf(String[] dates, String[] amounts, String[] miles){
        return .5 * ((2 * calculateMilesToLoseHalf(amounts, miles)) / calculateAverageMilesPerDay(dates, miles));
    }

    public static double calculateAverageFluidPerMonth(String[] dates, String[] amounts){
        Double[] decAmounts = new Double[amounts.length];
        Date[] dateDates = new Date[dates.length];

        Date date = new Date();
        for(int i = 0; i < dates.length; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            try {
                date = sdf.parse(dates[i]);
            } catch (ParseException e){
                //Do nothing
            }
            dateDates[i] = date;
            decAmounts[i] = Double.parseDouble(amounts[i]);
        }

        //Get dates lowest and highest values so I can get the difference
        Double amountTotal = 0.0;
        long firstDay = dateDates[0].getTime();
        long lastDay = dateDates[0].getTime();

        for(int i = 0; i < decAmounts.length; i++) {
            amountTotal = amountTotal + decAmounts[i];

            //Days
            if(firstDay > dateDates[i].getTime())
                firstDay = dateDates[i].getTime();
            if(lastDay < dateDates[i].getTime())
                lastDay = dateDates[i].getTime();
        }

        long diff = Math.abs(firstDay - lastDay);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        return (amountTotal/ diffDays) * 30.5;
    }

    public static double calculateAverageMilesPerDay(String[] dates, String[] miles){
        Date[] dateDates = new Date[dates.length];
        Double[] decMiles = new Double[miles.length];

        Date date = new Date();
        for(int i = 0; i < dates.length; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    date = sdf.parse(dates[i]);
            } catch (ParseException e){
                //Do nothing
            }

            dateDates[i] = date;
            decMiles[i] = Double.parseDouble(miles[i]);
        }

        //Get miles and dates lowest and highest values so I can get the difference
        double lowest = decMiles[0];
        double highest = decMiles[0];
        long firstDay = dateDates[0].getTime();
        long lastDay = dateDates[0].getTime();

        for (int i = 0; i < dates.length; i++){
            //Miles
            if (lowest > decMiles[i])
                lowest = decMiles[i];
            if (highest < decMiles[i])
                highest = decMiles[i];

            //Days
            if(firstDay > dateDates[i].getTime())
                firstDay = dateDates[i].getTime();
            if(lastDay < dateDates[i].getTime())
                lastDay = dateDates[i].getTime();
        }

        long diff = Math.abs(firstDay - lastDay);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        return (highest - lowest) / diffDays;
    }

    private static String getMonthName(int month){
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthNames[month];
    }
}
