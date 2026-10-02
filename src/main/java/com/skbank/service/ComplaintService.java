package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.Complaint;
import com.skbank.model.ComplaintMessage;

import java.util.List;

public interface ComplaintService {
    Complaint createComplaint(Long customerId, String subject, String description, String priority) throws BankException;
    List<Complaint> getCustomerComplaints(Long customerId) throws BankException;
    Complaint getComplaintById(Long complaintId) throws BankException;
    List<ComplaintMessage> getComplaintMessages(Long complaintId) throws BankException;
    boolean addMessage(Long complaintId, Long senderUserId, String message) throws BankException;
    boolean updateStatus(Long complaintId, String status) throws BankException;
}
