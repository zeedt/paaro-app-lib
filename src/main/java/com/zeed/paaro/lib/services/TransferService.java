package com.zeed.paaro.lib.services;

import com.zeed.paaro.lib.apirequestmodel.WalletTransferRequest;
import com.zeed.paaro.lib.apiresponsemodel.WalletTransferRequestResponse;
import com.zeed.paaro.lib.enums.ApiResponseCode;
import com.zeed.paaro.lib.enums.TransactionStatus;
import com.zeed.paaro.lib.models.*;
import com.zeed.paaro.lib.repository.CurrencyRepository;
import com.zeed.paaro.lib.repository.WalletRepository;
import com.zeed.paaro.lib.repository.WalletTransferTransactionRepository;
import com.zeed.usermanagement.security.UserDetailsTokenEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TransferService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletEmailService walletEmailService;

    @Autowired
    private WalletTransferTransactionRepository walletTransferTransactionRepository;

    public WalletTransferRequestResponse logCustomerTransferRequest(WalletTransferRequest walletTransferRequest) throws IOException {

        if (walletTransferRequest == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request");
        }

        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(walletTransferRequest.getFromCurrencyType());

        if (wallet == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Wallet of the specified from currency type not found for user");
        }

        String validationMessage = getValidationRequestErrorMessage(walletTransferRequest);

        if (!StringUtils.isEmpty(validationMessage)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, validationMessage);
        }

        if (wallet.getAvailableAccountBalance().compareTo(walletTransferRequest.getTotalAmount()) < 0) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.UNABLE_TO_PROCESS, "Available balance cannot be less than total balance");
        }

        // TODO : Validate to ensure that the transaction can only be from Naira wallet to other currency account and vice versa.

        walletTransferRequest.setWallet(wallet);
        walletTransferRequest.setFromCurrency(wallet.getCurrency());

        WalletTransferTransaction walletTransferTransaction = createWalletTransactionFromTransferRequest(walletTransferRequest);

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        walletTransferTransaction.setManagedUser(userDetailsTokenEnvelope.managedUser);

        walletTransferTransactionRepository.save(walletTransferTransaction);

        BigDecimal newAvailableAccountBalance = wallet.getAvailableAccountBalance().subtract(walletTransferRequest.getTotalAmount());
        wallet.setAvailableAccountBalance(newAvailableAccountBalance);
        walletRepository.save(wallet);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setMessage("Transfer request successfully logged");
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setWallet(wallet);

        walletEmailService.sendTransferRequestEmail(walletTransferTransaction);

        return walletTransferRequestResponse;

    }


    public String getValidationRequestErrorMessage(WalletTransferRequest walletTransferRequest) {

        if (StringUtils.isEmpty(walletTransferRequest.getNarration()) ||
                StringUtils.isEmpty(walletTransferRequest.getPaaroTransactionReferenceId())  || StringUtils.isEmpty(walletTransferRequest.getToCurrencyType())
                || StringUtils.isEmpty(walletTransferRequest.getFromCurrencyType())|| StringUtils.isEmpty(walletTransferRequest.getToAccountName()) || StringUtils.isEmpty(walletTransferRequest.getToAccountNumber()) ) {

            return "Narration, paaro transaction reference id, account name, account number, from and to currency type cannot be blank.";

        }

        if (walletTransferRequest.getExchangeRate() == null || walletTransferRequest.getExchangeRate().compareTo(BigDecimal.valueOf(0)) <= 0) {
            return "Exchange rate must be greater than 0";
        }

        if (walletTransferRequest.getActualAmount() == null || walletTransferRequest.getActualAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            return  "Actual amount must be greater than 0";
        }

        if (walletTransferRequest.getTotalAmount() == null || walletTransferRequest.getTotalAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            return "Total amount must be greater than 0";
        }

        if (walletTransferRequest.getChargeAmount() == null || walletTransferRequest.getChargeAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            return "Charge amount must be greater than 0";
        }
        if (walletTransferRequest.getEquivalentAmount() == null || walletTransferRequest.getEquivalentAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            return "Equivalent amount must be greater than 0";
        }

        if (walletTransferRequest.getTotalAmount().compareTo(walletTransferRequest.getActualAmount()) <=0 ) {
            return "Total amount must be greater than equal amount";
        }

        if (walletTransferRequest.getTotalAmount().compareTo(walletTransferRequest.getChargeAmount()) <=0 ) {
            return "Total amount must be greater than charge amount";
        }

        if (walletTransferRequest.getTotalAmount().compareTo( (walletTransferRequest.getChargeAmount().add(walletTransferRequest.getActualAmount())) ) !=0 ) {
            return "Total amount must be equal to sum of charge amount and actual amount";
        }


        Currency toCurrency = currencyRepository.findCurrencyByType(walletTransferRequest.getToCurrencyType());

        if (toCurrency == null) {
            return "Currency type you are about to transfer to does not exist on the system";
        }

        walletTransferRequest.setToCurrency(toCurrency);

        return null;

    }

    public WalletTransferRequestResponse findAllTransferWalletTransactionsByUserWallet(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getCurrency())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "currency cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        if (StringUtils.isEmpty(email)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }

        List<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndFromCurrency_Type(email, walletTransferRequest.getCurrency());

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched by currency");
        walletTransferRequestResponse.setWalletTransferTransactions(transferTransactions);

        return walletTransferRequestResponse;

    }
    public WalletTransferRequestResponse findAllTransferWalletTransactionsByUserWalletPaged(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getCurrency())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "currency cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        if (StringUtils.isEmpty(email)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }

        Pageable pageable = new PageRequest(walletTransferRequest.getPageNo(), walletTransferRequest.getPageSize());

        Page<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndFromCurrency_Type(email, walletTransferRequest.getCurrency(), pageable);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched by currency");
        walletTransferRequestResponse.setWalletTransferTransactionPage(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllTransferWalletTransactionsByUserWalletAndEmail(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getCurrency()) || StringUtils.isEmpty(walletTransferRequest.getEmail())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "currency and email cannot be blank");
        }

        List<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndFromCurrency_Type(walletTransferRequest.getEmail(), walletTransferRequest.getCurrency());

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched by currency and email");
        walletTransferRequestResponse.setWalletTransferTransactions(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllTransferWalletTransactionsByUserWalletAndEmailPaged(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getCurrency()) || StringUtils.isEmpty(walletTransferRequest.getEmail())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "currency and email cannot be blank");
        }

        Pageable pageable = new PageRequest(walletTransferRequest.getPageNo(), walletTransferRequest.getPageSize());

        Page<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndFromCurrency_Type(walletTransferRequest.getEmail(), walletTransferRequest.getCurrency(), pageable);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched by currency and email");
        walletTransferRequestResponse.setWalletTransferTransactionPage(transferTransactions);

        return walletTransferRequestResponse;

    }


    public WalletTransferRequestResponse findAllTransferWalletTransactionsByEmail(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getEmail())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Email cannot be blank");
        }

        List<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_Email(walletTransferRequest.getEmail());

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched");
        walletTransferRequestResponse.setWalletTransferTransactions(transferTransactions);

        return walletTransferRequestResponse;

    }
    public WalletTransferRequestResponse findAllTransferWalletTransactionsByEmailPaged(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getEmail())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Email cannot be blank");
        }

        Pageable pageable = new PageRequest(walletTransferRequest.getPageNo(), walletTransferRequest.getPageSize(), Sort.Direction.DESC, "id" );

        Page<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_Email(walletTransferRequest.getEmail(), pageable);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched");
        walletTransferRequestResponse.setWalletTransferTransactionPage(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllTransferWalletTransactionsByEmailPagedWithFilter(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getEmail()) || StringUtils.isEmpty(walletTransferRequest.getFilter())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Email and filter cannot be blank");
        }

        Pageable pageable = new PageRequest(walletTransferRequest.getPageNo(), walletTransferRequest.getPageSize(), Sort.Direction.DESC, "id" );

        Page<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndToAccountNameLike(walletTransferRequest.getEmail(), "%"+walletTransferRequest.getFilter().trim()+"%",pageable);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched");
        walletTransferRequestResponse.setWalletTransferTransactionPage(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllCustomerLoggedTransferWalletTransactions() {


        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        if (StringUtils.isEmpty(email)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }


        List<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndTransactionStatus(email, TransactionStatus.CUSTOMER_LOGGED_REQUEST);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Customer logged transactions fetched");
        walletTransferRequestResponse.setWalletTransferTransactions(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllCustomerLoggedTransferWalletTransactionsPaged(int pageSize, int pageNo) {


        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        if (StringUtils.isEmpty(email)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }
        Pageable pageable = new PageRequest(pageNo, pageSize);

        Page<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndTransactionStatus(email, TransactionStatus.CUSTOMER_LOGGED_REQUEST, pageable);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Customer logged transactions fetched by page");
        walletTransferRequestResponse.setWalletTransferTransactionPage(transferTransactions);

        return walletTransferRequestResponse;

    }

    private WalletTransferTransaction createWalletTransactionFromTransferRequest(WalletTransferRequest walletTransferRequest) {

        WalletTransferTransaction transaction = new WalletTransferTransaction();

        transaction.setActualAmount(walletTransferRequest.getActualAmount());
        transaction.setChargeAmount(walletTransferRequest.getChargeAmount());
        transaction.setTotalAmount(walletTransferRequest.getTotalAmount());
        transaction.setEquivalentAmount(walletTransferRequest.getEquivalentAmount());
        transaction.setExchangeRate(walletTransferRequest.getExchangeRate());
        transaction.setToAccountNumber(walletTransferRequest.getToAccountNumber());
        transaction.setToCurrency(walletTransferRequest.getToCurrency());
        transaction.setFromCurrency(walletTransferRequest.getFromCurrency());
        transaction.setChargeAmount(walletTransferRequest.getChargeAmount());
        transaction.setWallet(walletTransferRequest.getWallet());
        transaction.setNarration(walletTransferRequest.getNarration());
        transaction.setInitiatedDate(new Date());
        transaction.setLastUpdatedDate(transaction.getInitiatedDate());
        transaction.setToAccountName(walletTransferRequest.getToAccountName());
        transaction.setPaaroReferenceId(walletTransferRequest.getPaaroTransactionReferenceId());
        transaction.setTransactionStatus(TransactionStatus.CUSTOMER_LOGGED_REQUEST);

        return transaction;

    }


}
