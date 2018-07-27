package com.zeed.paaro.lib.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class TransferRequestMap {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private WalletTransferTransaction nairaHolderTransaction;

    private WalletTransferTransaction otherCurrencyHolderTransaction;

    @NotNull
    private Date dateMapped;

    private Date dateSettled;

    public WalletTransferTransaction getNairaHolderTransaction() {
        return nairaHolderTransaction;
    }

    public void setNairaHolderTransaction(WalletTransferTransaction nairaHolderTransaction) {
        this.nairaHolderTransaction = nairaHolderTransaction;
    }

    public WalletTransferTransaction getOtherCurrencyHolderTransaction() {
        return otherCurrencyHolderTransaction;
    }

    public void setOtherCurrencyHolderTransaction(WalletTransferTransaction otherCurrencyHolderTransaction) {
        this.otherCurrencyHolderTransaction = otherCurrencyHolderTransaction;
    }

    public Date getDateMapped() {
        return dateMapped;
    }

    public void setDateMapped(Date dateMapped) {
        this.dateMapped = dateMapped;
    }

    public Date getDateSettled() {
        return dateSettled;
    }

    public void setDateSettled(Date dateSettled) {
        this.dateSettled = dateSettled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
