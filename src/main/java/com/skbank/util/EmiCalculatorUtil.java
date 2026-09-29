package com.skbank.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EmiCalculatorUtil {

    public static double calculateEmi(double principal, double annualInterestRate, int tenureMonths) {
        if (principal <= 0 || annualInterestRate <= 0 || tenureMonths <= 0) {
            return 0.0;
        }
        double monthlyRate = annualInterestRate / (12 * 100);
        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) / (Math.pow(1 + monthlyRate, tenureMonths) - 1);
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double calculateTotalPayment(double emi, int tenureMonths) {
        return BigDecimal.valueOf(emi * tenureMonths).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double calculateTotalInterest(double totalPayment, double principal) {
        return BigDecimal.valueOf(totalPayment - principal).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double calculateFdMaturityAmount(double principal, double annualRate, int tenureMonths) {
        double years = tenureMonths / 12.0;
        double compoundAmount = principal * Math.pow((1 + (annualRate / 100.0) / 4.0), 4.0 * years);
        return BigDecimal.valueOf(compoundAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
