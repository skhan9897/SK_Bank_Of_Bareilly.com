package com.skbankofbareilly.mobile.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.skbankofbareilly.mobile.model.*;
import com.skbankofbareilly.mobile.network.ApiClient;
import com.skbankofbareilly.mobile.network.ApiService;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerRepository {

    private final ApiService apiService;

    public CustomerRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    public LiveData<ApiResponse<Map<String, Object>>> getDashboardData() {
        MutableLiveData<ApiResponse<Map<String, Object>>> liveData = new MutableLiveData<>();
        apiService.getDashboard().enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(ApiResponse.error("Failed to load dashboard", "SERVER_ERROR"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                liveData.setValue(ApiResponse.error("Network error: " + t.getMessage(), "NETWORK_ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<ApiResponse<List<Account>>> getAccounts() {
        MutableLiveData<ApiResponse<List<Account>>> liveData = new MutableLiveData<>();
        apiService.getAccounts().enqueue(new Callback<ApiResponse<List<Account>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Account>>> call, Response<ApiResponse<List<Account>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(ApiResponse.error("Failed to load accounts", "SERVER_ERROR"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Account>>> call, Throwable t) {
                liveData.setValue(ApiResponse.error("Network error: " + t.getMessage(), "NETWORK_ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<ApiResponse<List<Card>>> getCards() {
        MutableLiveData<ApiResponse<List<Card>>> liveData = new MutableLiveData<>();
        apiService.getCards().enqueue(new Callback<ApiResponse<List<Card>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Card>>> call, Response<ApiResponse<List<Card>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(ApiResponse.error("Failed to load cards", "SERVER_ERROR"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Card>>> call, Throwable t) {
                liveData.setValue(ApiResponse.error("Network error: " + t.getMessage(), "NETWORK_ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<ApiResponse<Kyc>> getKyc() {
        MutableLiveData<ApiResponse<Kyc>> liveData = new MutableLiveData<>();
        apiService.getKyc().enqueue(new Callback<ApiResponse<Kyc>>() {
            @Override
            public void onResponse(Call<ApiResponse<Kyc>> call, Response<ApiResponse<Kyc>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(ApiResponse.error("Failed to load KYC", "SERVER_ERROR"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Kyc>> call, Throwable t) {
                liveData.setValue(ApiResponse.error("Network error: " + t.getMessage(), "NETWORK_ERROR"));
            }
        });
        return liveData;
    }
}
