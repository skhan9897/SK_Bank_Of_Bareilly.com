package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.dto.CustomerDashboardDTO;
import com.skbank.exception.BankException;
import com.skbank.model.Account;
import com.skbank.model.Customer;
import com.skbank.model.Kyc;
import com.skbank.service.CustomerService;

import java.math.BigDecimal;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final LoanDAO loanDAO = new LoanDAOImpl();
    private final FixedDepositDAO fdDAO = new FixedDepositDAOImpl();
    private final KycDAO kycDAO = new KycDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    @Override
    public Customer getCustomerById(String customerId) throws BankException {
        try {
            return customerDAO.findById(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching customer", e);
        }
    }

    @Override
    public boolean updateProfile(Customer customer) throws BankException {
        try {
            return customerDAO.update(customer);
        } catch (Exception e) {
            throw new BankException("Error updating customer profile", e);
        }
    }

    @Override
    public boolean updateProfileImage(String customerId, String imagePath) throws BankException {
        try {
            return customerDAO.updateProfileImage(customerId, imagePath);
        } catch (Exception e) {
            throw new BankException("Error updating profile image", e);
        }
    }

    @Override
    public CustomerDashboardDTO getCustomerDashboardData(String customerId, Long userId) throws BankException {
        try {
            CustomerDashboardDTO dto = new CustomerDashboardDTO();

            List<Account> accounts = accountDAO.findByCustomerId(customerId);
            BigDecimal totalBal = BigDecimal.ZERO;
            BigDecimal availBal = BigDecimal.ZERO;

            for (Account acc : accounts) {
                if (acc.getBalance() != null) totalBal = totalBal.add(acc.getBalance());
                if (acc.getAvailableBalance() != null) availBal = availBal.add(acc.getAvailableBalance());
            }

            dto.setTotalBalance(totalBal);
            dto.setAvailableBalance(availBal);
            dto.setTotalAccountsCount(accounts.size());

            dto.setActiveLoansCount(loanDAO.countActiveLoansByCustomerId(customerId));
            dto.setLoanOutstandingAmount(loanDAO.getTotalOutstandingByCustomerId(customerId));
            dto.setFdInvestmentAmount(fdDAO.getTotalFdInvestmentByCustomerId(customerId));

            Customer customer = customerDAO.findById(customerId);
            if (customer != null && customer.getKycStatus() != null) {
                dto.setKycStatus(customer.getKycStatus().name());
            }

            if (userId != null) {
                dto.setUnreadNotificationsCount(notificationDAO.countUnreadByUserId(userId));
            }

            return dto;
        } catch (Exception e) {
            throw new BankException("Error loading dashboard data", e);
        }
    }

    @Override
    public Kyc getKycByCustomerId(String customerId) throws BankException {
        try {
            return kycDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching KYC data", e);
        }
    }
}
