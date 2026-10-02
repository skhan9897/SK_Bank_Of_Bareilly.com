package com.skbankofbareilly.mobile.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.skbankofbareilly.mobile.model.ApiResponse;
import com.skbankofbareilly.mobile.model.AuthResponseDTO;
import com.skbankofbareilly.mobile.repository.AuthRepository;

import java.util.Map;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
    }

    public LiveData<ApiResponse<AuthResponseDTO>> login(String username, String password) {
        return repository.login(username, password);
    }

    public LiveData<ApiResponse<AuthResponseDTO>> verifyOtp(String otp, String purpose) {
        return repository.verifyOtp(otp, purpose);
    }

    public LiveData<ApiResponse<AuthResponseDTO>> register(Map<String, String> body) {
        return repository.register(body);
    }
}
