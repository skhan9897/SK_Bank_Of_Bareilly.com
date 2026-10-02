package com.skbankofbareilly.mobile.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.skbankofbareilly.mobile.model.*;
import com.skbankofbareilly.mobile.network.ApiClient;
import com.skbankofbareilly.mobile.network.ApiService;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentBankRepository {

    private final ApiService apiService;

    public PaymentBankRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    public LiveData<ApiResponse<PaymentWallet>> getWallet() {
        MutableLiveData<ApiResponse<PaymentWallet>> liveData = new MutableLiveData<>();
        apiService.getPaymentWallet().enqueue(new Callback<ApiResponse<PaymentWallet>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaymentWallet>> call, Response<ApiResponse<PaymentWallet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(ApiResponse.error("Failed to load wallet", "SERVER_ERROR"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaymentWallet>> call, Throwable t) {
                liveData.setValue(ApiResponse.error("Network error: " + t.getMessage(), "NETWORK_ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<ApiResponse<PaymentTransaction>> processPayment(PaymentRequestDTO request) {
        MutableLiveData<ApiResponse<PaymentTransaction>> liveData = new MutableLiveData<>();

        // Generate Idempotency Key
        String idempotencyKey = "IDEM_" + UUID.randomUUID().toString() + "_" + System.currentTimeMillis();

        apiService.processPaymentBankPay(idempotencyKey, request).enqueue(new Callback<ApiResponse<PaymentTransaction>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaymentTransaction>> call, Response<ApiResponse<PaymentTransaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(ApiResponse.error("Payment failed", "PAYMENT_FAILED"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaymentTransaction>> call, Throwable t) {
                liveData.setValue(ApiResponse.error("Network error: " + t.getMessage(), "NETWORK_ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<ApiResponse<List<PaymentTransaction>>> getPaymentHistory(int page) {
        MutableLiveData<ApiResponse<List<PaymentTransaction>>> liveData = new MutableLiveData<>();
        apiService.getPaymentBankHistory(page).enqueue(new Callback<ApiResponse<List<PaymentTransaction>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PaymentTransaction>>> call, Response<ApiResponse<List<PaymentTransaction>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(ApiResponse.error("Failed to load history", "SERVER_ERROR"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PaymentTransaction>>> call, Throwable t) {
                liveData.setValue(ApiResponse.error("Network error: " + t.getMessage(), "NETWORK_ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<ApiResponse<List<PaymentProvider>>> getProviders(String type) {
        MutableLiveData<ApiResponse<List<PaymentProvider>>> liveData = new MutableLiveData<>();
        apiService.getPaymentProviders(type).enqueue(new Callback<ApiResponse<List<PaymentProvider>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PaymentProvider>>> call, Response<ApiResponse<List<PaymentProvider>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(ApiResponse.error("Failed to load providers", "SERVER_ERROR"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PaymentProvider>>> call, Throwable t) {
                liveData.setValue(ApiResponse.error("Network error: " + t.getMessage(), "NETWORK_ERROR"));
            }
        });
        return liveData;
    }
}
