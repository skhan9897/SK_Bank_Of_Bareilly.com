package com.skbank.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TransactionIdGenerator {

    public static String generateTransactionId() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(dtf);
        Random random = new Random();
        int seq = 100 + random.nextInt(900);
        return "SKTXN" + timestamp + seq;
    }

    public static String generateReferenceNumber() {
        Random random = new Random();
        long ref = 1000000000L + (long)(random.nextDouble() * 9000000000L);
        return "REF" + ref;
    }
}
