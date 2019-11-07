package com.lcd.mymusic.retrofit;

import com.lcd.mymusic.model.BaseResponse;
import com.lcd.mymusic.model.LoginRequest;
import com.lcd.mymusic.model.RegisterRequest;
import com.lcd.mymusic.model.UserProfile;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Services {
    @POST("/api?login")
    Call<BaseResponse<UserProfile>> login(
            @Body LoginRequest request
    );

    @POST("api/register")
    Call<BaseResponse<UserProfile>> register(
            @Body RegisterRequest request
    );
}
