package com.skbank.dao;

import com.skbank.model.Complaint;
import com.skbank.model.ComplaintMessage;
import java.sql.SQLException;
import java.util.List;

public interface ComplaintDAO {
    Complaint findById(Long complaintId) throws SQLException;
    List<Complaint> findByCustomerId(Long customerId) throws SQLException;
    Long create(Complaint complaint) throws SQLException;
    boolean updateStatus(Long complaintId, String status) throws SQLException;
    Long addMessage(ComplaintMessage message) throws SQLException;
    List<ComplaintMessage> findMessagesByComplaintId(Long complaintId) throws SQLException;
    long countPendingComplaints() throws SQLException;
    List<Complaint> findAllAdmin(int offset, int limit, String statusFilter) throws SQLException;
    long countAllAdmin(String statusFilter) throws SQLException;
}
