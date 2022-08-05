package com.ls.creditcheckapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.gson.Gson;
import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.exception.APIConnectionException;
import com.interswitchng.sdk.exception.APIException;
import com.interswitchng.sdk.exception.AuthenticationException;
import com.interswitchng.sdk.exception.AuthorisationException;
import com.interswitchng.sdk.exception.InvalidRequestException;
import com.interswitchng.sdk.model.RequestOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.ls.creditcheckapp.Constant.HISTORY_URL;
import static com.ls.creditcheckapp.Constant.SCORE_URL;
import static com.ls.creditcheckapp.Constant.TOKEN_URL;


public class MainActivity extends AppCompatActivity{
    TextView textView,txtCreditScore,txtCreditHistory;
    AppCompatEditText editText;
    Button postRequestButton;
    private String tokenFromServer;
    String clientID,secret,msisdn,score,dateCreated,msisdn1,score1,dateCreated1;
    private  byte[] data64;
    String base64=null;
    private static final String EMPTY_STRING = "";
    private static final String PREF_NAME = "CreditCheck";
    Profile profile;
    Gson gson;
    String json;
    private SharedPreferences.Editor prefEditor;
    private SharedPreferences userPreferences;
    TextView txtInfo;
    OAuthToken token;
    private TokenAPI tokenAPI;
    private String displayResponse,historyResponse;
    private int id,profID;
    RecyclerView recyclerView;
    private CreditHistoryAdapter creditHistoryAdapter;
    List<CreditScoreHistory> creditScoreList;
    private int count;

    private String  base64EncodedCredentials ;
    AdView adView;
    AdRequest adRequest;
    private Bundle bundle;
    private String userName, name,password,email,base64String, phoneNo;
    private String myCreditScore;

    String CLIENT_ID = "IKIA0BC31B2FC9BB9A4A770D90430DF9906730728165";
    //String CLIENT_ID = CLIENT_ID1;
    //String SECRET = SECRET1;
    String SECRET = "560181E348ACA75967C55B8F94441E1ADB864973";
    ProgressDialog progressDialog;
    private AdLoader adLoader;
    private static final String AD_UNIT_NATIVE_ID = "ca-app-pub-2198582162916746/4668652291";
    private static final String AD_UNIT_INTERSTITIAL_ID = "ca-app-pub-2198582162916746/8280594247";
    private InterstitialAd interstitialAd;
    private JSONObject jsonObject = null;;


    private String credentials = Credentials.basic("IKIA0BC31B2FC9BB9A4A770D90430DF9906730728165", "560181E348ACA75967C55B8F94441E1ADB864973");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkInternetConnection();
        base64EncodedCredentials = "Basic" + Base64.encodeToString ((CLIENT_ID + ":" + SECRET) .getBytes (), Base64.NO_WRAP);

        try {
            String key1= String.valueOf(getApplicationContext().getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Passport.overrideApiBase("https://sandbox.interswitchng.com/passport");
        RequestOptions options = RequestOptions.builder()
                .setClientId(CLIENT_ID)
                .setClientSecret(SECRET)
                .build();
        RequestOptions.RequestOptionsBuilder builder = RequestOptions.builder().setClientId(CLIENT_ID).setClientSecret(SECRET);
        try {
            tokenFromServer = new Passport(options).getAccessToken().getToken();
        } catch (AuthorisationException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        }
        builder.setAccessToken(tokenFromServer);
        //options = builder.build();


        gson= new Gson();
        profile= new Profile();
        bundle= new Bundle();
        adView = new AdView(this);
        adRequest = new AdRequest.Builder().build();

        userPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        profile = gson.fromJson(json, Profile.class);
        json = userPreferences.getString("LastProfileUsed", "");
        txtCreditScore = findViewById(R.id.textCreditScore);
        txtCreditHistory = findViewById(R.id.textCreditHistory);
        txtInfo = findViewById(R.id.textViewMain);
        //createInterSwitchTokenAPI33();
        //createInterSwitchTokenAPI();
        editText = findViewById(R.id.editText);
        postRequestButton = (Button) findViewById(R.id.post);
        bundle= getIntent().getExtras();
        adView = findViewById(R.id.adView);
        if(tokenFromServer !=null){
            postRequestButton.setVisibility(View.VISIBLE);
            txtInfo.setVisibility(View.VISIBLE);
            txtInfo.setText(MessageFormat.format("Token{0}", tokenFromServer));
            Toast.makeText(MainActivity.this, "Token: " + tokenFromServer, Toast.LENGTH_LONG).show();
        }else {
            txtCreditScore.setVisibility(View.VISIBLE);
            txtCreditScore.setText(MessageFormat.format("{0}", " It takes some time for the result to come up"));
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                //adView.setAdSize(AdSize.BANNER);
                if(adView !=null){
                    adView.loadAd(adRequest);

                }
            }
        });

        if(bundle !=null){
            profID=bundle.getInt("PROFILE_ID");
            userName=bundle.getString("ProfileUserName","");
            password=bundle.getString("ProfilePassword","");
            name=bundle.getString("PROFILE_NAME");
            phoneNo=bundle.getString("PROFILE_PHONE");
            email=bundle.getString("PROFILE_EMAIL");

        }
        postRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText = findViewById(R.id.editText);
                String editTextInput = editText.getText().toString();
                if (!editTextInput.isEmpty()) {
                    createCreditScoreAPI(token);
                    createHistoryAPI(token);
                }else {
                    Toast.makeText(MainActivity.this, "Please provide your BVN Phone Number", Toast.LENGTH_LONG).show();
                }
                MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                        adView.loadAd(adRequest);
                    }
                });


                //tokenAPI.getCreditScore(editTextInput).enqueue(creditScoreCallback);
                //tokenAPI.getCreditScoreHistory(editTextInput).enqueue(creditScoreHistoryCallback);


            }

        });


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
    private void createInterSwitchTokenAPI() {
        String type="application/x-www-form-urlencoded";
        editText = findViewById(R.id.editText);
        //base64EncodedCredentials = "Basic" + Base64.encodeToString ((CLIENT_ID + ":" + SECRET) .getBytes (), Base64.NO_WRAP);

        base64EncodedCredentials = Base64.encodeToString ((CLIENT_ID + ":" + SECRET) .getBytes (), Base64.NO_WRAP);

        //RequestBody requestBody = RequestBody.create(MediaType.parse("Content-type"), type);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {

                Request originalRequest = chain.request();
                //.header("Authorization", "Bearer ${tokenResponse.value.access_token}")
                Request.Builder builder = originalRequest.newBuilder().header("Authorization", "Basic ${base64EncodedCredentials}").addHeader("Content-Type", type);


                /*Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        token != null ? token.getAuthorization() : base64EncodedCredentials).addHeader("Content-Type", type);*/

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .readTimeout(160, TimeUnit.SECONDS)
                .writeTimeout(160, TimeUnit.SECONDS)
                .connectTimeout(160, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TOKEN_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tokenAPI = retrofit.create(TokenAPI.class);

        tokenAPI.postCredentials("client_credentials").enqueue(tokenCallback);


    }
    Callback<OAuthToken> tokenCallback = new Callback<OAuthToken>() {
        @Override
        public void onResponse(@NotNull Call<OAuthToken> call, Response<OAuthToken> response) {
            if (response.isSuccessful()) {
                token = response.body();
                txtCreditScore.setVisibility(View.VISIBLE);
                txtCreditHistory.setVisibility(View.VISIBLE);
                postRequestButton = (Button) findViewById(R.id.post);
                postRequestButton.setVisibility(View.VISIBLE);
                txtInfo = findViewById(R.id.textViewMain);
                editText = findViewById(R.id.editText);
                editText.setVisibility(View.VISIBLE);
                txtInfo.setVisibility(View.VISIBLE);
                postRequestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editText = findViewById(R.id.editText);
                        String editTextInput = editText.getText().toString();
                        if (!editTextInput.isEmpty()) {
                            createCreditScoreAPI(token);
                            createHistoryAPI(token);
                        }else {
                            Toast.makeText(MainActivity.this, "Please provide your BVN Phone Number", Toast.LENGTH_LONG).show();
                        }
                        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
                            @Override
                            public void onInitializationComplete(InitializationStatus initializationStatus) {
                                adView.loadAd(adRequest);
                            }
                        });


                        //tokenAPI.getCreditScore(editTextInput).enqueue(creditScoreCallback);
                        //tokenAPI.getCreditScoreHistory(editTextInput).enqueue(creditScoreHistoryCallback);


                    }

                });
                txtInfo.setText(MessageFormat.format("Token{0}", token));
                Toast.makeText(MainActivity.this, "Code: " + response.code() + "Message: " + response.message(), Toast.LENGTH_LONG).show();
                txtCreditScore.setText(MessageFormat.format("", "Code: " + response.code() + "Message: " + response.message()));


                //createCreditScoreAPI(token);
                //createHistoryAPI(token);

            } else {
                txtInfo.setVisibility(View.VISIBLE);
                txtInfo.setText(MessageFormat.format("{0}", " It takes some time for the result to come up"));
                Toast.makeText(MainActivity.this, "Failure while requesting token", Toast.LENGTH_LONG).show();
                Log.d("RequestTokenCallback", "Code: " + response.code() + "Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<OAuthToken> call, Throwable t) {
            t.printStackTrace();
        }
    };
    private void createTokenPost() {
        String type="application/x-www-form-urlencoded";
        editText = findViewById(R.id.editText);
        //base64EncodedCredentials = "Basic" + Base64.encodeToString ((CLIENT_ID + ":" + SECRET) .getBytes (), Base64.NO_WRAP);

        base64EncodedCredentials = Base64.encodeToString ((CLIENT_ID + ":" + SECRET) .getBytes (), Base64.NO_WRAP);

        //RequestBody requestBody = RequestBody.create(MediaType.parse("Content-type"), type);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {

                Request originalRequest = chain.request();
                //.header("Authorization", "Bearer ${tokenResponse.value.access_token}")
                Request.Builder builder = originalRequest.newBuilder().header("Authorization", "Basic ${base64EncodedCredentials}").addHeader("Content-Type", type);


                /*Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        token != null ? token.getAuthorization() : base64EncodedCredentials).addHeader("Content-Type", type);*/

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .readTimeout(160, TimeUnit.SECONDS)
                .writeTimeout(160, TimeUnit.SECONDS)
                .connectTimeout(160, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TOKEN_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tokenAPI = retrofit.create(TokenAPI.class);

        tokenAPI.postForToken("client_credentials").enqueue(tokenCallbackStr);

    }
    Callback<String> tokenCallbackStr = new Callback<String>() {
        @Override
        public void onResponse(@NotNull Call<String> call, Response<String> response) {
            if (response.isSuccessful()) {
                String tokenStr = response.body();
                txtCreditScore.setVisibility(View.VISIBLE);
                txtCreditHistory.setVisibility(View.VISIBLE);
                postRequestButton = (Button) findViewById(R.id.post);
                postRequestButton.setVisibility(View.VISIBLE);
                txtInfo = findViewById(R.id.textViewMain);
                editText = findViewById(R.id.editText);
                editText.setVisibility(View.VISIBLE);
                txtInfo.setVisibility(View.VISIBLE);
                try {
                    if (response.body() != null) {
                        jsonObject = new JSONObject(response.body().toString());
                    }
                    if (jsonObject != null && jsonObject.getString("responseCode").contains("00")) {

                        String ourToken = jsonObject.getString("msisdn");
                        myCreditScore = "Our Token: "+ourToken;

                    }
                    //displayResponse += text + " Page\n" + total + " Total\n" + totalPages + " Total Pages\n";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                postRequestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editText = findViewById(R.id.editText);
                        String editTextInput = editText.getText().toString();
                        if (!editTextInput.isEmpty()) {
                            //createCreditScoreAPI(tokenStr);
                           // createHistoryAPI(tokenStr);
                        }else {
                            Toast.makeText(MainActivity.this, "Please provide your BVN Phone Number", Toast.LENGTH_LONG).show();
                        }
                        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
                            @Override
                            public void onInitializationComplete(InitializationStatus initializationStatus) {
                                adView.loadAd(adRequest);
                            }
                        });


                        //tokenAPI.getCreditScore(editTextInput).enqueue(creditScoreCallback);
                        //tokenAPI.getCreditScoreHistory(editTextInput).enqueue(creditScoreHistoryCallback);


                    }

                });
                txtInfo.setText(MessageFormat.format("Token{0}", tokenStr));
                Toast.makeText(MainActivity.this, "Code: " + response.code() + "Message: " + response.message(), Toast.LENGTH_LONG).show();
                txtCreditScore.setText(MessageFormat.format("", "Code: " + response.code() + "Message: " + response.message()));


                //createCreditScoreAPI(token);
                //createHistoryAPI(token);

            } else {
                txtInfo.setVisibility(View.VISIBLE);
                txtInfo.setText(MessageFormat.format("{0}", " It takes some time for the result to come up"));
                Toast.makeText(MainActivity.this, "Failure while requesting token", Toast.LENGTH_LONG).show();
                Log.d("RequestTokenCallback", "Code: " + response.code() + "Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<String> call, Throwable t) {
            t.printStackTrace();
        }
    };


    private void createCreditScoreAPI(OAuthToken token) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                //Request originalRequest = chain.request();

                Request request = chain.request().newBuilder().addHeader("Authorization", token.getAccessToken()).build();
                return chain.proceed(request);
            }
        }).addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SCORE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tokenAPI = retrofit.create(TokenAPI.class);

        tokenAPI.getCreditScore(Objects.requireNonNull(editText.getText()).toString()).enqueue(creditScoreCallback);
    }
    Callback<CreditScore> creditScoreCallback = new Callback<CreditScore>() {
        @Override
        public void onResponse(Call<CreditScore> call, Response<CreditScore> response) {
            if (response.isSuccessful()) {

                CreditScore creditScore = response.body();

                if (creditScore != null) {
                    id = creditScore.getId();
                    msisdn = creditScore.getMsisdn();
                    score = creditScore.getScore();
                    dateCreated = creditScore.getDateCreated();
                }
                try {

                    if (response.body() != null) {
                        jsonObject = new JSONObject(response.body().toString());
                    }
                    if (jsonObject != null && jsonObject.getString("responseCode").contains("00")) {

                        String phoneNo = jsonObject.getString("msisdn");
                        String score = jsonObject.getString("score");
                        String dateCreated4 = jsonObject.getString("dateCreated");
                        myCreditScore = "Phone No: "+phoneNo+"\n"+"score: "+score+"\n"+"Date Created: "+dateCreated4;

                    }
                    //displayResponse += text + " Page\n" + total + " Total\n" + totalPages + " Total Pages\n";
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                displayResponse += id + " Phone\n" + msisdn + " Score\n" + score + " dateCreated\n"+dateCreated;
                txtCreditScore = findViewById(R.id.textCreditScore);
                txtCreditScore.setVisibility(View.VISIBLE);
                if (displayResponse == null) txtCreditScore.setText("no value");
                else txtCreditScore.setText(myCreditScore);
            } else {
                Toast.makeText(MainActivity.this, "Failure while requesting Credit Score", Toast.LENGTH_LONG).show();
                txtCreditScore.setText(MessageFormat.format("Error!{0}", response.message()));
                Log.d("Credit Score Details", "Code: " + response.code() + "Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<CreditScore> call, Throwable t) {
            t.printStackTrace();
        }
    };
    private void createHistoryAPI(OAuthToken token) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request request = chain.request().newBuilder().addHeader("Authorization", token.getAccessToken()).build();
                return chain.proceed(request);
            }
        }).addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HISTORY_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tokenAPI = retrofit.create(TokenAPI.class);
        tokenAPI.getCreditScoreHistory(Objects.requireNonNull(editText.getText()).toString()).enqueue(creditScoreHistoryCallback);

    }
    Callback<List<CreditScoreHistory>> creditScoreHistoryCallback = new Callback<List<CreditScoreHistory>>() {
        @Override
        public void onResponse(Call<List<CreditScoreHistory>> call, Response<List<CreditScoreHistory>> response) {
            if (response.isSuccessful()) {

                recyclerView = findViewById(R.id.recyclerViewHistory);
                txtCreditHistory = findViewById(R.id.textCreditHistory);
                txtCreditHistory.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(MainActivity.this, "Failure getting Credit History", Toast.LENGTH_LONG).show();
                Log.d("History Callback", "Code: " + response.code() + "Message: " + response.message());
            }
            if (response.isSuccessful()) {
                try {
                    String s = response.toString();

                    JSONObject jsonObject = new JSONObject(s);

                    JSONArray jsonArray1 = jsonObject.getJSONArray("creditScores");



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CreditScoreHistory creditScoreHistory = new CreditScoreHistory();
                creditScoreList = response.body();

                if (creditScoreList != null) {
                    for (int i = 0; i < creditScoreList.size(); i++) {
                        creditScoreList.add(creditScoreHistory);
                        count=creditScoreList.size();


                    }
                    if(count>0){
                        txtCreditHistory.setText("History Size:"+count);
                    }
                    if(creditScoreHistory !=null){
                        msisdn = creditScoreHistory.getMsisdn1();
                        score = creditScoreHistory.getScore1();
                        dateCreated = creditScoreHistory.getDateCreated1();
                        creditScoreList = creditScoreHistory.data;

                    }



                }

                displayResponse += count+" Phone\n" + msisdn + " Score\n" + score + " dateCreated\n"+dateCreated;
                creditHistoryAdapter = new CreditHistoryAdapter(MainActivity.this, creditScoreList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(creditHistoryAdapter);
                SnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(recyclerView);
                recyclerView.setNestedScrollingEnabled(false);

                if (displayResponse == null) txtCreditScore.setText("no value");
                else txtCreditHistory.setText(displayResponse);
            } else {
                Toast.makeText(MainActivity.this, "Failure while requesting Credit Score", Toast.LENGTH_LONG).show();
                txtCreditHistory.setText(MessageFormat.format("Error!{0}", response.message()));
                Log.d("Credit Score History", "Code: " + response.code() + "Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<List<CreditScoreHistory>> call, Throwable t) {
            t.printStackTrace();
        }
    };
    public void CheckLogin() {
        if (userPreferences == null)
            userPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);


        String userName = userPreferences.getString("ProfileUserName", "");

        if (userName != null && !userName.equals("")) {
            txtInfo.setText("Welcome " + userName);

        } else
        {
            Intent i = new Intent(MainActivity.this, LoginAct.class);
            startActivity(i);
            finish();
        }
    }
    public void Logout() {
        try {
            if (userPreferences == null)
                userPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

            prefEditor = userPreferences.edit();
            prefEditor.putString("ProfileUserName", "");
            prefEditor.apply();

            Intent i = new Intent(MainActivity.this, LoginAct.class);
            startActivity(i);
            finish();

        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }


    private void getCreditScore() {
        try {
            Call<CreditScore> call = null;
            txtCreditScore.setVisibility(View.VISIBLE);
            txtCreditScore = findViewById(R.id.textCreditScore);

            call = TokenRetrofitClient.getInstance().getMyTokenApi().getCreditScore(editText.getText().toString());

            if (call != null) {
                call.enqueue(new Callback<CreditScore>() {
                    @Override
                    public void onResponse(@NotNull Call<CreditScore> call, Response<CreditScore> response) {
                        txtCreditScore.setText("Your Result:"+response.body().toString());
                    }

                    @Override
                    public void onFailure(@NotNull Call<CreditScore> call, Throwable t) {
                        txtCreditScore.setText("Api Call Failed" + t.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            txtCreditScore.setText("Some Exception Occurred");
        }
    }

    public void takeAction(View view) {
    }
}