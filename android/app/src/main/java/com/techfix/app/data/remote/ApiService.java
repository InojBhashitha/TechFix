package com.techfix.app.data.remote;

import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.AuthResponse;
import com.techfix.app.data.remote.dto.BookingRequestDto;
import com.techfix.app.data.remote.dto.BookingResponseDto;
import com.techfix.app.data.remote.dto.BranchRecommendationRequestDto;
import com.techfix.app.data.remote.dto.BranchRecommendationResponseDto;
import com.techfix.app.data.remote.dto.DeviceCategoryDto;
import com.techfix.app.data.remote.dto.LoginRequest;
import com.techfix.app.data.remote.dto.RegisterRequest;
import com.techfix.app.data.remote.dto.RepairServiceDto;
import com.techfix.app.data.remote.dto.UserProfile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ─── Authentication ──────────────────────────────────────────
    @POST("api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @GET("api/auth/profile")
    Call<ApiResponse<UserProfile>> getProfile();

    // ─── Categories & Repair Services ────────────────────────────
    @GET("api/categories")
    Call<ApiResponse<List<DeviceCategoryDto>>> getCategories();

    @GET("api/services")
    Call<ApiResponse<List<RepairServiceDto>>> getServices(@Query("categoryId") Long categoryId);

    @GET("api/services/{id}")
    Call<ApiResponse<RepairServiceDto>> getServiceById(@Path("id") Long id);

    // ─── Repair Bookings ─────────────────────────────────────────
    @POST("api/bookings")
    Call<ApiResponse<BookingResponseDto>> createBooking(@Body BookingRequestDto request);

    @GET("api/bookings/my-bookings")
    Call<ApiResponse<List<BookingResponseDto>>> getMyBookings();

    @GET("api/bookings/{reference}")
    Call<ApiResponse<BookingResponseDto>> getBookingByReference(@Path("reference") String reference);

    // ─── Branch Recommendations ──────────────────────────────────
    @POST("api/branches/recommend-branch")
    Call<ApiResponse<BranchRecommendationResponseDto>> recommendBranch(@Body BranchRecommendationRequestDto request);

    // ─── Image Uploads ───────────────────────────────────────────
    @retrofit2.http.Multipart
    @POST("api/images/upload-repair-image")
    Call<ApiResponse<com.techfix.app.data.remote.dto.RepairImageDto>> uploadRepairImage(
            @retrofit2.http.Part okhttp3.MultipartBody.Part file,
            @retrofit2.http.Part("bookingReference") okhttp3.RequestBody bookingReference,
            @retrofit2.http.Part("imageType") okhttp3.RequestBody imageType
    );
}

