package com.skbankofbareilly.mobile.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.skbankofbareilly.mobile.model.ApiResponse;
import com.skbankofbareilly.mobile.model.AuthResponseDTO;
import com.skbankofbareilly.mobile.network.ApiClient;
import com.skbankofbareilly.mobile.network.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    public LiveData<ApiResponse<AuthResponseDTO>> login(String username, String password) {
        MutableLiveData<ApiResponse<AuthResponseDTO>> liveData = new MutableLiveData<>();
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);

        apiService.login(body).enqueue(new Callback<ApiResponse<AuthResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponseDTO>> call, Response<ApiResponse<AuthResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(ApiResponse.error("Login failed: " + response.message(), "AUTH_FAILED"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponseDTO>> call, Throwable t) {
                liveData.setValue(ApiResponse.error("Network error: " + t.getMessage(), "NETWORK_ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<ApiResponse<AuthResponseDTO>> verifyOtp(String otp, String purpose) {
        MutableLiveData<ApiResponse<AuthResponseDTO>> liveData = new MutableLiveData<>();
        Map<String, String> body = new HashMap<>();
        body.put("otp", otp);
        body.put("purpose", purpose);

        apiService.verifyOtp(body).enqueue(new Callback<ApiResponse<AuthResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponseDTO>> call, Response<ApiResponse<AuthResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(ApiResponse.error("OTP verification failed", "OTP_INVALID"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponseDTO>> call, Throwable t) {
                liveData.setValue(ApiResponse.error("Network error: " + t.getMessage(), "NETWORK_ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<ApiResponse<AuthResponseDTO>> register(Map<String, String> body) {
        MutableLiveData<ApiResponse<AuthResponseDTO>> liveData = new MutableLiveData<>();

        apiService.register(body).enqueue(new Callback<ApiResponse<AuthResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponseDTO>> call, Response<ApiResponse<AuthResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(ApiResponse.error("Registration failed", "REGISTER_FAILED"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponseDTO>> call, Throwable t) {
                liveData.setValue(ApiResponse.error("Network error: " + t.getMessage(), "NETWORK_ERROR"));
            }
        });
        return liveData;
    }
}
