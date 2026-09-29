package com.skbank.controller.admin;

import com.skbank.model.FixedDeposit;
import com.skbank.service.FDService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/fixed-deposits")
public class AdminFDServlet extends HttpServlet {

    private final FDService fdService = new FDService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<FixedDeposit> fds = fdService.getAllFDs();
            request.setAttribute("fixedDeposits", fds);
            request.getRequestDispatcher("/admin/fixed-deposits.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading FDs: " + e.getMessage());
            request.getRequestDispatcher("/admin/fixed-deposits.jsp").forward(request, response);
        }
    }
}
