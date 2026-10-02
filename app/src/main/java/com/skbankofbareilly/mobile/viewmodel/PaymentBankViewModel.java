package com.skbankofbareilly.mobile.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.skbankofbareilly.mobile.model.*;
import com.skbankofbareilly.mobile.repository.PaymentBankRepository;

import java.util.List;

public class PaymentBankViewModel extends AndroidViewModel {

    private final PaymentBankRepository repository;

    public PaymentBankViewModel(@NonNull Application application) {
        super(application);
        repository = new PaymentBankRepository(application);
    }

    public LiveData<ApiResponse<PaymentWallet>> getWallet() {
        return repository.getWallet();
    }

    public LiveData<ApiResponse<PaymentTransaction>> processPayment(PaymentRequestDTO request) {
        return repository.processPayment(request);
    }

    public LiveData<ApiResponse<List<PaymentTransaction>>> getPaymentHistory(int page) {
        return repository.getPaymentHistory(page);
    }

    public LiveData<ApiResponse<List<PaymentProvider>>> getProviders(String type) {
        return repository.getProviders(type);
    }
}
