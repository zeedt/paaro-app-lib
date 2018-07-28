package com.zeed.paaro.lib.repository;


import com.zeed.paaro.lib.models.WalletFundingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletFundingTransactionRepository extends JpaRepository<WalletFundingTransaction, Long> {

    List<WalletFundingTransaction> findAllByManagedUser_EmailAndCurrency_Type(String email, String currencyType);

}
