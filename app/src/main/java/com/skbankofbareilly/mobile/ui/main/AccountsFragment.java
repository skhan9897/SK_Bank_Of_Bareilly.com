package com.skbankofbareilly.mobile.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.skbankofbareilly.mobile.databinding.FragmentAccountsBinding;
import com.skbankofbareilly.mobile.model.Account;
import com.skbankofbareilly.mobile.repository.CustomerRepository;

import java.util.List;

public class AccountsFragment extends Fragment {

    private FragmentAccountsBinding binding;
    private CustomerRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new CustomerRepository(requireContext());
        loadAccounts();
    }

    private void loadAccounts() {
        binding.progressBar.setVisibility(View.VISIBLE);

        repository.getAccounts().observe(getViewLifecycleOwner(), response -> {
            binding.progressBar.setVisibility(View.GONE);

            if (response != null && response.isSuccess() && response.getData() != null) {
                List<Account> accounts = response.getData();
                if (!accounts.isEmpty()) {
                    Account primary = accounts.get(0);
                    binding.tvAccountNumber.setText(primary.getMaskedAccountNumber());
                    binding.tvAccountType.setText(primary.getAccountTypeName());
                    binding.tvBalance.setText("₹" + primary.getBalance());
                    binding.tvAvailable.setText("₹" + primary.getAvailableBalance());
                    binding.tvIfsc.setText("IFSC: " + primary.getIfscCode());
                    binding.tvBranch.setText("Branch: " + primary.getBranchName());
                }
            } else {
                Toast.makeText(requireContext(), "Failed to load accounts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
