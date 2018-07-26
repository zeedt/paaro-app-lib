package com.zeed.paaro.lib.models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
public class Wallet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Currency currency;

    @NotNull
    private BigDecimal availableAccountBalance = BigDecimal.valueOf(0);

    @NotNull
    private BigDecimal ledgerAccountBalance = BigDecimal.valueOf(0);

    private boolean isActive = true;

    @NotNull
    private Long userId;

    @NotNull
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getAvailableAccountBalance() {
        return availableAccountBalance;
    }

    public void setAvailableAccountBalance(BigDecimal availableAccountBalance) {
        this.availableAccountBalance = availableAccountBalance;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getLedgerAccountBalance() {
        return ledgerAccountBalance;
    }

    public void setLedgerAccountBalance(BigDecimal ledgerAccountBalance) {
        this.ledgerAccountBalance = ledgerAccountBalance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
