package com.zeed.paaro.lib.models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("wallet_transfer")
public class WalletTransferTransaction extends Transaction {

}
