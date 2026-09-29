package com.skbank.util;

import com.skbank.model.Customer;
import com.skbank.model.User;
import com.skbank.service.AuthenticationService;

import java.sql.Date;
import java.util.Random;

public class TestRegistration {

    public static void main(String[] args) {
        AuthenticationService service = new AuthenticationService();
        Random r = new Random();
        int seq = 1000 + r.nextInt(9000);

        try {
            User user = new User();
            user.setUsername("testuser" + seq);
            user.setPasswordHash("Password@123");

            Customer customer = new Customer();
            customer.setFirstName("Test");
            customer.setLastName("User");
            customer.setDob(Date.valueOf("1995-01-01"));
            customer.setGender("Male");
            customer.setMobile("98" + seq + "12345");
            customer.setEmail("test" + seq + "@example.com");
            customer.setAadhaar("12345678" + seq);
            customer.setPan("ABCDE" + seq + "F");
            customer.setAddress("123 Civil Lines");
            customer.setCity("Bareilly");
            customer.setState("Uttar Pradesh");
            customer.setPincode("243001");
            customer.setOccupation("Engineer");

            Customer registered = service.registerCustomer(user, customer);
            System.out.println("CUSTOMER REGISTRATION TEST PASSED! Customer ID: " + registered.getCustomerId());
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
