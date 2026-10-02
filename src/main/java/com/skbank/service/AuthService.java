package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.Admin;
import com.skbank.model.Customer;
import com.skbank.model.User;

public interface AuthService {
    User authenticateCustomer(String username, String password) throws BankException;
    User authenticateAdmin(String username, String password) throws BankException;
    Customer registerCustomer(Customer customer, String username, String plainPassword, Long branchId, Long accountTypeId) throws BankException;
    Admin getAdminByUserId(Long userId) throws BankException;
    Customer getCustomerByUserId(Long userId) throws BankException;
    boolean changePassword(Long userId, String oldPassword, String newPassword) throws BankException;
}
