package com.zeed.paaro.lib.apiresponsemodel;

import com.zeed.paaro.lib.enums.ApiResponseCode;
import com.zeed.paaro.lib.models.Wallet;
import com.zeed.paaro.lib.models.WalletFundingTransaction;
import com.zeed.usermanagement.enums.ResponseStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public class WalletResponse {

    private String message;

    private ApiResponseCode responseStatus;

    private Wallet wallet;

    private List<Wallet> walletList;

    List<WalletFundingTransaction> walletFundingTransactions;

    Page<WalletFundingTransaction> walletFundingTransactionPage;

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

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public List<Wallet> getWalletList() {
        return walletList;
    }

    public void setWalletList(List<Wallet> walletList) {
        this.walletList = walletList;
    }

    public static WalletResponse returnResponseWithCode(ApiResponseCode responseStatus, String messagea) {

        WalletResponse walletResponse = new WalletResponse();

        walletResponse.setMessage(messagea);
        walletResponse.setResponseStatus(responseStatus);

        return walletResponse;

    }

    public List<WalletFundingTransaction> getWalletFundingTransactions() {
        return walletFundingTransactions;
    }

    public void setWalletFundingTransactions(List<WalletFundingTransaction> walletFundingTransactions) {
        this.walletFundingTransactions = walletFundingTransactions;
    }

    public Page<WalletFundingTransaction> getWalletFundingTransactionPage() {
        return walletFundingTransactionPage;
    }

    public void setWalletFundingTransactionPage(Page<WalletFundingTransaction> walletFundingTransactionPage) {
        this.walletFundingTransactionPage = walletFundingTransactionPage;
    }
}
