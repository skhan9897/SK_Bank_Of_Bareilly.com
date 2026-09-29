package com.skbank.util;

import com.skbank.model.User;
import com.skbank.service.AuthenticationService;

public class TestLogin {

    public static void main(String[] args) {
        AuthenticationService service = new AuthenticationService();
        try {
            User admin = service.authenticate("admin", "Admin@123");
            if (admin != null) {
                System.out.println("ADMIN LOGIN VERIFIED SUCCESSFUL! Role: " + admin.getRole());
            } else {
                System.err.println("ADMIN LOGIN FAILED!");
            }

            User customer = service.authenticate("rajesh123", "Customer@123");
            if (customer != null) {
                System.out.println("CUSTOMER LOGIN VERIFIED SUCCESSFUL! Role: " + customer.getRole());
            } else {
                System.err.println("CUSTOMER LOGIN FAILED!");
            }

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
