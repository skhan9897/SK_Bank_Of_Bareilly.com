package com.skbank.util;

import com.skbank.model.User;
import com.skbank.service.AuthenticationService;

public class TestLogin {

    public static void main(String[] args) {
        AuthenticationService service = new AuthenticationService();
        try {
            User admin1 = service.authenticate("admin", "Admin@123");
            if (admin1 != null) {
                System.out.println("ADMIN 1 (admin / Admin@123) VERIFIED SUCCESSFUL! Role: " + admin1.getRole());
            } else {
                System.err.println("ADMIN 1 LOGIN FAILED!");
            }

            User admin2 = service.authenticate("SKBOB9897", "Admin9897");
            if (admin2 != null) {
                System.out.println("ADMIN 2 (SKBOB9897 / Admin9897) VERIFIED SUCCESSFUL! Role: " + admin2.getRole());
            } else {
                System.err.println("ADMIN 2 LOGIN FAILED!");
            }

            User customer = service.authenticate("rajesh123", "Customer@123");
            if (customer != null) {
                System.out.println("CUSTOMER (rajesh123 / Customer@123) VERIFIED SUCCESSFUL! Role: " + customer.getRole());
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
