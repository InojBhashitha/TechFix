package com.techfix.app.data.remote;

import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.AuthResponse;
import com.techfix.app.data.remote.dto.LoginRequest;
import com.techfix.app.data.remote.dto.RegisterRequest;
import com.techfix.app.data.remote.dto.UserProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    // ─── Authentication ──────────────────────────────────────────
    @POST("api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @GET("api/auth/profile")
    Call<ApiResponse<UserProfile>> getProfile();
}
