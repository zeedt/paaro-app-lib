package com.zeed.paaro.lib.services;

import com.zeed.paaro.lib.apirequestmodel.WalletRequest;
import com.zeed.paaro.lib.apiresponsemodel.WalletResponse;
import com.zeed.paaro.lib.enums.ApiResponseCode;
import com.zeed.paaro.lib.enums.TransactionStatus;
import com.zeed.paaro.lib.models.*;
import com.zeed.paaro.lib.repository.CurrencyRepository;
import com.zeed.paaro.lib.repository.WalletFundingTransactionRepository;
import com.zeed.paaro.lib.repository.WalletRepository;
import com.zeed.usermanagement.security.UserDetailsTokenEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private WalletEmailService walletEmailService;

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

        List<Wallet> wallets = walletRepository.findAllByManagedUser_Id(id);

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



        List<Wallet> wallets = walletRepository.findAllByManagedUser_Email(email);

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

        List<Wallet> wallets = walletRepository.findAllByManagedUser_Id(userDetailsTokenEnvelope.managedUser.getId());

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "No wallet found for user");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }

    public WalletResponse addWallet(WalletRequest walletRequest) throws IOException {

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

        List<Wallet> wallets = walletRepository.findAllByCurrency_TypeAndManagedUser_Email(walletRequest.getCurrencyType(), email);

        if (!CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.ALREADY_EXIST, "Wallet of the passed currency already exist for user");

        } else {
            Wallet wallet = new Wallet();
            wallet.setCurrency(currency);
            wallet.setActive(true);
            wallet.setManagedUser(userDetailsTokenEnvelope.managedUser);
            walletRepository.save(wallet);

            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWallet(wallet);

            walletEmailService.sendWalletAdditionToAccountEmail(wallet);
            return walletResponse;
        }

    }

    public WalletResponse fundWallet(WalletRequest walletRequest) throws IOException {

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

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        walletRequest.setWallet(wallet);

        WalletFundingTransaction fundingTransaction = createFundTransactionFromWalletrequest(walletRequest);
        fundingTransaction.setManagedUser(userDetailsTokenEnvelope.managedUser);

        walletFundingTransactionRepository.save(fundingTransaction);
        WalletResponse walletResponse = new WalletResponse();


        if (fundingTransaction.getTransactionStatus() == TransactionStatus.SUCCESSFULL) {
            BigDecimal walletAmount = wallet.getAvailableAccountBalance();
            BigDecimal walletLedgerAmount = wallet.getLedgerAccountBalance();

            wallet.setAvailableAccountBalance(walletAmount.add(fundingTransaction.getActualAmount()));
            wallet.setLedgerAccountBalance(walletLedgerAmount.add(fundingTransaction.getActualAmount()));
            walletRepository.save(wallet);
            walletResponse.setMessage("Wallet has been credited with " + walletRequest.getActualAmount());
        }

        walletResponse.setWallet(wallet);
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        walletEmailService.sendWalletFundingEmail(wallet, fundingTransaction.getActualAmount());

        return walletResponse;

    }

    public WalletResponse findAllFundingWalletTransactionsByUserWallet(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isEmpty(walletRequest.getCurrencyType())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        if (StringUtils.isEmpty(email)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }

        List<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByManagedUser_EmailAndCurrency_Type(email, walletRequest.getCurrencyType());

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactions(fundingTransactions);

        return walletResponse;

    }

    public WalletResponse findALlFundingWalletTransactionsByUserWalletPaged(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isEmpty(walletRequest.getCurrencyType())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        if (StringUtils.isEmpty(email)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }

        Pageable pageable = new PageRequest(walletRequest.getPageNo(), walletRequest.getPageSize());

        Page<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByManagedUser_EmailAndCurrency_Type(email, walletRequest.getCurrencyType(), pageable);

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactionPage(fundingTransactions);

        return walletResponse;

    }

    public WalletResponse findALlFundingWalletTransactionsByEmailAndCurrency(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isEmpty(walletRequest.getCurrencyType()) || StringUtils.isEmpty(walletRequest.getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type and email cannot be blank");
        }

        List<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByManagedUser_EmailAndCurrency_Type(walletRequest.getEmail(), walletRequest.getCurrencyType());

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactions(fundingTransactions);

        return walletResponse;

    }

    public WalletResponse findALlFundingWalletTransactionsByEmailAndCurrencyPaged(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isEmpty(walletRequest.getCurrencyType()) || StringUtils.isEmpty(walletRequest.getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type and email cannot be blank");
        }

        Pageable pageable = new PageRequest(walletRequest.getPageNo(), walletRequest.getPageSize());

        Page<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByManagedUser_EmailAndCurrency_Type(walletRequest.getEmail(), walletRequest.getCurrencyType(), pageable);

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactionPage(fundingTransactions);

        return walletResponse;

    }

    public WalletResponse findALlFundingWalletTransactionsByEmailPaged(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isEmpty(walletRequest.getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Email cannot be blank");
        }

        Pageable pageable = new PageRequest(walletRequest.getPageNo(), walletRequest.getPageSize());

        Page<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByWallet_ManagedUser_Email(walletRequest.getEmail(), pageable);

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactionPage(fundingTransactions);

        return walletResponse;

    }



    public String getValidationRequestErrorMessage(WalletRequest walletRequest) {

        if (StringUtils.isEmpty(walletRequest.getNarration()) ||
                StringUtils.isEmpty(walletRequest.getPaaroTransactionReferenceId())  || StringUtils.isEmpty(walletRequest.getThirdPartyTransactionId())
                || StringUtils.isEmpty(walletRequest.getCurrencyType()) || walletRequest.getTransactionStatus() == null ) {

            return "Narration, paaro transaction reference id, transaction status, third party transaction reference id and currency type cannot be blank.";

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

        return walletRepository.findByManagedUser_EmailAndCurrency_Type(email, currencyType);

    }


    private WalletFundingTransaction createFundTransactionFromWalletrequest(WalletRequest walletRequest) {

        WalletFundingTransaction transaction = new WalletFundingTransaction();

        transaction.setActualAmount(walletRequest.getActualAmount());
        transaction.setChargeAmount(walletRequest.getChargeAmount());
        transaction.setTotalAmount(walletRequest.getTotalAmount());
        transaction.setEquivalentAmount(walletRequest.getActualAmount());
        transaction.setExchangeRate(walletRequest.getExchangeRate());
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

    public Page<WalletFundingTransaction> findWalletFundingTransactionPageWithFilter (int pageNo, int pageSize, String filter) {

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        return walletFundingTransactionRepository.filterByWalletUserFirstOrLastName("%"+filter+"%",pageRequest);

    }


    public Page<WalletFundingTransaction> findWalletFundingTransactionPage (int pageNo, int pageSize) {

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        return walletFundingTransactionRepository.findAllByIdIsNotNull(pageRequest);

    }

    private List<Wallet> getAllTransferTransactionsByEmail(String email) {

        return walletRepository.findAllByManagedUser_Email(email);

    }



}
