package com.ls.creditcheckapp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ls.creditcheckapp.Constant.CLIENT_ID1;
import static com.ls.creditcheckapp.Constant.SECRET1;
import static com.ls.creditcheckapp.Constant.TOKEN_URL;

@SuppressWarnings("deprecation")
public class TokenRetrofitClient {
    protected static TokenRetrofitClient tokenRetrofitClient = null;
    private static RetroApis myRetroApis;
    private static TokenAPI tokenAPI;
    private static Retrofit retrofit = null;
    private String tokenFromServer;
    String clientID,secret;
    private  byte[] data64;
    String base64=null;

    TokenRetrofitClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(TOKEN_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        myRetroApis = retrofit.create(RetroApis.class);


    }
    public String encodeToBase64() {
        clientID= CLIENT_ID1;
        secret=SECRET1;

        String text = clientID + ":" + secret;

        data64 = new byte[0];
        data64 = text.getBytes(StandardCharsets.UTF_8);
        base64=android.util.Base64.encodeToString(data64, android.util.Base64.DEFAULT);

        return base64;
    }

    static Retrofit getTokenClient() {
        String type="application/x-www-form-urlencoded";
        String base64String= TokenRetrofitClient.tokenRetrofitClient.encodeToBase64();
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-type"), type);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        logging.redactHeader("Authorization");
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addNetworkInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Authorization", TokenRetrofitClient.getInstance().encodeToBase64()).build();
                        return chain.proceed(request);
                    }
                })
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(TOKEN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();



        return retrofit;
    }
    static Retrofit getTCreditReportClient(String tokenFromServer,String userPhoneNumber) {
        String type="application/json";
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-type"), type);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        logging.redactHeader("Authorization");
        logging.redactHeader("Content-type");


        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addNetworkInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Authorization", tokenFromServer).build();
                        //Request request3 = chain.request().newBuilder().method("Content-type", requestBody).build();
                        return chain.proceed(request);
                    }
                })
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();



        return retrofit;
    }
    static Retrofit getClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        logging.redactHeader("Authorization");
        logging.redactHeader("Cookie");
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit;
    }

    public static synchronized TokenRetrofitClient getInstance() {
        if (tokenRetrofitClient == null) {
            tokenRetrofitClient = new TokenRetrofitClient();
        }
        return tokenRetrofitClient;
    }

    public RetroApis getMyApi() {
        return myRetroApis;
    }
    public TokenAPI getMyTokenApi() {
        return tokenAPI;
    }
}
