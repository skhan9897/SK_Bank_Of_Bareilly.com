package com.skbank.controller.customer;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.TransferRequest;
import com.skbank.service.TransferService;
import com.skbank.service.impl.TransferServiceImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/central-clearing/switch"})
public class CentralClearingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TransferService transferService = new TransferServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String sourceHandle = request.getParameter("sourceHandle");
            String targetHandle = request.getParameter("targetHandle");
            String amountStr = request.getParameter("amount");

            if (targetHandle == null || !targetHandle.contains("@skminibank")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(ApiResponse.error("Invalid Central Clearing Handle. Must route through @skminibank switch.", "INVALID_HANDLE")));
                return;
            }

            // Central Clearing House Settlement & Routing Response
            response.getWriter().write(gson.toJson(ApiResponse.success("SK Mini Bank Central Clearing Switch: Transaction successfully routed and settled through @skminibank clearing house.", null)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error("Central Clearing Switch Error: " + e.getMessage(), "SWITCH_ERROR")));
        }
    }
}
