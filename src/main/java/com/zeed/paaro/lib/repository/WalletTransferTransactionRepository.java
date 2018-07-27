package com.zeed.paaro.lib.repository;

import com.zeed.paaro.lib.enums.TransactionStatus;
import com.zeed.paaro.lib.models.WalletTransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransferTransactionRepository extends JpaRepository<WalletTransferTransaction, Long> {

    List<WalletTransferTransaction> findAllByEmailAndCurrency_Type(String email, String currency);

    List<WalletTransferTransaction> findAllByEmail(String email);

    List<WalletTransferTransaction> findAllByEmailAndTransactionStatus(String email, TransactionStatus transactionStatus);



}
