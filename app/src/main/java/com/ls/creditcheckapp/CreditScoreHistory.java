package com.ls.creditcheckapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreditScoreHistory implements Serializable, Parcelable {

    private CreditScore CreditScore;
    @SerializedName("msisdn1")
    private String msisdn1;

    @SerializedName("score1")
    private String score1;

    @SerializedName("dateCreated1")
    private String dateCreated1;

    protected CreditScoreHistory(Parcel in) {
        CreditScore = in.readParcelable(com.ls.creditcheckapp.CreditScore.class.getClassLoader());
        msisdn1 = in.readString();
        score1 = in.readString();
        dateCreated1 = in.readString();
        data = in.createTypedArrayList(com.ls.creditcheckapp.CreditScoreHistory.CREATOR);
    }

    public static final Creator<CreditScoreHistory> CREATOR = new Creator<CreditScoreHistory>() {
        @Override
        public CreditScoreHistory createFromParcel(Parcel in) {
            return new CreditScoreHistory(in);
        }

        @Override
        public CreditScoreHistory[] newArray(int size) {
            return new CreditScoreHistory[size];
        }
    };

    public CreditScoreHistory() {
        super();
    }

    public String getMsisdn1() {
        return msisdn1;
    }

    public String getScore1() {
        return score1;
    }

    public String getDateCreated1() {
        return dateCreated1;
    }
    @SerializedName("data")
    public ArrayList<CreditScoreHistory> data = new ArrayList();

    public CreditScore getCreditScore() {
        return CreditScore;
    }

    public void setCreditScore(CreditScore creditScore) {
        CreditScore = creditScore;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(CreditScore, i);
        parcel.writeString(msisdn1);
        parcel.writeString(score1);
        parcel.writeString(dateCreated1);
        parcel.writeTypedList(data);
    }
}
