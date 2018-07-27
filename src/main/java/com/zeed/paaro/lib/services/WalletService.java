package com.zeed.paaro.lib.services;

import com.zeed.paaro.lib.apirequestmodel.WalletRequest;
import com.zeed.paaro.lib.apiresponsemodel.WalletResponse;
import com.zeed.paaro.lib.email.sendgrid.SendGridEmail;
import com.zeed.paaro.lib.enums.ApiResponseCode;
import com.zeed.paaro.lib.models.*;
import com.zeed.paaro.lib.repository.CurrencyRepository;
import com.zeed.paaro.lib.repository.WalletFundingTransactionRepository;
import com.zeed.paaro.lib.repository.WalletRepository;
import com.zeed.usermanagement.security.UserDetailsTokenEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private SendGridEmail sendGridEmail;

    @Autowired
    private WalletFundingTransactionRepository walletFundingTransactionRepository;

    Logger logger = LoggerFactory.getLogger(WalletService.class.getName());

    public WalletResponse findWalletById(Long id) {

        if (id == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Null id passed");
        }

        Optional<Wallet> wallet = walletRepository.findById(id);

        if (wallet.isPresent()) {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWallet(wallet.get());
            return walletResponse;
        } else {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Record not found");
        }

    }
    public WalletResponse findWalletsByUserId(Long id) {

        if (id == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Null id passed");
        }

        List<Wallet> wallets = walletRepository.findAllByUserId(id);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No wallet found for user");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }

    public WalletResponse findWalletsByEmail(String email) {

        if (StringUtils.isEmpty(email)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Email cannot be blank");
        }

        List<Wallet> wallets = walletRepository.findAllByEmail(email);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No wallet found for email");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }
    public WalletResponse findWalletsByCurrency(String currency) {

        if (StringUtils.isEmpty(currency)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        List<Wallet> wallets = walletRepository.findAllByCurrency_Type(currency);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No wallet found for currency");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }
    public WalletResponse findLoggedInUserWallets() {
        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.managedUser == null || StringUtils.isEmpty(userDetailsTokenEnvelope.managedUser.getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid credentials");
        }

        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        List<Wallet> wallets = walletRepository.findAllByEmail(email);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "No wallet found for user");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }

    public WalletResponse addWallet(WalletRequest walletRequest) {

        if ( walletRequest == null || StringUtils.isEmpty(walletRequest.getCurrencyType()) ) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.managedUser == null || StringUtils.isEmpty(userDetailsTokenEnvelope.managedUser.getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid credentials");
        }

        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        Currency currency = currencyRepository.findCurrencyByType(walletRequest.getCurrencyType());

        if (currency == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "The currency type is not found on the system. Please choose the appropriate currency");
        }

        List<Wallet> wallets = walletRepository.findAllByCurrency_TypeAndEmail(walletRequest.getCurrencyType(), email);

        if (!CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.ALREADY_EXIST, "Wallet of the passed currency already exist for user");

        } else {
            Wallet wallet = new Wallet();
            wallet.setCurrency(currency);
            wallet.setActive(true);
            wallet.setUserId(userDetailsTokenEnvelope.managedUser.getId());
            wallet.setEmail(email);
            walletRepository.save(wallet);

            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWallet(wallet);

            //Notification test
            sendWalletNotification();
            return walletResponse;
        }

    }

    public WalletResponse fundWallet(WalletRequest walletRequest) {

        if (walletRequest == null || walletRequest.getTransactionStatus() == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Request and transaction status cannot be null");
        }

        String validationResult = getValidationRequestErrorMessage(walletRequest);

        if (validationResult != null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, validationResult);
        }

        Wallet wallet = getCurrentLoggedInUserWalletByCurrencyType(walletRequest.getCurrencyType());

        if (wallet == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Wallet of the specified currency type not found for user");
        }

        walletRequest.setWallet(wallet);
        walletRequest.setEmail(wallet.getEmail());

        WalletFundingTransaction fundingTransaction = createFundTransactionFromWalletrequest(walletRequest);

        walletFundingTransactionRepository.save(fundingTransaction);

        BigDecimal walletAmount = wallet.getAvailableAccountBalance();

        wallet.setAvailableAccountBalance(walletAmount.add(fundingTransaction.getActualAmount()));
        walletRepository.save(wallet);

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setWallet(wallet);
        walletResponse.setMessage("Wallet has been credited with " + walletRequest.getActualAmount());
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        return walletResponse;

    }

    public WalletResponse findALlFundingWalletTransactionsByUserWallet(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isEmpty(walletRequest.getCurrencyType())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        if (StringUtils.isEmpty(email)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }

        List<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByEmailAndCurrency_Type(email, walletRequest.getCurrencyType());

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactions(fundingTransactions);

        return walletResponse;

    }

    public WalletResponse findALlFundingWalletTransactionsByEmail(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isEmpty(walletRequest.getCurrencyType()) || StringUtils.isEmpty(walletRequest.getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type and email cannot be blank");
        }

        List<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByEmailAndCurrency_Type(walletRequest.getEmail(), walletRequest.getCurrency().getType());

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactions(fundingTransactions);

        return walletResponse;

    }



    public String getValidationRequestErrorMessage(WalletRequest walletRequest) {

        if (StringUtils.isEmpty(walletRequest.getEmail()) || StringUtils.isEmpty(walletRequest.getNarration()) ||
                StringUtils.isEmpty(walletRequest.getPaaroTransactionReferenceId())  || StringUtils.isEmpty(walletRequest.getThirdPartyTransactionId())
                || StringUtils.isEmpty(walletRequest.getCurrencyType()) ) {

            return "Email, narration, paaro transaction reference id, third party transaction reference id and currency type cannot be blank.";

        }

        if (walletRequest.getExchangeRate() == null || walletRequest.getExchangeRate().compareTo(BigDecimal.valueOf(0)) <= 0) {
            return "Exchange rate must be greater than 0";
        }

        if (walletRequest.getActualAmount() == null || walletRequest.getActualAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            return  "Actual amount must be greater than 0";
        }

        if (walletRequest.getTotalAmount() == null || walletRequest.getTotalAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            return "Total amount must be greater than 0";
        }

        if (walletRequest.getChargeAmount() == null || walletRequest.getChargeAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            return "Charge amount must be greater than 0";
        }

        if (walletRequest.getTotalAmount().compareTo(walletRequest.getActualAmount()) <=0 ) {
            return "Total amount must be greater than equal amount";
        }

        if (walletRequest.getTotalAmount().compareTo(walletRequest.getChargeAmount()) <=0 ) {
            return "Total amount must be greater than charge amount";
        }

        if (walletRequest.getTotalAmount().compareTo( (walletRequest.getChargeAmount().add(walletRequest.getActualAmount())) ) !=0 ) {
            return "Total amount must be equal to sum of charge amount and actual amount";
        }

        Currency currency = currencyRepository.findCurrencyByType(walletRequest.getCurrencyType());

        if (currency == null) {
            return "Currency type does not exist on the system";
        }

        walletRequest.setCurrency(currency);
        return null;

    }

    public Wallet getCurrentLoggedInUserWalletByCurrencyType(String currencyType) {

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        Wallet wallet = walletRepository.findByEmailAndCurrency_Type(email, currencyType);

        return wallet;


    }


    private WalletFundingTransaction createFundTransactionFromWalletrequest(WalletRequest walletRequest) {

        WalletFundingTransaction transaction = new WalletFundingTransaction();

        transaction.setActualAmount(walletRequest.getActualAmount());
        transaction.setChargeAmount(walletRequest.getChargeAmount());
        transaction.setTotalAmount(walletRequest.getTotalAmount());
        transaction.setEquivalentAmount(walletRequest.getActualAmount());
        transaction.setExchangeRate(walletRequest.getExchangeRate());
        transaction.setEmail(walletRequest.getEmail());
        transaction.setFromAccountNumber(walletRequest.getFromAccountNumber());
        transaction.setCurrency(walletRequest.getCurrency());
        transaction.setChargeAmount(walletRequest.getChargeAmount());
        transaction.setWallet(walletRequest.getWallet());
        transaction.setNarration(walletRequest.getNarration());
        transaction.setInitiatedDate(new Date());
        transaction.setLastUpdatedDate(transaction.getInitiatedDate());
        transaction.setPaaroReferenceId(walletRequest.getPaaroTransactionReferenceId());
        transaction.setThirdPartyReferenceId(walletRequest.getThirdPartyTransactionId());
        transaction.setTransactionStatus(walletRequest.getTransactionStatus());

        return transaction;

    }

    public void sendWalletNotification() {

        try {

            EmailNotification emailNotification = new EmailNotification();
            emailNotification.setContent("<h3>Dear pal</h3> A new wallet has been added to your paaro account");
            emailNotification.setSubject("Paaro - Wallet creation");
            emailNotification.setTo("yusufsaheedtaiwo@gmail.com");
            emailNotification.addTo("soluwawunmi@gmail.com");

            sendGridEmail.sendEmailWithNoAttachment(emailNotification);

        } catch (IOException e) {

            logger.error("Error occured while sending notification due to ", e);

        }
    }



}
