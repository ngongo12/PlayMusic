package com.ps14237.support;

import com.ps14237.model.ServerRequest;
import com.ps14237.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {
    @POST("assignment/user.php")
    @Headers("Content-Type: application/json")
    Call<ServerResponse> userOperation(@Body ServerRequest request);

    @GET("assignment/songs.php")
    Call<ServerResponse> getListSong(@Query("type") String type, @Query("uid") String uid);

    @GET("assignment/songs.php")
    Call<ServerResponse> getListSong(@Query("type") String type, @Query("uid") String uid, @Query("arr") String arr);

    @GET("assignment/songs.php")
    Call<ServerResponse> search(@Query("type") String type, @Query("uid") String uid, @Query("key") String key);


    @GET("assignment/SingerList.php")
    Call<ServerResponse> getSigners();

    @POST("assignment/songs.php")
    @Headers("Content-Type: application/json")
    Call<ServerResponse> songOperation(@Body ServerRequest request);
}
