package com.ls.creditcheckapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetroApis {
    String BASE_URL = "https://api.creditregistry.com/nigeria/AutoCred/Test/v8/api/GetReport202/";

    @GET("test")
    Call<Object> getTestData();

    @GET("test/{id}")
    Call<Object> getPathVariableData(@Path("id") String id);

    @GET("queryTest")
    Call<Object> getQueryParamData(@Query("queryParam") String queryParam);

    @GET("queryTest/{id}")
    Call<Object> getWithQueryAndParamVarTest(@Path("id") String id, @Query("queryParam") String queryParam);

    @POST("postTest")
    Call<Object> getPostData(@Body String body);
}
