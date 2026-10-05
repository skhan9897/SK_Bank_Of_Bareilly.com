package com.skbank.service.impl;

import com.skbank.dao.ComplaintDAO;
import com.skbank.dao.CustomerDAO;
import com.skbank.dao.impl.ComplaintDAOImpl;
import com.skbank.dao.impl.CustomerDAOImpl;
import com.skbank.exception.BankException;
import com.skbank.model.*;
import com.skbank.service.ComplaintService;

import java.util.List;

public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();

    @Override
    public Complaint createComplaint(String customerId, String subject, String description, String priority) throws BankException {
        if (subject == null || subject.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            throw new BankException("Subject and description are required");
        }
        try {
            Complaint c = new Complaint();
            c.setCustomerId(customerId);
            c.setSubject(subject);
            c.setDescription(description);
            c.setPriority(ComplaintPriority.valueOf(priority != null ? priority : "MEDIUM"));
            c.setStatus(ComplaintStatus.OPEN);

            Long id = complaintDAO.create(c);
            c.setComplaintId(id);

            Customer cust = customerDAO.findById(customerId);
            if (cust != null) {
                // Add initial message
                ComplaintMessage msg = new ComplaintMessage();
                msg.setComplaintId(id);
                msg.setSenderUserId(cust.getUserId());
                msg.setMessage(description);
                complaintDAO.addMessage(msg);
            }

            return c;
        } catch (Exception e) {
            throw new BankException("Error logging complaint: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Complaint> getCustomerComplaints(String customerId) throws BankException {
        try {
            return complaintDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching complaints", e);
        }
    }

    @Override
    public Complaint getComplaintById(Long complaintId) throws BankException {
        try {
            Complaint c = complaintDAO.findById(complaintId);
            if (c == null) throw new BankException("Complaint not found");
            return c;
        } catch (Exception e) {
            throw new BankException("Error fetching complaint", e);
        }
    }

    @Override
    public List<ComplaintMessage> getComplaintMessages(Long complaintId) throws BankException {
        try {
            return complaintDAO.findMessagesByComplaintId(complaintId);
        } catch (Exception e) {
            throw new BankException("Error fetching complaint messages", e);
        }
    }

    @Override
    public boolean addMessage(Long complaintId, Long senderUserId, String message) throws BankException {
        if (message == null || message.trim().isEmpty()) {
            throw new BankException("Message content cannot be empty");
        }
        try {
            ComplaintMessage msg = new ComplaintMessage();
            msg.setComplaintId(complaintId);
            msg.setSenderUserId(senderUserId);
            msg.setMessage(message.trim());
            return complaintDAO.addMessage(msg) != null;
        } catch (Exception e) {
            throw new BankException("Error sending message", e);
        }
    }

    @Override
    public boolean updateStatus(Long complaintId, String status) throws BankException {
        try {
            return complaintDAO.updateStatus(complaintId, status);
        } catch (Exception e) {
            throw new BankException("Error updating complaint status", e);
        }
    }
}
