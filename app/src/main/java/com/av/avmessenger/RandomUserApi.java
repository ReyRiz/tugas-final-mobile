package com.av.avmessenger;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RandomUserApi {
    @GET("api/?results=10")
    Call<RandomUserResponse> getUsers();
}