package com.zeed.paaro.lib.apiresponsemodel;


import com.zeed.paaro.lib.enums.ApiResponseCode;
import com.zeed.paaro.lib.models.Wallet;
import com.zeed.paaro.lib.models.WalletTransferTransaction;
import org.springframework.data.domain.Page;

import java.util.List;

public class WalletTransferRequestResponse {

    private String message;

    private ApiResponseCode responseStatus;

    private List<WalletTransferTransaction> walletTransferTransactions;

    private Page<WalletTransferTransaction> walletTransferTransactionPage;

    private WalletTransferTransaction walletTransferTransaction;

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
}
