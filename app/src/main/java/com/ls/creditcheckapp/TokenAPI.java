package com.ls.creditcheckapp;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TokenAPI {
    String contentType= "application/json";
    String authorization= "<>";

    @FormUrlEncoded
    @POST("client_credentials")
    Call<OAuthToken> postCredentials(@Field("grant_type") String grantType);


    @FormUrlEncoded
    @GET("creditScores")
    Call<CreditScore> getCreditScore(@Query("client_credentials") String queryParam);

    @FormUrlEncoded
    @GET("creditScores")
    Call<List<CreditScoreHistory>> getCreditScoreHistory(@Query("client_credentials") String name);



    @GET("/1.1/users/show.json")
    Call<String> getUserDetails(@Query("screen_name") String name);

}
