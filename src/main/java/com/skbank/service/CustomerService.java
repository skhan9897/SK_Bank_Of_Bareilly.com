package com.skbank.service;

import com.skbank.dto.CustomerDashboardDTO;
import com.skbank.exception.BankException;
import com.skbank.model.Customer;
import com.skbank.model.Kyc;

public interface CustomerService {
    Customer getCustomerById(String customerId) throws BankException;
    boolean updateProfile(Customer customer) throws BankException;
    boolean updateProfileImage(String customerId, String imagePath) throws BankException;
    CustomerDashboardDTO getCustomerDashboardData(String customerId, Long userId) throws BankException;
    Kyc getKycByCustomerId(String customerId) throws BankException;
}
