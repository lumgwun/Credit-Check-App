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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.ls.creditcheckapp.Constant.CLIENT_ID;
import static com.ls.creditcheckapp.Constant.HISTORY_URL;
import static com.ls.creditcheckapp.Constant.REPORT_URL;
import static com.ls.creditcheckapp.Constant.SCORE_URL;
import static com.ls.creditcheckapp.Constant.SECRET;
import static com.ls.creditcheckapp.Constant.TOKEN_URL;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
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
    private int id,id1;
    RecyclerView recyclerView;
    private CreditHistoryAdapter creditHistoryAdapter;
    List<CreditScoreHistory> creditScoreList;
    private int count;
    private String credentials = Credentials.basic(CLIENT_ID, SECRET);
    private String  base64EncodedCredentials ;
    AdView adView;
    AdRequest adRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkInternetConnection();
        gson= new Gson();
        profile= new Profile();
        adView = new AdView(this);
        adRequest = new AdRequest.Builder().build();
        base64EncodedCredentials = "Basic" + Base64.encodeToString ((CLIENT_ID + ":" + SECRET) .getBytes (), Base64.NO_WRAP);
        userPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        profile = gson.fromJson(json, Profile.class);
        json = userPreferences.getString("LastProfileUsed", "");
        txtCreditScore = findViewById(R.id.textCreditScore);
        txtCreditHistory = findViewById(R.id.textCreditHistory);
        txtInfo = findViewById(R.id.textViewMain);
        createInterSwitchTokenAPI33();
        editText = findViewById(R.id.editText);
        postRequestButton = (Button) findViewById(R.id.post);
        postRequestButton.setOnClickListener(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        postRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tokenAPI.postCredentials("client_credentials").enqueue(tokenCallback);
                MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });

            }
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
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        token != null ? token.getAuthorization() : credentials);

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TOKEN_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tokenAPI = retrofit.create(TokenAPI.class);
    }
    private void createInterSwitchTokenAPI33() {
        String type="application/x-www-form-urlencoded";
        String base64String=MyRetrofitClient.myRetrofitClient.encodeToBase64();
        //RequestBody requestBody = RequestBody.create(MediaType.parse("Content-type"), type);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        logging.redactHeader("Authorization");
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addNetworkInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public okhttp3.Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Authorization", base64String).build();
                        return chain.proceed(request);
                    }
                })
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TOKEN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        tokenAPI = retrofit.create(TokenAPI.class);

    }
    public void onClick(View view) {
        if (view.getId() == R.id.post) {
            String editTextInput = editText.getText().toString();
            if (!editTextInput.isEmpty())
                createCreditScoreAPI(token);
                createHistoryAPI(token);
                tokenAPI.getCreditScore(editTextInput).enqueue(creditScoreCallback);
                tokenAPI.getCreditScoreHistory(editTextInput).enqueue(creditScoreHistoryCallback);


            }  else {
                Toast.makeText(this, "Please provide your BVN Phone Number", Toast.LENGTH_LONG).show();
            }
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
                txtInfo.setVisibility(View.VISIBLE);
                txtInfo.setText(MessageFormat.format("Token{0}", token));
                //createCreditScoreAPI(token);
                //createHistoryAPI(token);

            } else {
                Toast.makeText(MainActivity.this, "Failure while requesting token", Toast.LENGTH_LONG).show();
                Log.d("RequestTokenCallback", "Code: " + response.code() + "Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<OAuthToken> call, Throwable t) {
            t.printStackTrace();
        }
    };


    private void createCreditScoreAPI(OAuthToken token) {
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
                .baseUrl(SCORE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tokenAPI = retrofit.create(TokenAPI.class);
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

                displayResponse += id + " Phone\n" + msisdn + " Score\n" + score + " dateCreated\n"+dateCreated;
                txtCreditScore = findViewById(R.id.textCreditScore);
                if (displayResponse == null) txtCreditScore.setText("no value");
                else txtCreditScore.setText(displayResponse);
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
    }
    Callback<List<CreditScoreHistory>> creditScoreHistoryCallback = new Callback<List<CreditScoreHistory>>() {
        @Override
        public void onResponse(Call<List<CreditScoreHistory>> call, Response<List<CreditScoreHistory>> response) {
            if (response.isSuccessful()) {

                recyclerView = findViewById(R.id.recyclerViewHistory);
            } else {
                Toast.makeText(MainActivity.this, "Failure while requesting user details", Toast.LENGTH_LONG).show();
                Log.d("History Callback", "Code: " + response.code() + "Message: " + response.message());
            }
            if (response.isSuccessful()) {
                CreditScoreHistory creditScoreHistory = new CreditScoreHistory();
                creditScoreList = response.body();

                if (creditScoreList != null) {
                    for (int i = 0; i < creditScoreList.size(); i++) {
                        creditScoreList.add(creditScoreHistory);
                        count=creditScoreList.size();

                    }
                    if(creditScoreHistory !=null){
                        msisdn = creditScoreHistory.getMsisdn1();
                        score = creditScoreHistory.getScore1();
                        dateCreated = creditScoreHistory.getDateCreated1();
                        creditScoreList = creditScoreHistory.data;

                    }


                }

                displayResponse += count+" Phone\n" + msisdn + " Score\n" + score + " dateCreated\n"+dateCreated;
                txtCreditHistory = findViewById(R.id.textCreditHistory);
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

    private void sendCreditCheckPostRequest(String tokenFromServer,String userPhoneNumber) {

        @SuppressWarnings("deprecation")
        @SuppressLint("StaticFieldLeak")
        class SendPostReqAsyncTask extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                JSONObject jsonObject2 = new JSONObject();
                InputStream inputStream = null;
                String result2 = "";
                String json2 = "";
                HttpURLConnection urlConnection = null;


                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(REPORT_URL);
                    StringEntity stringEntity2 = new StringEntity(userPhoneNumber);
                    httpPost.setEntity(stringEntity2);
                    httpPost.setHeader("Authorization", tokenFromServer);
                    httpPost.setHeader("Content-type", "application/json");
                    HttpResponse httpResponse1 = httpclient.execute(httpPost);

                    inputStream = httpResponse1.getEntity().getContent();

                    InputStreamReader inputStreamReader1 = new InputStreamReader(inputStream);

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader1);

                    StringBuilder stringBuilder2 = new StringBuilder();

                    String bufferedStrChunk = null;
                    int data = inputStreamReader1.read();

                    while((bufferedStrChunk = bufferedReader.readLine()) != null){
                        stringBuilder2.append(bufferedStrChunk);
                    }
                    result2=stringBuilder2.toString();


                    return result2;

                } catch (Exception e) {
                    Log.d("InputStream", e.getLocalizedMessage());
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result2) {
                super.onPostExecute(result2);

                try {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date newDate = calendar.getTime();
                    String transferDate = sdf.format(newDate);

                    String userTimelineText="You requested for credit check:"+result2+""+"is your credit status";
                    String timelineTittle="Non Subscriber Loan Request Alert";
                    //String timelineDetails="User Credit status for:"+customerID+":"+result2;
                    SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor edito3 = pref.edit();
                    edito3.putString("CreditB", result2);
                    edito3.putString("Date", transferDate);
                    edito3.apply();

                    //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("ProfileUserName",userName).apply();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("Date",transferDate).apply();

                    JSONObject jsonObject = new JSONObject(result2);
                    String creditStatus = jsonObject.getString("Responses");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


    }

   /* @Override
    public void onClick(View view) {
        if (view.getId() == R.id.post) {
            getApiDataWithParamORPostData("post");
            getApiDataWithParamORPostData("query");
        }
    }*/

    private void getCreditScore() {
        try {
            Call<CreditScore> call = null;

            call = MyRetrofitClient.getInstance().getMyTokenApi().getCreditScore(editText.getText().toString());

            if (call != null) {
                call.enqueue(new Callback<CreditScore>() {
                    @Override
                    public void onResponse(@NotNull Call<CreditScore> call, Response<CreditScore> response) {
                        textView.setText("Your Result:"+response.body().toString());
                    }

                    @Override
                    public void onFailure(@NotNull Call<CreditScore> call, Throwable t) {
                        textView.setText("Api Call Failed" + t.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            textView.setText("Some Exception Occurred");
        }
    }

}