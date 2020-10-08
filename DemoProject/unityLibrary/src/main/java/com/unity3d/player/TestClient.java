package com.unity3d.player;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public interface TestClient {

    @POST("v1/todos")
    Call<User> createUser(@Body User user);
}
