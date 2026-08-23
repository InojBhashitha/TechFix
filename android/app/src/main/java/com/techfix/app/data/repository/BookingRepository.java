package com.techfix.app.data.repository;

import android.content.Context;

import com.techfix.app.data.local.TechFixDatabase;
import com.techfix.app.data.local.dao.BookingDao;
import com.techfix.app.data.local.entities.BookingEntity;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.data.remote.ApiService;
import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.BookingResponseDto;
import com.techfix.app.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {

    private final ApiService apiService;
    private final BookingDao bookingDao;
    private final Context context;

    public interface BookingCallback<T> {
        void onSuccess(T data, boolean isFromCache);
        void onError(String error);
    }

    public BookingRepository(Context context) {
        this.context = context.getApplicationContext();
        this.apiService = ApiClient.getApiService();
        TechFixDatabase db = TechFixDatabase.getInstance(this.context);
        this.bookingDao = db.bookingDao();
    }

    public void getMyBookings(BookingCallback<List<BookingResponseDto>> callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            // Load cached bookings from SQLite
            List<BookingEntity> entities = bookingDao.getAllBookings();
            callback.onSuccess(mapBookingEntitiesToDtos(entities), true);
            return;
        }

        // Online fetch
        apiService.getMyBookings().enqueue(new Callback<ApiResponse<List<BookingResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BookingResponseDto>>> call, Response<ApiResponse<List<BookingResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<BookingResponseDto> dtos = response.body().getData();
                    cacheBookings(dtos);
                    callback.onSuccess(dtos, false);
                } else {
                    List<BookingEntity> entities = bookingDao.getAllBookings();
                    callback.onSuccess(mapBookingEntitiesToDtos(entities), true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BookingResponseDto>>> call, Throwable t) {
                List<BookingEntity> entities = bookingDao.getAllBookings();
                callback.onSuccess(mapBookingEntitiesToDtos(entities), true);
            }
        });
    }

    private void cacheBookings(List<BookingResponseDto> dtos) {
        if (dtos == null) return;
        List<BookingEntity> entities = new ArrayList<>();
        for (BookingResponseDto dto : dtos) {
            double cost = dto.getTotalCost() != null ? dto.getTotalCost().doubleValue() : 0.0;
            entities.add(new BookingEntity(
                    dto.getId(),
                    dto.getBookingReference(),
                    dto.getServiceName(),
                    dto.getBranchName(),
                    dto.getDeviceBrand(),
                    dto.getDeviceModel(),
                    dto.getProblemDescription(),
                    dto.getCurrentStatus(),
                    dto.getStatusDisplayName(),
                    cost,
                    dto.getAppointmentDate(),
                    dto.getCreatedAt()
            ));
        }
        bookingDao.clearAll();
        bookingDao.insertAll(entities);
    }

    private List<BookingResponseDto> mapBookingEntitiesToDtos(List<BookingEntity> entities) {
        List<BookingResponseDto> dtos = new ArrayList<>();
        if (entities == null) return dtos;
        for (BookingEntity entity : entities) {
            BookingResponseDto dto = new BookingResponseDto();
            dtos.add(dto);
        }
        return dtos;
    }
}
