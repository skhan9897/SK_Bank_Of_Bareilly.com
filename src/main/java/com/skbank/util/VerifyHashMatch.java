package com.skbank.util;

public class VerifyHashMatch {

    public static void main(String[] args) {
        String hash = "$2a$10$282JYUVCbBPv28NiHy7RKOzL16828tRAt0az7f37QzXjAi7ExZumi";

        boolean match1 = PasswordUtil.checkPassword("Admin@123", hash);
        boolean match2 = PasswordUtil.checkPassword("Admin9897", hash);

        System.out.println("Check 'Admin@123' against hash: " + match1);
        System.out.println("Check 'Admin9897' against hash: " + match2);

        System.exit(0);
    }
}
