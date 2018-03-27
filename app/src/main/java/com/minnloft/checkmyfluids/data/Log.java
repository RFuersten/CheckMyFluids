package com.minnloft.checkmyfluids.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ryan on 9/27/2015.
 */
public class Log implements Parcelable{

    private long mId;
    private String mDate;
    private String mAmount;
    private String mMiles;

    public Log(){
    }

    public Log(long mId, String mDate, String mAmount, String mMiles){
        this.mId = mId;
        this.mDate = mDate;
        this.mAmount = mAmount;
        this.mMiles = mMiles;
    }

    public Log(Parcel in) {
        mId = in.readLong();
        mDate = in.readString();
        mAmount = in.readString();
        mMiles = in.readString();
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmDateDayYear() {
        String parts[];

        parts = mDate.split("/");

        String day = parts[1];
        String year = parts[2];

        String temp;

        switch(day){
            case "01":
                day = "1";
                temp = "st";
                break;
            case "1":
                temp = "st";
                break;
            case "02":
                day = "2";
                temp = "nd";
                break;
            case "2":
                temp = "nd";
                break;
            case "03":
                day = "3";
                temp = "rd";
                break;
            case "3":
                temp = "rd";
                break;
            case "04":
                day = "4";
                temp = "th";
                break;
            case "05":
                day = "5";
                temp = "th";
                break;
            case "06":
                day = "6";
                temp = "th";
                break;
            case "07":
                day = "7";
                temp = "th";
                break;
            case "08":
                day = "8";
                temp = "th";
                break;
            case "09":
                day = "9";
                temp = "th";
                break;
            default:
                temp = "th";
                break;
        }

        return day + temp + " " + year;
    }

    public String getmDateMonth() {
        String parts[];
        String monthChar = "Jan.";

        parts = mDate.split("/");

        String month = parts[0];

        switch (month) {
            case "01":
                monthChar = "January";
                break;
            case "1":
                monthChar = "January";
                break;
            case "02":
                monthChar = "February";
                break;
            case "2":
                monthChar = "February";
                break;
            case "03":
                monthChar = "March";
                break;
            case "3":
                monthChar = "March";
                break;
            case "04":
                monthChar = "April";
                break;
            case "4":
                monthChar = "April";
                break;
            case "05":
                monthChar = "May";
                break;
            case "5":
                monthChar = "May";
                break;
            case "06":
                monthChar = "June";
                break;
            case "6":
                monthChar = "June";
                break;
            case "07":
                monthChar = "July";
                break;
            case "7":
                monthChar = "July";
                break;
            case "08":
                monthChar = "August";
                break;
            case "8":
                monthChar = "August";
                break;
            case "09":
                monthChar = "September";
                break;
            case "9":
                monthChar = "September";
                break;
            case "10":
                monthChar = "October";
                break;
            case "11":
                monthChar = "November";
                break;
            case "12":
                monthChar = "December";
                break;
        }

        return monthChar;

    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmAmount() {
        return mAmount;
    }
    public void setmAmount(String mAmount) {
        this.mAmount = mAmount;
    }

    public String getmMiles() {
        return mMiles;
    }

    public void setmMiles(String mMiles) {
        this.mMiles = mMiles;
    }

    @Override
    public String toString(){
        return mDate+" "+mAmount+" "+mMiles;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mDate);
        dest.writeString(mAmount);
        dest.writeString(mMiles);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Log> CREATOR = new Parcelable.Creator<Log>() {
        @Override
        public Log createFromParcel(Parcel in) {
            return new Log(in);
        }

        @Override
        public Log[] newArray(int size) {
            return new Log[size];
        }
    };


}