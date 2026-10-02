package com.skbankofbareilly.mobile.network;

import com.skbankofbareilly.mobile.model.*;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface ApiService {

    // AUTH
    @POST("api/customer/auth/login")
    Call<ApiResponse<AuthResponseDTO>> login(@Body Map<String, String> body);

    @POST("api/customer/auth/verify-otp")
    Call<ApiResponse<AuthResponseDTO>> verifyOtp(@Body Map<String, String> body);

    @POST("api/customer/register")
    Call<ApiResponse<AuthResponseDTO>> register(@Body Map<String, String> body);

    @POST("api/customer/auth/logout")
    Call<ApiResponse<Void>> logout();

    // DASHBOARD & ACCOUNTS
    @GET("api/customer/dashboard")
    Call<ApiResponse<Map<String, Object>>> getDashboard();

    @GET("api/customer/accounts")
    Call<ApiResponse<List<Account>>> getAccounts();

    @GET("api/customer/account-details")
    Call<ApiResponse<Map<String, Object>>> getAccountDetails(@Query("accountId") Long accountId);

    // TRANSFER & RECIPIENT
    @GET("api/customer/recipient/mobile")
    Call<ApiResponse<RecipientLookupDTO>> lookupMobile(@Query("mobile") String mobile);

    @GET("api/customer/recipient/account")
    Call<ApiResponse<RecipientLookupDTO>> lookupAccount(@Query("accountNumber") String accountNumber);

    @GET("api/customer/recipient/upi")
    Call<ApiResponse<RecipientLookupDTO>> lookupUpi(@Query("upiAddress") String upiAddress);

    @POST("api/customer/transfer")
    Call<ApiResponse<TransferRequest>> processTransfer(@Body TransferDTO transferDTO);

    @POST("api/customer/withdraw")
    Call<ApiResponse<Transaction>> processWithdrawal(@Body Map<String, Object> body);

    @GET("api/customer/beneficiaries")
    Call<ApiResponse<List<Beneficiary>>> getBeneficiaries();

    @POST("api/customer/beneficiaries")
    Call<ApiResponse<Beneficiary>> addBeneficiary(@Body Beneficiary beneficiary);

    @DELETE("api/customer/beneficiaries")
    Call<ApiResponse<Void>> deleteBeneficiary(@Query("id") Long id);

    // TRANSACTIONS & STATEMENTS
    @GET("api/customer/transactions")
    Call<ApiResponse<Map<String, Object>>> getTransactions(
            @Query("accountId") Long accountId,
            @Query("type") String type,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("page") int page
    );

    @GET("api/customer/transaction-details")
    Call<ApiResponse<Transaction>> getTransactionDetails(@Query("ref") String ref);

    @GET("api/customer/statements")
    Call<ApiResponse<Map<String, Object>>> getStatement(
            @Query("accountId") Long accountId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    // UPI
    @GET("api/customer/upi")
    Call<ApiResponse<UpiAccount>> getUpiDetails();

    @POST("api/customer/upi")
    Call<ApiResponse<UpiAccount>> manageUpi(@Body Map<String, Object> body);

    // FIXED DEPOSITS
    @GET("api/customer/fixed-deposits")
    Call<ApiResponse<List<FixedDeposit>>> getFixedDeposits();

    @POST("api/customer/fixed-deposits")
    Call<ApiResponse<FixedDeposit>> openFd(@Body Map<String, Object> body);

    // LOANS
    @GET("api/customer/loans")
    Call<ApiResponse<List<Loan>>> getLoans();

    @GET("api/customer/loan-types")
    Call<ApiResponse<List<LoanType>>> getLoanTypes();

    @POST("api/customer/loan-apply")
    Call<ApiResponse<Loan>> applyLoan(@Body Map<String, Object> body);

    @POST("api/customer/loan-emi")
    Call<ApiResponse<LoanPayment>> payLoanEmi(@Body Map<String, Object> body);

    @POST("api/customer/loan-calculate-emi")
    Call<ApiResponse<EmiCalculatorDTO>> calculateEmi(@Body Map<String, Object> body);

    // CARDS
    @GET("api/customer/cards")
    Call<ApiResponse<List<Card>>> getCards();

    @POST("api/customer/cards")
    Call<ApiResponse<Boolean>> toggleCardStatus(@Query("cardId") Long cardId);

    // BILLS & KYC
    @GET("api/customer/bill-payments")
    Call<ApiResponse<List<BillPayment>>> getBillHistory();

    @POST("api/customer/bill-payments")
    Call<ApiResponse<BillPayment>> payBill(@Body Map<String, Object> body);

    @GET("api/customer/kyc")
    Call<ApiResponse<Kyc>> getKyc();

    // NOTIFICATIONS & COMPLAINTS
    @GET("api/customer/notifications")
    Call<ApiResponse<List<Notification>>> getNotifications();

    @POST("api/customer/notifications")
    Call<ApiResponse<Void>> markNotificationsRead(@Query("action") String action, @Query("id") Long id);

    @GET("api/customer/complaints")
    Call<ApiResponse<List<Complaint>>> getComplaints();

    @GET("api/customer/complaint-details")
    Call<ApiResponse<Map<String, Object>>> getComplaintDetails(@Query("id") Long id);

    @POST("api/customer/complaints")
    Call<ApiResponse<Void>> submitComplaintReply(@Query("action") String action, @Body Map<String, Object> body);

    // PROFILE & SECURITY
    @GET("api/customer/profile")
    Call<ApiResponse<Customer>> getProfile();

    @POST("api/customer/profile")
    Call<ApiResponse<Customer>> updateProfile(@Body Customer customer);

    @Multipart
    @POST("api/customer/profile/photo")
    Call<ApiResponse<String>> uploadProfilePhoto(@Part MultipartBody.Part photo);

    @POST("api/customer/security")
    Call<ApiResponse<Void>> changePassword(@Body Map<String, String> body);

    // PAYMENT BANK APIs
    @GET("api/payment-bank/balance")
    Call<ApiResponse<PaymentWallet>> getPaymentWallet();

    @POST("api/payment-bank/pay")
    Call<ApiResponse<PaymentTransaction>> processPaymentBankPay(
            @Header("X-Idempotency-Key") String idempotencyKey,
            @Body PaymentRequestDTO request
    );

    @GET("api/payment-bank/history")
    Call<ApiResponse<List<PaymentTransaction>>> getPaymentBankHistory(@Query("page") int page);

    @GET("api/payment-bank/providers")
    Call<ApiResponse<List<PaymentProvider>>> getPaymentProviders(@Query("type") String type);
}
