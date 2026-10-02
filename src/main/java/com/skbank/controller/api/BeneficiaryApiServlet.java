package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Beneficiary;
import com.skbank.service.BeneficiaryService;
import com.skbank.service.impl.BeneficiaryServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/beneficiaries"})
public class BeneficiaryApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final BeneficiaryService beneficiaryService = new BeneficiaryServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            List<Beneficiary> beneficiaries = beneficiaryService.getBeneficiaries(customerId);
            response.getWriter().write(gson.toJson(ApiResponse.success("Beneficiaries loaded", beneficiaries)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "SERVER_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            BufferedReader reader = request.getReader();
            Beneficiary b = gson.fromJson(reader, Beneficiary.class);
            b.setCustomerId(customerId);

            Beneficiary added = beneficiaryService.addBeneficiary(b);
            response.getWriter().write(gson.toJson(ApiResponse.success("Beneficiary added successfully", added)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "BENEFICIARY_ERROR")));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String idStr = request.getParameter("id");

        try {
            Long benId = Long.parseLong(idStr);
            beneficiaryService.deleteBeneficiary(benId, customerId);
            response.getWriter().write(gson.toJson(ApiResponse.success("Beneficiary deleted", null)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "DELETE_FAILED")));
        }
    }
}
