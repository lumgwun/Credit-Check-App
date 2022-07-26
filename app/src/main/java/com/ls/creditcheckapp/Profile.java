package com.ls.creditcheckapp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Profile implements Serializable, Parcelable {

    public static final String PROFILE_TABLE = "p_Table";
    public static final String PROFILE_USERNAME = "p_username";
    public static final String PROFILE_NAME = "p_name";
    public static final String PROFILE_PASSWORD = "p_Password";
    public static final String PROFILE_ID = "P_ID";
    public static final String PROFILE_EMAIL = "P_email";
    public static final String PROFILE_PHONE = "P_Phone";
    public static final String PROFILE_STATUS = "P_Status";

    public static final String CREATE_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS " + PROFILE_TABLE + " (" + PROFILE_ID + " INTEGER , " + PROFILE_NAME + "TEXT," + PROFILE_USERNAME + " TEXT , " +
            PROFILE_PASSWORD + " TEXT , " + PROFILE_EMAIL + " TEXT , " + PROFILE_PHONE + " TEXT , " + PROFILE_STATUS + " TEXT , "  + "PRIMARY KEY(" + PROFILE_ID + "))";
    private int profileID;
    private String name;
    private String userName;
    private String password;
    private String phoneNo;
    private String email;
    private String status;
    public Profile() {
        super();

    }
    public Profile(int profileID, String name, String phoneNo,  String email) {
        this.profileID = profileID;
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
    }

    protected Profile(Parcel in) {
        profileID = in.readInt();
        name = in.readString();
        phoneNo = in.readString();
        email = in.readString();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(profileID);
        parcel.writeString(name);
        parcel.writeString(phoneNo);
        parcel.writeString(email);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
