package com.skbank.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class AccountNumberGenerator {

    private static final String BANK_PREFIX = "SKB";
    private static final String BRANCH_CODE = "2401"; // Bareilly Main Branch

    public static String generateAccountNumber() {
        Random random = new Random();
        long number = 10000000L + random.nextInt(90000000);
        return BANK_PREFIX + BRANCH_CODE + number;
    }

    public static String generateCustomerId() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);
        return "SKC" + number;
    }

    public static String generateFdReceiptNumber() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateStr = LocalDateTime.now().format(dtf);
        Random random = new Random();
        int seq = 100 + random.nextInt(900);
        return "SKFD" + dateStr + seq;
    }

    public static String generateComplaintId() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateStr = LocalDateTime.now().format(dtf);
        Random random = new Random();
        int seq = 100 + random.nextInt(900);
        return "SKCMP" + dateStr + seq;
    }
}
