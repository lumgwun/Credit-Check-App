package com.ls.creditcheckapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreditScore implements Serializable, Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("msisdn")
    private String msisdn;

    @SerializedName("score")
    private String score;

    @SerializedName("dateCreated")
    private String dateCreated;


    /*public String getAuthorization() {
        return getTokenType() + " " + getAccessToken();
    }*/

    protected CreditScore(Parcel in) {
        id = in.readInt();
        msisdn = in.readString();
        score = in.readString();
        dateCreated = in.readString();
    }

    public static final Creator<CreditScore> CREATOR = new Creator<CreditScore>() {
        @Override
        public CreditScore createFromParcel(Parcel in) {
            return new CreditScore(in);
        }

        @Override
        public CreditScore[] newArray(int size) {
            return new CreditScore[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getScore() {
        return score;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(msisdn);
        parcel.writeString(score);
        parcel.writeString(dateCreated);
    }
}
