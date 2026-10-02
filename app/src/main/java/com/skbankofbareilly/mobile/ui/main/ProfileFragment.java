package com.skbankofbareilly.mobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.skbankofbareilly.mobile.databinding.FragmentProfileBinding;
import com.skbankofbareilly.mobile.security.BiometricAuthManager;
import com.skbankofbareilly.mobile.security.TokenManager;
import com.skbankofbareilly.mobile.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TokenManager tokenManager = TokenManager.getInstance(requireContext());
        binding.tvProfileName.setText(tokenManager.getCustomerName());

        binding.switchBiometric.setChecked(tokenManager.isBiometricEnabled());
        binding.switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !BiometricAuthManager.isBiometricAvailable(requireContext())) {
                Toast.makeText(requireContext(), "Biometric sensor not available on this device", Toast.LENGTH_SHORT).show();
                binding.switchBiometric.setChecked(false);
                return;
            }
            tokenManager.setBiometricEnabled(isChecked);
            Toast.makeText(requireContext(), "Biometric login " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        binding.btnLogout.setOnClickListener(v -> {
            tokenManager.clearSession();
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
