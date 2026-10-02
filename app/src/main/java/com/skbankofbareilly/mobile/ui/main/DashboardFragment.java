package com.skbankofbareilly.mobile.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.skbankofbareilly.mobile.R;
import com.skbankofbareilly.mobile.databinding.FragmentDashboardBinding;
import com.skbankofbareilly.mobile.security.TokenManager;
import com.skbankofbareilly.mobile.ui.banking.*;
import com.skbankofbareilly.mobile.viewmodel.DashboardViewModel;

import java.util.Map;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding.tvCustomerName.setText("Hello, " + TokenManager.getInstance(requireContext()).getCustomerName());

        setupQuickActions();
        loadDashboardData();
    }

    private void setupQuickActions() {
        binding.btnQuickSend.setOnClickListener(v -> ((MainActivity) requireActivity()).loadFragment(new SendMoneyFragment()));
        binding.btnQuickWithdraw.setOnClickListener(v -> ((MainActivity) requireActivity()).loadFragment(new WithdrawFragment()));
        binding.btnQuickUpi.setOnClickListener(v -> ((MainActivity) requireActivity()).loadFragment(new UpiFragment()));
        binding.btnQuickFd.setOnClickListener(v -> ((MainActivity) requireActivity()).loadFragment(new FixedDepositFragment()));
        binding.btnQuickLoans.setOnClickListener(v -> ((MainActivity) requireActivity()).loadFragment(new LoansFragment()));
        binding.btnQuickBills.setOnClickListener(v -> ((MainActivity) requireActivity()).loadFragment(new BillPaymentFragment()));
    }

    private void loadDashboardData() {
        binding.progressBar.setVisibility(View.VISIBLE);

        viewModel.getDashboardData().observe(getViewLifecycleOwner(), response -> {
            binding.progressBar.setVisibility(View.GONE);

            if (response != null && response.isSuccess() && response.getData() != null) {
                Map<String, Object> data = response.getData();
                if (data.containsKey("stats")) {
                    Map<String, Object> stats = (Map<String, Object>) data.get("stats");
                    if (stats != null) {
                        binding.tvTotalBalance.setText("₹" + String.format("%.2f", ((Number) stats.get("totalBalance")).doubleValue()));
                        binding.tvAvailableBalance.setText("₹" + String.format("%.2f", ((Number) stats.get("availableBalance")).doubleValue()));
                        binding.tvFdInvestment.setText("₹" + String.format("%.2f", ((Number) stats.get("fdInvestmentAmount")).doubleValue()));
                        binding.tvActiveLoans.setText("₹" + String.format("%.2f", ((Number) stats.get("loanOutstandingAmount")).doubleValue()));
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Error loading dashboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
