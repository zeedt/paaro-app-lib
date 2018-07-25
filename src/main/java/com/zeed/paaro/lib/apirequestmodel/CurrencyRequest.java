package com.zeed.paaro.lib.apirequestmodel;

public class CurrencyRequest {

    private String type;

    private Double rateToNaira;

    private String decsription;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getRateToNaira() {
        return rateToNaira;
    }

    public void setRateToNaira(Double rateToNaira) {
        this.rateToNaira = rateToNaira;
    }

    public String getDecsription() {
        return decsription;
    }

    public void setDecsription(String decsription) {
        this.decsription = decsription;
    }
}
