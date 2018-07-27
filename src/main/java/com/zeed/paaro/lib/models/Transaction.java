package com.zeed.paaro.lib.models;

import com.zeed.paaro.lib.enums.TransactionStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_type", discriminatorType = DiscriminatorType.STRING)
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fromAccountNumber;

    private String toAccountNumber;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private TransactionStatus transactionStatus;

    @NotNull
    private String narration;

    @NotNull
    private String email;

    @NotNull
    private String paaroReferenceId;

    private String thirdPartyReferenceId;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private BigDecimal actualAmount;

    @NotNull
    private BigDecimal chargeAmount;

    @NotNull
    private BigDecimal equivalentAmount;

    @OneToOne
    private Currency currency;

    @OneToOne
    private Currency fromCurrency;

    @OneToOne
    private Currency toCurrency;

    @NotNull
    private BigDecimal exchangeRate;

    @NotNull
    private Date initiatedDate;

    @NotNull
    private Date lastUpdatedDate;

    @NotNull
    @OneToOne
    private Wallet wallet;

    @OneToOne
    private TransferRequestMap transferRequestMap;

    private String errorMessage;

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
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

    public String getPaaroReferenceId() {
        return paaroReferenceId;
    }

    public void setPaaroReferenceId(String paaroReferenceId) {
        this.paaroReferenceId = paaroReferenceId;
    }

    public String getThirdPartyReferenceId() {
        return thirdPartyReferenceId;
    }

    public void setThirdPartyReferenceId(String thirdPartyReferenceId) {
        this.thirdPartyReferenceId = thirdPartyReferenceId;
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

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getEquivalentAmount() {
        return equivalentAmount;
    }

    public void setEquivalentAmount(BigDecimal equivalentAmount) {
        this.equivalentAmount = equivalentAmount;
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

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getInitiatedDate() {
        return initiatedDate;
    }

    public void setInitiatedDate(Date initiatedDate) {
        this.initiatedDate = initiatedDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public TransferRequestMap getTransferRequestMap() {
        return transferRequestMap;
    }

    public void setTransferRequestMap(TransferRequestMap transferRequestMap) {
        this.transferRequestMap = transferRequestMap;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
