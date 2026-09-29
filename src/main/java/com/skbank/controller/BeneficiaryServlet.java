package com.skbank.controller;

import com.skbank.dao.BeneficiaryDAO;
import com.skbank.model.Beneficiary;
import com.skbank.model.Customer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/beneficiaries")
public class BeneficiaryServlet extends HttpServlet {

    private final BeneficiaryDAO beneficiaryDAO = new BeneficiaryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer customer = (session != null) ? (Customer) session.getAttribute("customerProfile") : null;

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<Beneficiary> beneficiaries = beneficiaryDAO.findByCustomerId(customer.getCustomerId());
            request.setAttribute("beneficiaries", beneficiaries);
            request.getRequestDispatcher("/beneficiaries.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading beneficiaries: " + e.getMessage());
            request.getRequestDispatcher("/beneficiaries.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer customer = (session != null) ? (Customer) session.getAttribute("customerProfile") : null;

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("add".equalsIgnoreCase(action)) {
                String name = request.getParameter("name");
                String accountNumber = request.getParameter("accountNumber");
                String ifsc = request.getParameter("ifsc");
                String bankName = request.getParameter("bankName");
                String nickname = request.getParameter("nickname");

                Beneficiary b = new Beneficiary();
                b.setCustomerId(customer.getCustomerId());
                b.setName(name);
                b.setAccountNumber(accountNumber);
                b.setIfsc(ifsc);
                b.setBankName(bankName);
                b.setNickname(nickname);
                b.setStatus("ACTIVE");

                beneficiaryDAO.addBeneficiary(b);
                request.setAttribute("successMessage", "Beneficiary added successfully.");

            } else if ("delete".equalsIgnoreCase(action)) {
                int beneficiaryId = Integer.parseInt(request.getParameter("beneficiaryId"));
                beneficiaryDAO.deleteBeneficiary(beneficiaryId, customer.getCustomerId());
                request.setAttribute("successMessage", "Beneficiary removed.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
