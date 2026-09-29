package com.skbank.service;

import com.skbank.dao.ComplaintDAO;
import com.skbank.model.Complaint;
import com.skbank.util.AccountNumberGenerator;

import java.sql.SQLException;
import java.util.List;

public class ComplaintService {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    public boolean submitComplaint(Complaint complaint) throws SQLException {
        complaint.setComplaintId(AccountNumberGenerator.generateComplaintId());
        complaint.setStatus("OPEN");
        return complaintDAO.createComplaint(complaint);
    }

    public List<Complaint> getCustomerComplaints(String customerId) throws SQLException {
        return complaintDAO.findByCustomerId(customerId);
    }

    public List<Complaint> getAllComplaints() throws SQLException {
        return complaintDAO.findAll();
    }

    public boolean updateComplaintStatus(String complaintId, String status) throws SQLException {
        return complaintDAO.updateComplaintStatus(complaintId, status);
    }
}
