package com.unity3d.player;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TestClient {

    @POST("user")
    Call<User> createUser(@Body User user);
}
