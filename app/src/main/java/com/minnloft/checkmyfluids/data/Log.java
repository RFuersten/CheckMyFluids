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

    public String getmDateAsString(){
        return getmDateMonth() + " " + getmDateDay() + "," + " " + getmDateYear();
    }

    public String getmDateMonth() {
        String parts[];
        String monthChar = "Jan.";

        parts = mDate.split("/");

        String month = parts[0];

        switch (month) {
            case "01":
                monthChar = "Jan";
                break;
            case "1":
                monthChar = "Jan";
                break;
            case "02":
                monthChar = "Feb";
                break;
            case "2":
                monthChar = "Feb";
                break;
            case "03":
                monthChar = "Mar";
                break;
            case "3":
                monthChar = "Mar";
                break;
            case "04":
                monthChar = "Apr";
                break;
            case "4":
                monthChar = "Apr";
                break;
            case "05":
                monthChar = "May";
                break;
            case "5":
                monthChar = "May";
                break;
            case "06":
                monthChar = "Jun";
                break;
            case "6":
                monthChar = "Jun";
                break;
            case "07":
                monthChar = "Jul";
                break;
            case "7":
                monthChar = "Jul";
                break;
            case "08":
                monthChar = "Aug";
                break;
            case "8":
                monthChar = "Aug";
                break;
            case "09":
                monthChar = "Sep";
                break;
            case "9":
                monthChar = "Sep";
                break;
            case "10":
                monthChar = "Oct";
                break;
            case "11":
                monthChar = "Nov";
                break;
            case "12":
                monthChar = "Dec";
                break;
        }

        return monthChar;
    }

    public String getmDateDay(){
        String parts[];

        parts = mDate.split("/");

        String day = parts[1];

        switch(day) {
            case "01":
                day = "1";
                break;
            case "02":
                day = "2";
                break;
            case "03":
                day = "3";
                break;
            case "04":
                day = "4";
                break;
            case "05":
                day = "5";
                break;
            case "06":
                day = "6";
                break;
            case "07":
                day = "7";
                break;
            case "08":
                day = "8";
                break;
            case "09":
                day = "9";
                break;
        }

        return day;

    }

    public String getmDateYear(){
        String parts[];

        parts = mDate.split("/");

        String year = parts[2];

        return year;
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