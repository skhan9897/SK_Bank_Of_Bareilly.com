package com.skbank.service;

import com.skbank.dto.EmiCalculatorDTO;
import com.skbank.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class LoanServiceImplTest {

    @Test
    public void testEmiCalculation() {
        LoanService loanService = new LoanServiceImpl();
        BigDecimal principal = new BigDecimal("100000.00");
        BigDecimal annualRate = new BigDecimal("12.00");
        int tenureMonths = 12;

        EmiCalculatorDTO dto = loanService.calculateEmi(principal, annualRate, tenureMonths);

        assertNotNull(dto);
        assertNotNull(dto.getEmiAmount());
        assertTrue(dto.getEmiAmount().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(dto.getTotalAmountPayable().compareTo(principal) > 0);
    }
}
