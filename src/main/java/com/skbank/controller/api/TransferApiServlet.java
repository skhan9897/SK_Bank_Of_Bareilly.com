package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.dto.TransferDTO;
import com.skbank.model.TransferRequest;
import com.skbank.service.TransferService;
import com.skbank.service.impl.TransferServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/transfer"})
public class TransferApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TransferService transferService = new TransferServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String senderCustomerId = (String) request.getAttribute("API_CUSTOMER_ID");

        try {
            BufferedReader reader = request.getReader();
            TransferDTO dto = gson.fromJson(reader, TransferDTO.class);

            TransferRequest tr = transferService.processTransfer(dto, senderCustomerId);

            response.getWriter().write(gson.toJson(ApiResponse.success("Transfer completed successfully", tr)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "TRANSFER_FAILED")));
        }
    }
}
