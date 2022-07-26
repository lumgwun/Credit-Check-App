package com.ls.creditcheckapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class PrefManager {
    SharedPreferences pref;
    private static final String TAG = PrefManager.class.getSimpleName();
    SharedPreferences.Editor editor;
    Context _context;
    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "CreditCheck";
    private static final String PREF_PARAM_IS_PROFILE_CREATED = "isProfileCreated";
    private static final String PREF_PARAM_IS_POSTS_WAS_LOADED_AT_LEAST_ONCE = "isPostsWasLoadedAtLeastOnce";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_ADMIN = "IsAdmin";

    public static final String IS_USER_LOGIN = "IsUserLoggedIn";

    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setIsAdmin(boolean isAdmin) {
        editor.putBoolean(IS_ADMIN, isAdmin);
        editor.commit();
    }

    public void saveLoginDetails(String userName, String password) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ProfileUserName", userName);
        editor.putString("ProfilePassword", password);
        editor.apply();
    }
    public void createUserLoginSession(int profileID,String uName, String uPassword,String email,String machine){
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString("ProfileUserName", uName);
        editor.putString("ProfilePassword",  uPassword);
        editor.putLong("ProfileID",  profileID);
        editor.commit();
    }
    public void saveLoan(Context context, Profile profile) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonProfile = gson.toJson(profile);

        editor.putString("Profile", jsonProfile);

        editor.apply();
    }
    public void createUserReport(String uName,String date, String history,String bereauResult,String creditScore){
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString("Name", uName);
        editor.putString("Date",  date);
        editor.putString("History",  history);
        editor.putString("CreditB",  bereauResult);
        editor.putString("CreditScore",  creditScore);
        editor.commit();
    }
    public void logoutUser(){

        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginAct.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }


    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public String getProfileUserName() {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("ProfileUserName", "");
    }
    public String getProfilePassword() {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("ProfilePassword", "");
    }
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }
    public static Boolean isProfileCreated(Context context) {
        return getSharedPreferences(context).getBoolean(PREF_PARAM_IS_PROFILE_CREATED, false);
    }

    public static Boolean isPostWasLoadedAtLeastOnce(Context context) {
        return getSharedPreferences(context).getBoolean(PREF_PARAM_IS_POSTS_WAS_LOADED_AT_LEAST_ONCE, false);
    }

    public static void setProfileCreated(Context context, Boolean isProfileCreated) {
        getSharedPreferences(context).edit().putBoolean(PREF_PARAM_IS_PROFILE_CREATED, isProfileCreated).apply();
    }


    public boolean checkLogin(){
        if(!this.isUserLoggedIn()){


            Intent i = new Intent(_context, LoginAct.class);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);

            return true;
        }
        return false;
    }


}
