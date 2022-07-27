package com.ls.creditcheckapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;

import static org.greenrobot.eventbus.EventBus.TAG;

public class LoginAct extends AppCompatActivity {
    private static final String PREF_NAME = "CreditCheck";
    Profile profile;
    Gson gson;
    String json,userName,password;
    SharedPreferences.Editor editor;
    private SharedPreferences userPreferences;
    private AppCompatEditText edtUserName,edtPassword;
    private AppCompatButton btnLogin,btnForGotPassword,btnSignup;
    private AppCompatCheckBox checkBox;
    private AppCompatTextView txtMg;
    private SharedPreferences.Editor prefEditor;
    boolean cancel = false;
    View focusView = null;
    private InterstitialAd interstitialAd;
    AdView adView;
    AdRequest adRequest;

    private static final String AD_UNIT_BANNER = "ca-app-pub-2198582162916746/8150169093";
    private static final String AD_UNIT_NATIVE_ID = "ca-app-pub-2198582162916746/4668652291";
    private static final String AD_UNIT_INTERSTITIAL_ID = "ca-app-pub-2198582162916746/8280594247";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        profile= new Profile();
        adView = new AdView(this);
        gson=new Gson();
        adRequest = new AdRequest.Builder().build();
        edtUserName = findViewById(R.id.edt_username5);
        edtPassword = findViewById(R.id.edt_password6);
        btnLogin = findViewById(R.id.btn_login6);
        checkBox = findViewById(R.id.chk_remember_6);
        txtMg = findViewById(R.id.txt_account_msg);
        btnSignup = findViewById(R.id.btn_create_account6);
        btnForGotPassword = findViewById(R.id.btn_forgot_account_p6);
        userPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        profile = gson.fromJson(json, Profile.class);
        json = userPreferences.getString("LastProfileUsed", "");
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        adView = findViewById(R.id.adViewLogIn);
        adView.loadAd(adRequest);
        //adView.setAdSize(AdSize.BANNER);

        //adView.setAdUnitId("ca-app-pub-2198582162916746/8150169093");

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });


        CheckLogin();
    }
    public void CheckLogin() {
        if (userPreferences == null){
            loadAd();
            userPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            userName = userPreferences.getString("ProfileUserName", "");
            password = userPreferences.getString("ProfileUserName", "");

            if (userName != null && !userName.equals("")) {
                DoLogin(userName, password);
                finish();
            }

        }else {
            if (userName != null && !userName.equals("")) {
                DoLogin(userName, password);
                finish();
            }

        }


    }
    public void DoLogin(String userName, String password) {
        try {
            if (cancel) {

                focusView.requestFocus();
            } else {
                if (checkBox.isChecked()) {
                    saveLoginDetails(userName, password);
                    startHomeActivity(userName,password);

                }

            }

            if (!(password.isEmpty())) {

                if (userPreferences == null)
                    userPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

                prefEditor = userPreferences.edit();
                prefEditor.putString("ProfileUserName", userName);
                prefEditor.putString("ProfilePassword", password);
                prefEditor.commit();

                Intent i = new Intent(LoginAct.this, MainActivity.class);
                startActivity(i);
                finish();
            } else
                focusView = edtPassword;
                cancel = true;
                txtMg.setText("Invalid Credentails");
        } catch (Exception ex) {
            txtMg.setText(ex.getMessage().toString());
        }
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                AD_UNIT_INTERSTITIAL_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        LoginAct.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        LoginAct.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        LoginAct.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;

                        @SuppressLint("DefaultLocale") String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Toast.makeText(
                                LoginAct.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }
    private void showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onStart() {
        super.onStart();
        showInterstitial();
        userPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

    }
    @Override
    protected void onStop() {
        showInterstitial();
        super.onStop();

    }


    private void startHomeActivity(String userName, String password) {
        new PrefManager(this).saveLoginDetails(userName, password);
        Intent i = new Intent(LoginAct.this, MainActivity.class);
        startActivity(i);
        finish();


    }

    private void saveLoginDetails(String userName, String password) {
        new PrefManager(this).saveLoginDetails(userName, password);
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    public void loginUserName(View view) {
    }
}