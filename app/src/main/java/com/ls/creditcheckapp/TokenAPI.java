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
    @POST("oauth2/token")
    Call<OAuthToken> postCredentials(@Field("grant_type") String grantType);

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<String> postForToken(@Field("grant_type") String grantType);


    //@FormUrlEncoded
    @GET("msisdn")
    Call<CreditScore> getCreditScore(@Query("client_credentials") String queryParam);

    //@FormUrlEncoded
    @GET("msisdn")
    Call<List<CreditScoreHistory>> getCreditScoreHistory(@Query("client_credentials") String phoneNo);


}
