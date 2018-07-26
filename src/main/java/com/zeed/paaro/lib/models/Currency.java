package com.zeed.paaro.lib.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
public class Currency implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String type;

    @NotNull
    private double rateToNaira;

    @NotNull
    private String description;

    @OneToMany (mappedBy = "currency", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Wallet> walletList;



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRateToNaira() {
        return rateToNaira;
    }

    public void setRateToNaira(double rateToNaira) {
        this.rateToNaira = rateToNaira;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Wallet> getWalletList() {
        return walletList;
    }

    public void setWalletList(List<Wallet> walletList) {
        this.walletList = walletList;
    }
}
