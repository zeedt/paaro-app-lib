package com.zeed.paaro.lib.apiresponsemodel;


import com.zeed.paaro.lib.enums.ApiResponseCode;
import com.zeed.paaro.lib.models.Currency;
import com.zeed.paaro.lib.models.Wallet;
import com.zeed.paaro.lib.models.WalletTransferTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WalletTransferRequestResponse {

    private String message;

    private BigDecimal charges;

    private Double chargeRate;

    private Double rate;

    private BigDecimal equivalentAmount;

    private BigDecimal totalAmount;

    private BigDecimal amount;

    private BigDecimal exchangeRate;

    private ApiResponseCode responseStatus;

    private Currency fromCurrency;

    private Currency toCurrency;

    private List<WalletTransferTransaction> walletTransferTransactions = new ArrayList<>();

    private Page<WalletTransferTransaction> walletTransferTransactionPage = new PageImpl<>(new ArrayList<>());

    private WalletTransferTransaction walletTransferTransaction = new WalletTransferTransaction();

    private Wallet wallet;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiResponseCode getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ApiResponseCode responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<WalletTransferTransaction> getWalletTransferTransactions() {
        return walletTransferTransactions;
    }

    public void setWalletTransferTransactions(List<WalletTransferTransaction> walletTransferTransactions) {
        this.walletTransferTransactions = walletTransferTransactions;
    }

    public WalletTransferTransaction getWalletTransferTransaction() {
        return walletTransferTransaction;
    }

    public void setWalletTransferTransaction(WalletTransferTransaction walletTransferTransaction) {
        this.walletTransferTransaction = walletTransferTransaction;
    }

    public static WalletTransferRequestResponse returnResponseWithCode(ApiResponseCode responseStatus, String message) {
        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setMessage(message);
        walletTransferRequestResponse.setResponseStatus(responseStatus);
        return walletTransferRequestResponse;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Page<WalletTransferTransaction> getWalletTransferTransactionPage() {
        return walletTransferTransactionPage;
    }

    public void setWalletTransferTransactionPage(Page<WalletTransferTransaction> walletTransferTransactionPage) {
        this.walletTransferTransactionPage = walletTransferTransactionPage;
    }

    public BigDecimal getCharges() {
        return charges;
    }

    public void setCharges(BigDecimal charges) {
        this.charges = charges;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public BigDecimal getEquivalentAmount() {
        return equivalentAmount;
    }

    public void setEquivalentAmount(BigDecimal equivalentAmount) {
        this.equivalentAmount = equivalentAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Double getChargeRate() {
        return chargeRate;
    }

    public void setChargeRate(Double chargeRate) {
        this.chargeRate = chargeRate;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }
}
