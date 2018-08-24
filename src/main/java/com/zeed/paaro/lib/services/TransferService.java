package com.zeed.paaro.lib.services;

import com.zeed.generic.DateUtils;
import com.zeed.paaro.lib.apirequestmodel.WalletTransferRequest;
import com.zeed.paaro.lib.apiresponsemodel.WalletTransferRequestResponse;
import com.zeed.paaro.lib.enums.ApiResponseCode;
import com.zeed.paaro.lib.enums.Module;
import com.zeed.paaro.lib.enums.TransactionStatus;
import com.zeed.paaro.lib.models.*;
import com.zeed.paaro.lib.repository.CurrencyRepository;
import com.zeed.paaro.lib.repository.TokensRepository;
import com.zeed.paaro.lib.repository.WalletRepository;
import com.zeed.paaro.lib.repository.WalletTransferTransactionRepository;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.repository.ManagedUserRepository;
import com.zeed.usermanagement.security.UserDetailsTokenEnvelope;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
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

    @Autowired
    private ManagedUserRepository userRepository;

    @Autowired
    private DateUtils dateUtils;

    @Autowired
    private TokensRepository tokensRepository;

    @Value("${currency.naira-type:NGN}")
    private String nairaCurrency;

    @Value("${transfer.charge:0.5}")
    private Double chargeRate;

    private Logger logger = LoggerFactory.getLogger(TransferService.class.getName());

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
        walletTransferRequestResponse.setMessage("Transactions fetched by email");
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

    public Page<WalletTransferTransaction> findWalletTransferTransactionPage(int pageNo, int pageSize, String filter) {

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        return walletTransferTransactionRepository.findAllByIdIsNotNullAndToAccountNameIsLike("%"+filter+"%", pageRequest);

    }


    public Page<WalletTransferTransaction> findWalletTransferTransactionPageWithDateRange(int pageNo, int pageSize, String filter, String fromDateStr, String toDateStr) {

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        Date toDate = null;
        Date fromDate = null;

        try {
            toDate = dateUtils.convertStringToDate(toDateStr,"yyyy-MM-dd");
            fromDate = dateUtils.convertStringToDate(fromDateStr,"yyyy-MM-dd");
        } catch (ParseException e) {
            logger.error("Unable to convert date string to date due to ", e);
        }

        if (toDate == null || fromDate == null) {
            return new PageImpl<>(new ArrayList<>());
        }

        return walletTransferTransactionRepository.findAllByIdIsNotNullAndToAccountNameIsLikeAndInitiatedDateIsBetween("%"+filter+"%", fromDate, toDate, pageRequest);

    }






    public Page<WalletTransferTransaction> findWalletTransferTransactionPageWithFilter(int pageNo, int pageSize) {

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        return walletTransferTransactionRepository.findAllByIdIsNotNull(pageRequest);

    }

    public List<WalletTransferTransaction> findWalletTransferTransactionWithDateRange(String filter, String fromDateStr, String toDateStr) {


        Date toDate = null;
        Date fromDate = null;

        try {
            toDate = dateUtils.convertStringToDate(toDateStr,"yyyy-MM-dd");
            fromDate = dateUtils.convertStringToDate(fromDateStr,"yyyy-MM-dd");
        } catch (ParseException e) {
            logger.error("Unable to convert date string to date due to ", e);
        }

        if (toDate == null || fromDate == null) {
            return new ArrayList<>();
        }

        return walletTransferTransactionRepository.findAllByIdIsNotNullAndToAccountNameIsLikeAndInitiatedDateIsBetween("%"+filter+"%", fromDate, toDate);

    }

    public List<WalletTransferTransaction> findWalletTransferTransaction(String filter) {

        return walletTransferTransactionRepository.findAllByIdIsNotNullAndToAccountNameIsLike("%"+filter+"%");

    }

    public void generateExcelForTransactions(String filter, HttpServletResponse httpServletResponse) throws IOException{

        List<WalletTransferTransaction> walletTransferTransactions = findWalletTransferTransaction(filter);

        logger.info("Transactions fetched is " + walletTransferTransactions.size());

        Workbook workbook = new XSSFWorkbook();

        workbook.createSheet("Transactions");

        workbook = generateHeaderForExcel(workbook);

        workbook = writeTransactionsToExcel(workbook, walletTransferTransactions);

        workbook.write(httpServletResponse.getOutputStream());

        workbook.close();

        httpServletResponse.flushBuffer();


    }

    public Workbook generateHeaderForExcel(Workbook workbook) {

        workbook.getSheetAt(0);
        String[] excelHeader = new String[]{"Currency(from)","Currency(to)","Actual Amount","Charge Amount","Equivalent Amount","Exchange Rate","Account Name","System ref Id","TP ref Id","Narration","Status","Date initiated"};
        Sheet sheet = workbook.getSheet("Transactions");
        Row row = sheet.createRow(0);

        for (int i=0;i<excelHeader.length;i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
        }

        return workbook;

    }

    public Workbook writeTransactionsToExcel(Workbook workbook, List<WalletTransferTransaction> transferTransactions) {

        Sheet sheet = workbook.getSheet("Transactions");

        int rowNum = 1;

        Row row;
        Cell cell;

        for (WalletTransferTransaction transferTransaction : transferTransactions) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(transferTransaction.getFromCurrency().getDescription());

            cell = row.createCell(1);
            cell.setCellValue(transferTransaction.getToCurrency().getDescription());

            cell = row.createCell(2);
            cell.setCellValue(String.valueOf(transferTransaction.getActualAmount()));

            cell = row.createCell(3);
            cell.setCellValue(String.valueOf(transferTransaction.getChargeAmount()));

            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(transferTransaction.getEquivalentAmount()));

            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(transferTransaction.getExchangeRate()));

            cell = row.createCell(6);
            cell.setCellValue(transferTransaction.getToAccountName());


            cell = row.createCell(7);
            cell.setCellValue(transferTransaction.getPaaroReferenceId());


            cell = row.createCell(8);
            cell.setCellValue(transferTransaction.getThirdPartyReferenceId());


            cell = row.createCell(9);
            cell.setCellValue(transferTransaction.getNarration());


            cell = row.createCell(10);
            cell.setCellValue(transferTransaction.getTransactionStatus().toString());


            cell = row.createCell(11);
            cell.setCellValue(transferTransaction.getInitiatedDate());

        }

        return workbook;
    }


    @Transactional
    public WalletTransferRequestResponse computeChargesAndValues (WalletTransferRequest walletTransferRequest, boolean generateAndSendToken) throws IOException {

        BigDecimal totalAmount ;
        BigDecimal equivalentAmount ;
        BigDecimal exchangeRate ;

        boolean isFromNairaCurrency = false;

        if (walletTransferRequest == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request");
        }

        String fromCurrency = walletTransferRequest.getFromCurrencyType();
        String toCurrency = walletTransferRequest.getToCurrencyType();
        BigDecimal amount = walletTransferRequest.getActualAmount();

        if (StringUtils.isEmpty(fromCurrency) || StringUtils.isEmpty(toCurrency) || walletTransferRequest.getActualAmount()==null || walletTransferRequest.getActualAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Amount must be greater than 0, From currency and to currency cannot be empty");
        }

        if (fromCurrency.trim().equalsIgnoreCase(toCurrency.trim())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "From currency and to currency cannot be equal");
        }

        if (!fromCurrency.trim().equalsIgnoreCase(nairaCurrency.trim()) && !toCurrency.trim().equalsIgnoreCase(nairaCurrency.trim())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "From currency or to currency must be Naira");
        }

        Currency fromCurrencyObject = currencyRepository.findCurrencyByType(fromCurrency.trim());

        if (fromCurrencyObject == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "From currency not found");
        }


        Currency toCurrencyObject = currencyRepository.findCurrencyByType(toCurrency.trim());

        if (toCurrencyObject == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "To currency not found");
        }

        if (fromCurrency.trim().equalsIgnoreCase(nairaCurrency.trim())) {
            isFromNairaCurrency = true;
        }

        if (isFromNairaCurrency) {
            exchangeRate = BigDecimal.valueOf(toCurrencyObject.getRateToNaira());
            equivalentAmount = amount.divide(exchangeRate,2, RoundingMode.HALF_EVEN);
        } else {
            exchangeRate = BigDecimal.valueOf(fromCurrencyObject.getRateToNaira());
            equivalentAmount = amount.multiply(exchangeRate);
        }

        BigDecimal charge = amount.multiply(BigDecimal.valueOf(chargeRate));
        totalAmount = amount.add(charge);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setCharges(charge);
        walletTransferRequestResponse.setAmount(amount);
        walletTransferRequestResponse.setTotalAmount(totalAmount);
        walletTransferRequestResponse.setEquivalentAmount(equivalentAmount);
        walletTransferRequestResponse.setExchangeRate(exchangeRate);
        walletTransferRequestResponse.setChargeRate(chargeRate);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequest.setFromCurrency(fromCurrencyObject);
        walletTransferRequest.setToCurrency(toCurrencyObject);
        if (generateAndSendToken) {
            String token = generateTokenForRequest();
            saveToken(walletTransferRequest.getEmail(),token);
            walletEmailService.sendToken(walletTransferRequest.getEmail(),token);
        }
        return walletTransferRequestResponse;

    }

    private void saveToken(String email, String token) {
        Tokens tokens = new Tokens();
        tokens.setEmail(email);
        tokens.setToken(token);
        tokens.setModule(Module.TRANSFER);
        tokens.setDateAdded(new Date());
        tokensRepository.save(tokens);
    }

    @Transactional
    public WalletTransferRequestResponse initiateTransferRequestForUser(WalletTransferRequest walletTransferRequest) throws IOException {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getToAccountName()) || StringUtils.isEmpty(walletTransferRequest.getToAccountNumber())
                || StringUtils.isEmpty(walletTransferRequest.getNarration()) || StringUtils.isEmpty(walletTransferRequest.getEmail()) || StringUtils.isEmpty(walletTransferRequest.getToken())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Account name, Token, Account number, Email and Narration cannot be null");
        }

        ManagedUser managedUser = userRepository.findOneByEmail(walletTransferRequest.getEmail());

        if (managedUser == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "User not found with the supplied email");
        }

        WalletTransferRequestResponse walletTransferRequestResponse = computeChargesAndValues(walletTransferRequest, false);

        if (walletTransferRequestResponse == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Null response received");
        }

        if (walletTransferRequestResponse.getResponseStatus() != ApiResponseCode.SUCCESSFUL) {
            return walletTransferRequestResponse;
        }

        Tokens tokens = tokensRepository.findByEmailAndModuleAndTokenOrderByIdDesc(walletTransferRequest.getEmail(),Module.TRANSFER,walletTransferRequest.getToken());

        if (tokens == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Token not valid");
        }

        long tokenDuration = ((new Date()).getTime() - tokens.getDateAdded().getTime())/1000;

        if (tokenDuration > 300) {
            tokensRepository.delete(tokens);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Token has expired");
        }

        tokensRepository.delete(tokens);

        Wallet wallet = walletRepository.findByManagedUser_EmailAndCurrency_Type(walletTransferRequest.getEmail(),walletTransferRequest.getFromCurrency().getType());

        if (wallet == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Wallet not found for user");
        }


        walletTransferRequest.setExchangeRate(walletTransferRequestResponse.getExchangeRate());
        walletTransferRequest.setTotalAmount(walletTransferRequestResponse.getTotalAmount());
        walletTransferRequest.setActualAmount(walletTransferRequestResponse.getAmount());
        walletTransferRequest.setChargeAmount(walletTransferRequestResponse.getCharges());
        walletTransferRequest.setEquivalentAmount(walletTransferRequestResponse.getEquivalentAmount());
        walletTransferRequest.setWallet(wallet);

        if (wallet.getAvailableAccountBalance().compareTo(walletTransferRequest.getTotalAmount()) < 0 ) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.UNABLE_TO_PROCESS, "Available balance cannot be less than the total amount");
        }

        WalletTransferTransaction walletTransferTransaction = createTransaction(walletTransferRequest);

        walletTransferTransaction.setManagedUser(managedUser);

        walletTransferTransactionRepository.save(walletTransferTransaction);

        BigDecimal newAvailableAccountBalance = wallet.getAvailableAccountBalance().subtract(walletTransferRequest.getTotalAmount());
        wallet.setAvailableAccountBalance(newAvailableAccountBalance);
        walletRepository.save(wallet);

        walletTransferRequestResponse.setMessage("Transfer request successfully logged");
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setWallet(wallet);

        walletEmailService.sendTransferRequestEmail(walletTransferTransaction);

        return walletTransferRequestResponse;

    }

    public WalletTransferTransaction createTransaction (WalletTransferRequest walletTransferRequest) {
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
        transaction.setPaaroReferenceId(generateRandomValueForRequest());
        transaction.setTransactionStatus(TransactionStatus.INITIATOR_LOGGED_REQUEST);

        return transaction;
    }

    public String generateRandomValueForRequest() {
        Long nanoTime = System.nanoTime();
        SecureRandom secureRandom = new SecureRandom();
        int randomNumber = secureRandom.nextInt(1000001);
        String randomString = String.format("%07d",randomNumber);
        randomString = nanoTime.toString() + randomString;
        return randomString;
    }

    public String generateTokenForRequest() {
        SecureRandom secureRandom = new SecureRandom();
        int randomNumber = secureRandom.nextInt(100001);
        String token = String.valueOf(randomNumber);
        logger.info("Token generated is " + token);
        return token;
    }


}
