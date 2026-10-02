package com.skbankofbareilly.mobile.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.skbankofbareilly.mobile.model.ApiResponse;
import com.skbankofbareilly.mobile.model.Customer;
import com.skbankofbareilly.mobile.repository.CustomerRepository;

import java.util.Map;

public class DashboardViewModel extends AndroidViewModel {

    private final CustomerRepository repository;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new CustomerRepository(application);
    }

    public LiveData<ApiResponse<Map<String, Object>>> getDashboardData() {
        return repository.getDashboardData();
    }
}
