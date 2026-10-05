package com.skbank.controller.customer;

import com.skbank.model.Beneficiary;
import com.skbank.service.BeneficiaryService;
import com.skbank.service.impl.BeneficiaryServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/beneficiaries"})
public class BeneficiaryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final BeneficiaryService beneficiaryService = new BeneficiaryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String customerId = (String) session.getAttribute("CUSTOMER_ID");

        String action = request.getParameter("action");
        if ("delete".equalsIgnoreCase(action)) {
            try {
                Long benId = Long.parseLong(request.getParameter("id"));
                beneficiaryService.deleteBeneficiary(benId, customerId);
                response.sendRedirect(request.getContextPath() + "/customer/beneficiaries?msg=Beneficiary deleted successfully.");
                return;
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error deleting beneficiary: " + e.getMessage());
            }
        }

        try {
            List<Beneficiary> beneficiaries = beneficiaryService.getBeneficiaries(customerId);
            request.setAttribute("beneficiaries", beneficiaries);
            request.getRequestDispatcher("/WEB-INF/views/customer/beneficiaries.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading beneficiaries: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/beneficiaries.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String customerId = (String) session.getAttribute("CUSTOMER_ID");

        try {
            Beneficiary b = new Beneficiary();
            b.setCustomerId(customerId);
            b.setBeneficiaryName(request.getParameter("beneficiaryName"));
            b.setAccountNumber(request.getParameter("accountNumber"));
            b.setIfscCode(request.getParameter("ifscCode"));
            b.setBankName(request.getParameter("bankName"));
            b.setNickname(request.getParameter("nickname"));

            beneficiaryService.addBeneficiary(b);

            response.sendRedirect(request.getContextPath() + "/customer/beneficiaries?msg=Beneficiary added successfully.");
        } catch (Exception e) {
            try {
                request.setAttribute("beneficiaries", beneficiaryService.getBeneficiaries(customerId));
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "Failed to add beneficiary: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/beneficiaries.jsp").forward(request, response);
        }
    }
}
