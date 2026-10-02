package com.skbank.service.provider;

import java.math.BigDecimal;
import java.util.UUID;

public class DemoPaymentProvider {

    public static String processRecharge(String operator, String mobileNumber, BigDecimal amount) {
        // Internal Demo Recharge Processor
        return "RCH" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    public static String processBillPayment(String providerCode, String consumerNumber, BigDecimal amount) {
        // Internal Demo Bill Payment Processor
        return "BILL" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    public static String processFastagRecharge(String vehicleNumber, BigDecimal amount) {
        // Internal Demo FASTag Processor
        return "FTAG" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
