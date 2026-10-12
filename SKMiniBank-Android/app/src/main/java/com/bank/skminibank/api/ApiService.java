package com.bank.skminibank.api;

import com.bank.skminibank.model.AccountResponse;
import com.bank.skminibank.model.AtmStatusResponse;
import com.bank.skminibank.model.DashboardResponse;
import com.bank.skminibank.model.GenericResponse;
import com.bank.skminibank.model.LoanResponse;
import com.bank.skminibank.model.LoginResponse;
import com.bank.skminibank.model.MyRequestsResponse;
import com.bank.skminibank.model.ProfileResponse;
import com.bank.skminibank.model.TransactionResponse;
import com.bank.skminibank.model.UpiResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("api/login")
    Call<LoginResponse> login(
            @Field("mobile") String mobile,
            @Field("password") String password,
            @Field("otp") String otp
    );

    @FormUrlEncoded
    @POST("api/sendOtp")
    Call<GenericResponse> sendOtp(
            @Field("mobile") String mobile
    );

    @FormUrlEncoded
    @POST("api/dashboard")
    Call<DashboardResponse> getDashboardData(
            @Field("customerId") int customerId
    );

    @FormUrlEncoded
    @POST("api/profile")
    Call<ProfileResponse> getProfileData(
            @Field("customerId") int customerId
    );

    @GET("api/myRequests")
    Call<MyRequestsResponse> getMyRequests(
            @Query("customerId") int customerId
    );

    @FormUrlEncoded
    @POST("api/submitRequest")
    Call<LoginResponse> submitServiceRequest(
            @Field("customerId") int customerId,
            @Field("requestType") String requestType
    );

    @POST("api/logout")
    Call<LoginResponse> logout();

    @FormUrlEncoded
    @POST("api/updateAccountStatus")
    Call<LoginResponse> updateAccountStatus(
            @Field("customerId") int customerId,
            @Field("status") String status
    );

    @FormUrlEncoded
    @POST("api/transfer")
    Call<LoginResponse> transferAmount(
            @Field("fromAccount") String fromAccount,
            @Field("toAccount") String toAccount,
            @Field("amount") double amount,
            @Field("description") String description,
            @Field("transactionPin") String pin
    );

    @FormUrlEncoded
    @POST("api/account")
    Call<AccountResponse> getAccountByNumber(
            @Field("accountNumber") String accountNumber
    );

    @FormUrlEncoded
    @POST("api/generateUpi")
    Call<UpiResponse> generateUpi(
            @Field("customerId") int customerId,
            @Field("accountNumber") String accountNumber
    );

    @FormUrlEncoded
    @POST("api/upi")
    Call<UpiResponse> getUpiDetails(
            @Field("accountNumber") String accountNumber
    );

    @FormUrlEncoded
    @POST("api/setUpiPin")
    Call<LoginResponse> setUpiPin(
            @Field("accountNumber") String accountNumber,
            @Field("transactionPin") String transactionPin,
            @Field("upiPin") String upiPin,
            @Field("otp") String otp
    );

    @FormUrlEncoded
    @POST("api/upiPayment")
    Call<LoginResponse> performUpiPayment(
            @Field("fromAccount") String fromAccount,
            @Field("toUpiId") String toUpiId,
            @Field("upiPin") String upiPin,
            @Field("amount") double amount,
            @Field("remarks") String remarks
    );

    @FormUrlEncoded
    @POST("api/atm/apply")
    Call<GenericResponse> applyATM(
            @Field("customerId") int customerId,
            @Field("accountNumber") String accountNumber,
            @Field("cardType") String cardType
    );

    @GET("api/atm/status")
    Call<AtmStatusResponse> getATMStatus(
            @Query("customerId") int customerId
    );

    @FormUrlEncoded
    @POST("api/chequebook/apply")
    Call<GenericResponse> applyChequeBook(
            @Field("customerId") int customerId,
            @Field("accountNumber") String accountNumber,
            @Field("chequeType") String chequeType
    );

    @GET("api/chequebook/status")
    Call<com.bank.skminibank.model.ChequeBookResponse> getChequeBookStatus(
            @Query("customerId") int customerId
    );

    @FormUrlEncoded
    @POST("api/loan/apply")
    Call<GenericResponse> applyLoan(
            @Field("customerId") int customerId,
            @Field("accountNumber") String accountNumber,
            @Field("loanType") String loanType,
            @Field("loanAmount") double amount,
            @Field("tenureMonths") int tenure,
            @Field("monthlyIncome") double income,
            @Field("purpose") String purpose
    );

    @GET("api/loan/status")
    Call<LoanResponse> getLoanStatus(
            @Query("customerId") int customerId
    );

    @FormUrlEncoded
    @POST("api/netbanking/apply")
    Call<GenericResponse> applyNetBanking(
            @Field("customerId") int customerId,
            @Field("accountNumber") String accountNumber
    );

    @GET("api/netbanking/status")
    Call<GenericResponse> getNetBankingStatus(
            @Query("customerId") int customerId
    );

    @FormUrlEncoded
    @POST("api/mobilebanking/apply")
    Call<GenericResponse> applyMobileBanking(
            @Field("customerId") int customerId,
            @Field("accountNumber") String accountNumber
    );

    @GET("api/mobilebanking/status")
    Call<GenericResponse> getMobileBankingStatus(
            @Query("customerId") int customerId
    );

    @FormUrlEncoded
    @POST("api/kyc/submit")
    Call<GenericResponse> submitKYC(
            @Field("customerId") int customerId,
            @Field("accountNumber") String accountNumber,
            @Field("aadhaarNumber") String aadhaar,
            @Field("panNumber") String pan,
            @Field("aadhaarFront") String aadhaarFront,
            @Field("aadhaarBack") String aadhaarBack,
            @Field("panImage") String panImage,
            @Field("customerPhoto") String photo,
            @Field("signatureImage") String signature
    );

    @GET("api/kyc/status")
    Call<GenericResponse> getKYCStatus(
            @Query("customerId") int customerId
    );

    @FormUrlEncoded
    @POST("api/bill/electricity")
    Call<GenericResponse> payElectricityBill(
            @Field("customerId") int customerId,
            @Field("consumerNumber") String consumerNumber,
            @Field("board") String board,
            @Field("amount") double amount,
            @Field("transactionPin") String pin
    );

    @FormUrlEncoded
    @POST("api/bill/gas")
    Call<GenericResponse> payGasBill(
            @Field("customerId") int customerId,
            @Field("consumerId") String consumerId,
            @Field("provider") String provider,
            @Field("amount") double amount,
            @Field("transactionPin") String pin
    );

    @FormUrlEncoded
    @POST("api/bill/credit-card")
    Call<GenericResponse> payCreditCardBill(
            @Field("customerId") int customerId,
            @Field("cardNumber") String cardNumber,
            @Field("amount") double amount,
            @Field("transactionPin") String pin
    );

    @FormUrlEncoded
    @POST("api/admin/atm/approve")
    Call<GenericResponse> adminApproveATM(
            @Field("requestId") int requestId
    );

    @FormUrlEncoded
    @POST("api/admin/atm/reject")
    Call<GenericResponse> adminRejectATM(
            @Field("requestId") int requestId
    );

    @GET("api/transactions")
    Call<TransactionResponse> getTransactions(
            @Query("accountNumber") String accountNumber,
            @Query("customerId") int customerId
    );

    @FormUrlEncoded
    @POST("api/searchByMobile")
    Call<AccountResponse> searchByMobile(
            @Field("mobile") String mobile
    );

    @FormUrlEncoded
    @POST("api/resetPassword")
    Call<LoginResponse> resetPassword(
            @Field("accountNumber") String accountNumber,
            @Field("mobile") String mobile,
            @Field("newPassword") String newPassword
    );

    @FormUrlEncoded
    @POST("api/updateProfile")
    Call<GenericResponse> updateProfile(
            @Field("customerId") int customerId,
            @Field("mobile") String mobile,
            @Field("email") String email
    );

    @GET("api/notifications")
    Call<com.bank.skminibank.model.NotificationResponse> getNotifications(
            @Query("customerId") int customerId
    );

    @GET("api/stores")
    Call<com.bank.skminibank.model.StoreResponse> getStores();

    @FormUrlEncoded
    @POST("api/createAccount")
    Call<GenericResponse> createAccount(
            @Field("fullName") String fullName,
            @Field("fatherName") String fatherName,
            @Field("motherName") String motherName,
            @Field("dob") String dob,
            @Field("gender") String gender,
            @Field("maritalStatus") String maritalStatus,
            @Field("occupation") String occupation,
            @Field("mobile") String mobile,
            @Field("alternateMobile") String alternateMobile,
            @Field("email") String email,
            @Field("aadhaar") String aadhaar,
            @Field("pan") String pan,
            @Field("address") String address,
            @Field("city") String city,
            @Field("state") String state,
            @Field("pincode") String pincode,
            @Field("nomineeName") String nomineeName,
            @Field("relationship") String relationship,
            @Field("nomineeMobile") String nomineeMobile,
            @Field("accountType") String accountType,
            @Field("balance") double balance,
            @Field("password") String password,
            @Field("transactionPin") String transactionPin
    );
    @FormUrlEncoded
    @POST("api/wallet/transfer")
    Call<com.bank.skminibank.model.LoginResponse> transferToWallet(
            @Field("accountNumber") String accountNumber,
            @Field("walletType") String walletType,
            @Field("walletNumber") String walletNumber,
            @Field("amount") double amount
    );

    @FormUrlEncoded
    @POST("api/withdrawal")
    Call<com.bank.skminibank.model.LoginResponse> performWithdrawal(
            @Field("accountNumber") String accountNumber,
            @Field("method") String method,
            @Field("amount") double amount
    );

    @FormUrlEncoded
    @POST("api/deposit")
    Call<com.bank.skminibank.model.LoginResponse> performDeposit(
            @Field("accountNumber") String accountNumber,
            @Field("amount") double amount
    );

    @FormUrlEncoded
    @POST("api/fixed-deposit/create")
    Call<com.bank.skminibank.model.GenericResponse> createFixedDeposit(
            @Field("customerId") int customerId,
            @Field("accountNumber") String accountNumber,
            @Field("amount") double amount,
            @Field("duration") int duration
    );
}