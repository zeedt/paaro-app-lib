package com.zeed.paaro.lib.models;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("wallet_fund")
public class WalletFundingTransaction extends Transaction {

}
