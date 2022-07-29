package com.ls.creditcheckapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
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
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.gson.Gson;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.greenrobot.eventbus.EventBus.TAG;

public class SignUpAct extends AppCompatActivity {
    private static final String PREF_NAME = "CreditCheck";
    Profile profile;
    Gson gson;
    String json,phoneNo,email,name;
    SharedPreferences.Editor editor;
    private SharedPreferences userPreferences;
    private AppCompatEditText edtUserName,edtPassword, edtName, edtEmail, edtPhone;
    private AppCompatButton btnLogin,btnForGotPassword,btnSignup;
    private AppCompatCheckBox checkBox;
    private AppCompatTextView txtMg;
    private SharedPreferences.Editor prefEditor;
    PrefManager prefManager;
    private String password,userName;
    DBHelper dbHelper;
    private int profileID;
    private AdLoader adLoader;
    AdView adView;
    AdRequest adRequest;
    private static final String AD_UNIT_NATIVE_ID = "ca-app-pub-2198582162916746/4668652291";
    private static final String AD_UNIT_INTERSTITIAL_ID = "ca-app-pub-2198582162916746/8280594247";
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sign_up);
        checkInternetConnection();
        profile= new Profile();
        gson=new Gson();
        adView = new AdView(this);
        adRequest = new AdRequest.Builder().build();
        dbHelper= new DBHelper(this);
        edtUserName = findViewById(R.id.s_username);
        edtPassword = findViewById(R.id.s_password);
        btnLogin = findViewById(R.id.btn_login_account);
        checkBox = findViewById(R.id.check_rem);
        txtMg = findViewById(R.id.txt_msg);
        edtName = findViewById(R.id.s_name);
        edtEmail = findViewById(R.id.s_Email);
        edtPhone = findViewById(R.id.s_PhoneN);
        profileID = ThreadLocalRandom.current().nextInt(1125, 10400);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        btnSignup = findViewById(R.id.btn_SignUp);
        userPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        profile = gson.fromJson(json, Profile.class);
        json = userPreferences.getString("LastProfileUsed", "");
        btnSignup.setOnClickListener(this::SignUp);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //edtUserName.setError(null);
                //edtPassword.setError(null);
                //edtName.setError(null);
                //edtEmail.setError(null);
                //edtPhone.setError(null);
                userName = Objects.requireNonNull(edtUserName.getText()).toString();
                password= Objects.requireNonNull(edtPassword.getText()).toString();
                name= Objects.requireNonNull(edtName.getText()).toString();
                email= edtName.getText().toString();
                phoneNo= Objects.requireNonNull(edtPhone.getText()).toString();

                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(SignUpAct.this, "Please enter a valid UserName.", Toast.LENGTH_SHORT).show();
                }if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpAct.this, "Please enter a valid Password.", Toast.LENGTH_SHORT).show();
                }if (TextUtils.isEmpty(name)) {
                    Toast.makeText(SignUpAct.this, "Please enter a valid Name.", Toast.LENGTH_SHORT).show();
                }if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpAct.this, "Please enter a valid Email Address.", Toast.LENGTH_SHORT).show();
                }if (TextUtils.isEmpty(phoneNo)) {
                    Toast.makeText(SignUpAct.this, "Please enter a valid UserName.", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle= new Bundle();
                    bundle.putInt("ProfileID",profileID);
                    bundle.putString("UserName",userName);
                    bundle.putString("Name",name);
                    bundle.putString("Password",password);
                    bundle.putString("Email",email);
                    bundle.putString("Phone",phoneNo);
                    //doSignUp(userName,password,name,email,phoneNo,profileID);
                    if(dbHelper !=null){
                        dbHelper.insertNewUser(profileID,name,userName,password,email,phoneNo);

                    }
                    Intent mainIntent = new Intent(SignUpAct.this, MainActivity.class);
                    overridePendingTransition(0,0);
                    mainIntent.putExtras(bundle);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                    login(userName,password,name,email,phoneNo,profileID);
                }
                /*if(isValid(userName,password)){
                    login(userName,password,name,email,phoneNo,profileID);
                }*/
                adLoader = new AdLoader.Builder(SignUpAct.this, AD_UNIT_NATIVE_ID)
                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(NativeAd NativeAd) {
                                // Show the ad.
                            }
                        })
                        .withAdListener(new AdListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {
                                if (isDestroyed()) {
                                    adLoader.loadAds(new AdRequest.Builder().build(), 2);
                                }

                            }
                        })
                        .withNativeAdOptions(new NativeAdOptions.Builder()
                                // Methods in the NativeAdOptions.Builder class can be
                                // used here to specify individual options settings.
                                .build())
                        .build();




            }
        });
        adView = findViewById(R.id.adView);
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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpAct.this,LoginAct.class));

            }
        });
    }
    protected void doSignUp(String userName,String password,String name,String email,String phoneNo,int profileID) {

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
                        SignUpAct.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        SignUpAct.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        SignUpAct.this.interstitialAd = null;
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
                                SignUpAct.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
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
        dbHelper.close();
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

    private void login(final String userName, final String password, String name, String email, String phoneNo, int profileID){
        dbHelper= new DBHelper(this);
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edito3 = pref.edit();
        edito3.putString("ProfileUserName", userName);
        edito3.putString("ProfilePassword", password);
        edito3.putString("PROFILE_NAME", name);
        edito3.putString("PROFILE_PASSWORD", password);
        edito3.putString("PROFILE_PHONE", phoneNo);
        edito3.putString("PROFILE_EMAIL", email);
        edito3.putInt("PROFILE_ID", profileID);
        edito3.apply();
        Toast.makeText(SignUpAct.this,"Signing you up...",Toast.LENGTH_SHORT).show();
        prefManager.saveLoginDetails(userName,password);


    }

    public boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        @SuppressLint("MissingPermission") NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public boolean checkInternetConnection() {
        boolean hasInternetConnection = hasInternetConnection();
        if (!hasInternetConnection) {
            showWarningDialog("Internet connection failed");
        }

        return hasInternetConnection;
    }
    public void showWarningDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.button_ok, null);
        builder.show();
    }
    private boolean isValid(String number, String password){
        if(number.isEmpty()){
            showMessage("Empty User Name");
            edtUserName.setError("Empty User Name");
            return false;
        }else if(password.isEmpty()){
            showMessage("Empty Password");
            edtPassword.setError("Empty Password");
            return false;
        }
        return true;
    }
    private void showMessage(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

    public void SignUp(View view) {
        edtUserName = findViewById(R.id.s_username);
        edtPassword = findViewById(R.id.s_password);
        btnLogin = findViewById(R.id.btn_login_account);
        checkBox = findViewById(R.id.check_rem);
        txtMg = findViewById(R.id.txt_msg);
        edtName = findViewById(R.id.s_name);
        edtEmail = findViewById(R.id.s_Email);
        edtPhone = findViewById(R.id.s_PhoneN);
        edtUserName.setError(null);
        edtPassword.setError(null);
        edtName.setError(null);
        edtEmail.setError(null);
        edtPhone.setError(null);
        userName = Objects.requireNonNull(edtUserName.getText()).toString();
        password= Objects.requireNonNull(edtPassword.getText()).toString();
        name= Objects.requireNonNull(edtName.getText()).toString();
        email= edtName.getText().toString();
        phoneNo= Objects.requireNonNull(edtPhone.getText()).toString();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(SignUpAct.this, "Please enter a valid UserName.", Toast.LENGTH_SHORT).show();
        }if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignUpAct.this, "Please enter a valid Password.", Toast.LENGTH_SHORT).show();
        }if (TextUtils.isEmpty(name)) {
            Toast.makeText(SignUpAct.this, "Please enter a valid Name.", Toast.LENGTH_SHORT).show();
        }if (TextUtils.isEmpty(email)) {
            Toast.makeText(SignUpAct.this, "Please enter a valid Email Address.", Toast.LENGTH_SHORT).show();
        }if (TextUtils.isEmpty(phoneNo)) {
            Toast.makeText(SignUpAct.this, "Please enter a valid UserName.", Toast.LENGTH_SHORT).show();
        } else {
            Bundle bundle= new Bundle();
            bundle.putInt("ProfileID",profileID);
            bundle.putString("UserName",userName);
            bundle.putString("Name",name);
            bundle.putString("Password",password);
            bundle.putString("Email",email);
            bundle.putString("Phone",phoneNo);
            Intent mainIntent = new Intent(SignUpAct.this, MainActivity.class);
            overridePendingTransition(0,0);
            mainIntent.putExtras(bundle);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            login(userName,password,name,email,phoneNo,profileID);
            if(dbHelper !=null){
                dbHelper.insertNewUser(profileID,name,userName,password,email,phoneNo);

            }

        }
    }
}