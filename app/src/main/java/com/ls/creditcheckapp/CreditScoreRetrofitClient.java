package com.ls.creditcheckapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ls.creditcheckapp.Constant.SCORE_URL;
import static com.ls.creditcheckapp.Constant.TOKEN_URL;

public class CreditScoreRetrofitClient {
    protected static CreditScoreRetrofitClient client = null;
    private static RetroApis myRetroApis;
    private static TokenAPI tokenAPI;
    private static Retrofit retrofit = null;

    CreditScoreRetrofitClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SCORE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        myRetroApis = retrofit.create(RetroApis.class);


    }
    public static synchronized CreditScoreRetrofitClient getInstance() {
        if (client == null) {
            client = new CreditScoreRetrofitClient();
        }
        return client;
    }
}
