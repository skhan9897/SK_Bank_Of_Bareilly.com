package com.skbank.service;

import com.skbank.dto.CustomerDashboardDTO;
import com.skbank.exception.BankException;
import com.skbank.model.Customer;
import com.skbank.model.Kyc;

public interface CustomerService {
    Customer getCustomerById(Long customerId) throws BankException;
    boolean updateProfile(Customer customer) throws BankException;
    boolean updateProfileImage(Long customerId, String imagePath) throws BankException;
    CustomerDashboardDTO getCustomerDashboardData(Long customerId, Long userId) throws BankException;
    Kyc getKycByCustomerId(Long customerId) throws BankException;
}
