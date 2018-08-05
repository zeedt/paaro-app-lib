package com.zeed.paaro.lib.apirequestmodel;

import com.zeed.paaro.lib.enums.TransactionStatus;
import com.zeed.paaro.lib.models.Currency;
import com.zeed.paaro.lib.models.Wallet;

import java.math.BigDecimal;

public class WalletTransferRequest {

    private String email;

    private String paaroTransactionReferenceId;

    private com.zeed.paaro.lib.enums.TransactionStatus transactionStatus;

    private String narration;

    private BigDecimal chargeAmount;

    private BigDecimal totalAmount;

    private BigDecimal actualAmount;

    private BigDecimal equivalentAmount;

    private String fromCurrencyType;

    private String toCurrencyType;

    private String toAccountNumber;

    private BigDecimal exchangeRate;

    private Currency fromCurrency;

    private Currency toCurrency;

    private Wallet wallet;

    private String currency;

    private int pageNo = 0;

    private int pageSize = 10;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaaroTransactionReferenceId() {
        return paaroTransactionReferenceId;
    }

    public void setPaaroTransactionReferenceId(String paaroTransactionReferenceId) {
        this.paaroTransactionReferenceId = paaroTransactionReferenceId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public BigDecimal getEquivalentAmount() {
        return equivalentAmount;
    }

    public void setEquivalentAmount(BigDecimal equivalentAmount) {
        this.equivalentAmount = equivalentAmount;
    }

    public String getFromCurrencyType() {
        return fromCurrencyType;
    }

    public void setFromCurrencyType(String fromCurrencyType) {
        this.fromCurrencyType = fromCurrencyType;
    }

    public String getToCurrencyType() {
        return toCurrencyType;
    }

    public void setToCurrencyType(String toCurrencyType) {
        this.toCurrencyType = toCurrencyType;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
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

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
