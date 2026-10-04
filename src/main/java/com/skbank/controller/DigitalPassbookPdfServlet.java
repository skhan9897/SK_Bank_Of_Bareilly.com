package com.skbank.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.skbank.dto.DigitalPassbookDTO;
import com.skbank.model.User;
import com.skbank.service.PassbookService;
import com.skbank.service.impl.PassbookServiceImpl;

import java.io.IOException;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/passbook/pdf"})
public class DigitalPassbookPdfServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PassbookService passbookService = new PassbookServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("AUTHENTICATED_USER") : null;

        Long customerIdParam = null;
        String custIdStr = request.getParameter("customerId");
        if (custIdStr != null && !custIdStr.trim().isEmpty()) {
            try { customerIdParam = Long.parseLong(custIdStr.trim()); } catch (NumberFormatException ignored) {}
        }

        try {
            DigitalPassbookDTO passbook;
            if (user != null) {
                passbook = passbookService.getPassbookByUserId(user.getId());
            } else if (customerIdParam != null) {
                passbook = passbookService.getPassbookByCustomerId(customerIdParam);
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            response.setContentType("application/pdf");
            String filename = "SKBank_Digital_Passbook_" + passbook.getCustomerNumber() + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Brand Colors
            Color navy = new Color(7, 31, 73);
            Color gold = new Color(212, 167, 44);
            Color dark = new Color(30, 41, 59);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, navy);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, gold);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, navy);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, dark);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, dark);

            // Header Banner
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(navy);
            cell.setPadding(15);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph p1 = new Paragraph("SK BANK OF BAREILLY", titleFont);
            p1.getFont().setColor(Color.WHITE);
            p1.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p1);

            Paragraph p2 = new Paragraph("TRUST | GROWTH | TOGETHER", subtitleFont);
            p2.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p2);

            Paragraph p3 = new Paragraph("OFFICIAL DIGITAL PASSBOOK", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE));
            p3.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p3);

            headerTable.addCell(cell);
            document.add(headerTable);

            document.add(new Paragraph(" "));

            // Customer Details Section
            Paragraph sec1 = new Paragraph("CUSTOMER INFORMATION", sectionFont);
            sec1.setSpacingAfter(8);
            document.add(sec1);

            PdfPTable custTable = new PdfPTable(2);
            custTable.setWidthPercentage(100);
            custTable.setSpacingAfter(15);

            addTableRow(custTable, "Customer Name:", passbook.getCustomerName(), labelFont, valueFont);
            addTableRow(custTable, "Customer ID / Number:", passbook.getCustomerNumber(), labelFont, valueFont);
            addTableRow(custTable, "Date of Birth:", passbook.getDateOfBirth() != null ? passbook.getDateOfBirth().toString() : "-", labelFont, valueFont);
            addTableRow(custTable, "Gender:", passbook.getGender(), labelFont, valueFont);
            addTableRow(custTable, "Mobile (Masked):", passbook.getMaskedMobile(), labelFont, valueFont);
            addTableRow(custTable, "Email Address:", passbook.getEmail(), labelFont, valueFont);
            addTableRow(custTable, "Address:", passbook.getAddress() + ", " + passbook.getCity() + ", " + passbook.getState() + " - " + passbook.getPincode(), labelFont, valueFont);

            document.add(custTable);

            // Account Details Section
            Paragraph sec2 = new Paragraph("ACCOUNT INFORMATION", sectionFont);
            sec2.setSpacingAfter(8);
            document.add(sec2);

            PdfPTable accTable = new PdfPTable(2);
            accTable.setWidthPercentage(100);
            accTable.setSpacingAfter(15);

            addTableRow(accTable, "Account Number:", passbook.getMaskedAccountNumber(), labelFont, valueFont);
            addTableRow(accTable, "Account Type:", passbook.getAccountType(), labelFont, valueFont);
            addTableRow(accTable, "Branch Name:", passbook.getBranchName(), labelFont, valueFont);
            addTableRow(accTable, "IFSC Code:", passbook.getIfscCode(), labelFont, valueFont);
            addTableRow(accTable, "Account Status:", passbook.getAccountStatus(), labelFont, valueFont);
            addTableRow(accTable, "Current Balance:", "INR " + String.format("%.2f", passbook.getBalance()), labelFont, valueFont);
            addTableRow(accTable, "Available Balance:", "INR " + String.format("%.2f", passbook.getAvailableBalance()), labelFont, valueFont);

            document.add(accTable);

            // KYC Details Section
            Paragraph sec3 = new Paragraph("KYC INFORMATION", sectionFont);
            sec3.setSpacingAfter(8);
            document.add(sec3);

            PdfPTable kycTable = new PdfPTable(2);
            kycTable.setWidthPercentage(100);
            kycTable.setSpacingAfter(20);

            addTableRow(kycTable, "KYC Status:", passbook.getKycStatus(), labelFont, valueFont);
            addTableRow(kycTable, "Aadhaar (Masked):", passbook.getMaskedAadhaar(), labelFont, valueFont);
            addTableRow(kycTable, "PAN (Masked):", passbook.getMaskedPan(), labelFont, valueFont);

            document.add(kycTable);

            // Footer
            Paragraph footerText = new Paragraph("This is a computer generated digital passbook document issued by SK Bank of Bareilly. Generated on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), FontFactory.getFont(FontFactory.HELVETICA, 8, Font.ITALIC, Color.GRAY));
            footerText.setAlignment(Element.ALIGN_CENTER);
            document.add(footerText);

            document.close();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Digital passbook generation failed: " + e.getMessage());
        }
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, labelFont));
        c1.setBackgroundColor(new Color(245, 248, 252));
        c1.setPadding(6);

        PdfPCell c2 = new PdfPCell(new Phrase(value != null ? value : "-", valueFont));
        c2.setPadding(6);

        table.addCell(c1);
        table.addCell(c2);
    }
}
