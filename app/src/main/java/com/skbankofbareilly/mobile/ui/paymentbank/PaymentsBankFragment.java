package com.skbankofbareilly.mobile.ui.paymentbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.skbankofbareilly.mobile.databinding.FragmentPaymentsBankBinding;
import com.skbankofbareilly.mobile.viewmodel.PaymentBankViewModel;

public class PaymentsBankFragment extends Fragment {

    private FragmentPaymentsBankBinding binding;
    private PaymentBankViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentsBankBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PaymentBankViewModel.class);

        setupClickListeners();
        loadWalletBalance();
    }

    private void setupClickListeners() {
        binding.btnScanQr.setOnClickListener(v -> startActivity(new Intent(requireContext(), QRScannerActivity.class)));
        binding.btnReceiveMoney.setOnClickListener(v -> Toast.makeText(requireContext(), "Your UPI QR Code is ready to receive payments", Toast.LENGTH_SHORT).show());
        binding.btnMobileRecharge.setOnClickListener(v -> Toast.makeText(requireContext(), "Mobile Recharge: Enter 10-digit number & select plan", Toast.LENGTH_SHORT).show());
        binding.btnDth.setOnClickListener(v -> Toast.makeText(requireContext(), "DTH Recharge: Select provider and enter subscriber ID", Toast.LENGTH_SHORT).show());
        binding.btnElectricity.setOnClickListener(v -> Toast.makeText(requireContext(), "Electricity Bill: Select state provider & consumer number", Toast.LENGTH_SHORT).show());
        binding.btnFastag.setOnClickListener(v -> Toast.makeText(requireContext(), "FASTag Recharge: Enter vehicle registration number", Toast.LENGTH_SHORT).show());
    }

    private void loadWalletBalance() {
        viewModel.getWallet().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess() && response.getData() != null) {
                binding.tvWalletBalance.setText("₹" + String.format("%.2f", response.getData().getBalance()));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
