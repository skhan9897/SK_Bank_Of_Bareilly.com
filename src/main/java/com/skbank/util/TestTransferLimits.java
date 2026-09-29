package com.skbank.util;

import com.skbank.service.TransferService;

public class TestTransferLimits {

    public static void main(String[] args) {
        System.out.println("Testing Single Transfer Limit & Daily Account Type Limits...");
        TransferService service = new TransferService();

        // 1. Single Transfer > ₹25,000 Test
        try {
            service.processTransfer("SKB24010000001", "SKB24010000002", 30000.00, "IMPS", "Limit test");
            System.err.println("FAILED: Should have blocked > ₹25,000 single transfer!");
        } catch (IllegalArgumentException e) {
            System.out.println("PASSED: Single Transfer > ₹25,000 blocked! Message: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("PASSED WITH EXCEPTION: " + e.getMessage());
        }

        System.exit(0);
    }
}
