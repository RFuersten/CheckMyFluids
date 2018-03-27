package com.minnloft.checkmyfluids;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ryan on 3/5/2018.
 */

public class Validate {

    //Method used to validate the data (Date, Fluid amount, and miles on odometer) user input into the fields when
    //adding or updating a fluid log entry into the database
    public static String validateData(String date, String amount, String miles){
        int error_count = 0;
        String error_message = "";

        //Error check date field
        if (!verifyValidDate(date)) {
            error_count = 1;
            error_message = error_message.concat("Date");
        }
        //Error check amount field
        if (!verifyValidAmount(amount)) {
            error_count = error_count + 1;
            if (error_count == 2)
                error_message = error_message.concat(" and quarts filled");
            else
                error_message = error_message.concat("Quarts filled");
        }
        //Error check miles field
        if (!verifyValidMiles(miles)) {
            error_count = error_count + 1;
            if (error_count == 2 || error_count == 3)
                error_message = error_message.concat(" and mileage");
            else
                error_message = error_message.concat("Mileage");
        }


        switch (error_count) {
            case 1:
                error_message = error_message.concat(" value is invalid.");
                break;
            case 2:
                error_message = error_message.concat(" values are invalid.");
                break;
            case 3:
                error_message = "All values are invalid.";
                break;
        }

        return error_message;

    }

    //Helper method to verify the date entered in the dialog to add or edit a log is valid
    private static boolean verifyValidDate(String date){
        Pattern pattern;
        Matcher matcher;
        boolean matches = true;

        //Regex string for a valid date input
        final String DATE_PATTERN =
                "(0?[1-9]|1[012])[/-](0?[1-9]|[12][0-9]|3[01])[/-]((19|20)\\d\\d)";

        pattern = Pattern.compile(DATE_PATTERN);
        matcher = pattern.matcher(date);

        if(matcher.matches()){
            matcher.reset();

            if(matcher.find()){
                String month = matcher.group(1);
                String day = matcher.group(2);
                int year = Integer.parseInt(matcher.group(3));

                if (day.equals("31") && (month.equals("4") || month.equals("6") || month.equals("9") ||
                        month.equals("11") || month.equals("04") ||
                        month.equals("06") || month.equals("09"))) {
                    //Only months 1, 3, 5, 7, 8, 10, 12 have 31 days
                    matches = false;
                }
                else if (month.equals("2") || month.equals("02")) {
                    //Leap year
                    if(year % 4==0){
                        if(day.equals("30") || day.equals("31"))
                            matches = false;
                    } else {
                        if(day.equals("29")||day.equals("30")||day.equals("31"))
                            matches = false;
                    }
                }
            }
            else
                matches = false;
        }
        else
            matches = false;


        if(date.length() >= 8 && matches)
            return true;
        else
            return false;
    }


    //Helper method to verify the fluid amount entered in the dialog to add or edit a log is valid
    private static boolean verifyValidAmount(String amount){
        boolean parsable = true;

        try{
            Double.parseDouble(amount);
        }catch(NumberFormatException e){
            parsable = false;
        }

        if(amount.length() > 0 && parsable)
            return true;
        else
            return false;
    }


    //Helper method to verify the miles amount entered in the dialog to add or edit a log is valid
    private static boolean verifyValidMiles(String miles){
        boolean parsable = true;

        try{
            Integer.parseInt(miles);
        }catch(NumberFormatException e){
            parsable = false;
        }

        if(miles.length() > 0 && parsable)
            return true;
        else
            return false;
    }

}
