package com.zeed.paaro.lib.apirequestmodel;

public class WalletRequest {

    private String currencyType;

    private String email;

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
