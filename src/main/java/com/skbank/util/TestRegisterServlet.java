package com.skbank.util;

import com.skbank.dao.AccountTypeDAO;
import com.skbank.dao.BranchDAO;
import com.skbank.model.AccountType;
import com.skbank.model.Branch;

import java.util.List;

public class TestRegisterServlet {

    public static void main(String[] args) {
        System.out.println("Testing AccountTypeDAO and BranchDAO on Clever Cloud DB...");
        AccountTypeDAO accountTypeDAO = new AccountTypeDAO();
        BranchDAO branchDAO = new BranchDAO();

        try {
            List<AccountType> types = accountTypeDAO.findAllActive();
            System.out.println("Found " + types.size() + " active account types.");
            for (AccountType t : types) {
                System.out.println("  - " + t.getTypeCode() + " : " + t.getTypeName() + " (ID: " + t.getTypeId() + ")");
            }

            List<Branch> branches = branchDAO.findAllActive();
            System.out.println("Found " + branches.size() + " active branches.");
            for (Branch b : branches) {
                System.out.println("  - " + b.getBranchCode() + " : " + b.getBranchName() + " (IFSC: " + b.getIfscCode() + ")");
            }

            System.out.println("TEST PASSED SUCCESSFULLY!");
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
