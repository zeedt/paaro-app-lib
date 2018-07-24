package com.zeed.paaro.lib.models;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("direct_transfer")
public class DirectTransferTransaction extends Transaction {

}
