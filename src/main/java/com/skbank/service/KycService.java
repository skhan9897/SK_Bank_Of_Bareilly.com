package com.skbank.service;

import com.skbank.dao.CustomerDAO;
import com.skbank.dao.KycDAO;
import com.skbank.model.KycDocument;

import java.sql.SQLException;
import java.util.List;

public class KycService {

    private final KycDAO kycDAO = new KycDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public boolean uploadDocument(KycDocument doc) throws SQLException {
        return kycDAO.uploadDocument(doc);
    }

    public List<KycDocument> getCustomerKycDocuments(String customerId) throws SQLException {
        return kycDAO.findByCustomerId(customerId);
    }

    public List<KycDocument> getAllPendingKyc() throws SQLException {
        return kycDAO.findAllPending();
    }

    public boolean updateKycStatus(int kycId, String customerId, String status, String reason) throws SQLException {
        boolean updated = kycDAO.updateKycStatus(kycId, status, reason);
        if (updated) {
            customerDAO.updateKycStatus(customerId, status);
        }
        return updated;
    }
}
