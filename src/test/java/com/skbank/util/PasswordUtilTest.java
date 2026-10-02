package com.skbank.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testPasswordHashingAndVerification() {
        String rawPass = "BankSecret@123";
        String hashed = PasswordUtil.hashPassword(rawPass);

        assertNotNull(hashed);
        assertNotEquals(rawPass, hashed);
        assertTrue(PasswordUtil.checkPassword(rawPass, hashed));
        assertFalse(PasswordUtil.checkPassword("WrongPass", hashed));
    }

    @Test
    public void testValidationUtil() {
        assertTrue(ValidationUtil.isValidMobile("9876543210"));
        assertFalse(ValidationUtil.isValidMobile("12345"));

        assertTrue(ValidationUtil.isValidEmail("user@skbank.com"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));

        assertTrue(ValidationUtil.isValidAadhaar("123456789012"));
        assertFalse(ValidationUtil.isValidAadhaar("123"));

        assertTrue(ValidationUtil.isValidPan("ABCDE1234F"));
        assertFalse(ValidationUtil.isValidPan("INVALIDPAN"));
    }
}
