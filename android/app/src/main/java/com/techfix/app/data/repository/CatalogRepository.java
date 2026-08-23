package com.techfix.app.data.repository;

import android.content.Context;

import com.techfix.app.data.local.TechFixDatabase;
import com.techfix.app.data.local.dao.CategoryDao;
import com.techfix.app.data.local.dao.ServiceDao;
import com.techfix.app.data.local.entities.CategoryEntity;
import com.techfix.app.data.local.entities.ServiceEntity;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.data.remote.ApiService;
import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.DeviceCategoryDto;
import com.techfix.app.data.remote.dto.RepairServiceDto;
import com.techfix.app.utils.NetworkUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogRepository {

    private final ApiService apiService;
    private final CategoryDao categoryDao;
    private final ServiceDao serviceDao;
    private final Context context;

    public interface CatalogCallback<T> {
        void onSuccess(T data, boolean isFromCache);
        void onError(String error);
    }

    public CatalogRepository(Context context) {
        this.context = context.getApplicationContext();
        this.apiService = ApiClient.getApiService();
        TechFixDatabase db = TechFixDatabase.getInstance(this.context);
        this.categoryDao = db.categoryDao();
        this.serviceDao = db.serviceDao();
    }

    public void fetchCategories(CatalogCallback<List<DeviceCategoryDto>> callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            // Load from Room SQLite cache when offline
            List<CategoryEntity> entities = categoryDao.getAllCategories();
            List<DeviceCategoryDto> dtos = mapCategoryEntitiesToDtos(entities);
            callback.onSuccess(dtos, true);
            return;
        }

        // Online fetch via Retrofit
        apiService.getCategories().enqueue(new Callback<ApiResponse<List<DeviceCategoryDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DeviceCategoryDto>>> call, Response<ApiResponse<List<DeviceCategoryDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<DeviceCategoryDto> dtos = response.body().getData();
                    // Update Room DB cache asynchronously
                    cacheCategories(dtos);
                    callback.onSuccess(dtos, false);
                } else {
                    // Fallback to cache on server error
                    List<CategoryEntity> entities = categoryDao.getAllCategories();
                    callback.onSuccess(mapCategoryEntitiesToDtos(entities), true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<DeviceCategoryDto>>> call, Throwable t) {
                // Fallback to cache on network failure
                List<CategoryEntity> entities = categoryDao.getAllCategories();
                callback.onSuccess(mapCategoryEntitiesToDtos(entities), true);
            }
        });
    }

    public void fetchServices(Long categoryId, CatalogCallback<List<RepairServiceDto>> callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            // Offline fallback to Room DB
            List<ServiceEntity> entities;
            if (categoryId != null) {
                entities = serviceDao.getServicesByCategory(categoryId);
            } else {
                entities = serviceDao.getAllServices();
            }
            callback.onSuccess(mapServiceEntitiesToDtos(entities), true);
            return;
        }

        // Online fetch
        apiService.getServices(categoryId).enqueue(new Callback<ApiResponse<List<RepairServiceDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RepairServiceDto>>> call, Response<ApiResponse<List<RepairServiceDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<RepairServiceDto> dtos = response.body().getData();
                    cacheServices(dtos);
                    callback.onSuccess(dtos, false);
                } else {
                    List<ServiceEntity> entities = (categoryId != null) ? serviceDao.getServicesByCategory(categoryId) : serviceDao.getAllServices();
                    callback.onSuccess(mapServiceEntitiesToDtos(entities), true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RepairServiceDto>>> call, Throwable t) {
                List<ServiceEntity> entities = (categoryId != null) ? serviceDao.getServicesByCategory(categoryId) : serviceDao.getAllServices();
                callback.onSuccess(mapServiceEntitiesToDtos(entities), true);
            }
        });
    }

    private void cacheCategories(List<DeviceCategoryDto> dtos) {
        if (dtos == null) return;
        List<CategoryEntity> entities = new ArrayList<>();
        for (DeviceCategoryDto dto : dtos) {
            entities.add(new CategoryEntity(dto.getId(), dto.getName(), dto.getIconName(), dto.getDescription()));
        }
        categoryDao.clearAll();
        categoryDao.insertAll(entities);
    }

    private void cacheServices(List<RepairServiceDto> dtos) {
        if (dtos == null) return;
        List<ServiceEntity> entities = new ArrayList<>();
        for (RepairServiceDto dto : dtos) {
            Long catId = dto.getCategory() != null ? dto.getCategory().getId() : null;
            String catName = dto.getCategory() != null ? dto.getCategory().getName() : "";
            double price = dto.getEstimatedPrice() != null ? dto.getEstimatedPrice().doubleValue() : 0.0;
            entities.add(new ServiceEntity(dto.getId(), catId, catName, dto.getName(),
                    dto.getDescription(), price, dto.getEstimatedDurationMinutes(), dto.getSampleImageUrl()));
        }
        serviceDao.clearAll();
        serviceDao.insertAll(entities);
    }

    private List<DeviceCategoryDto> mapCategoryEntitiesToDtos(List<CategoryEntity> entities) {
        List<DeviceCategoryDto> dtos = new ArrayList<>();
        if (entities == null) return dtos;
        for (CategoryEntity entity : entities) {
            DeviceCategoryDto dto = new DeviceCategoryDto();
            dtos.add(dto);
        }
        return dtos;
    }

    private List<RepairServiceDto> mapServiceEntitiesToDtos(List<ServiceEntity> entities) {
        List<RepairServiceDto> dtos = new ArrayList<>();
        if (entities == null) return dtos;
        for (ServiceEntity entity : entities) {
            RepairServiceDto dto = new RepairServiceDto();
            dtos.add(dto);
        }
        return dtos;
    }
}
