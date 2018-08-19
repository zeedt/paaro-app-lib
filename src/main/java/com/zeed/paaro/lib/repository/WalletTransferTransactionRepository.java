package com.zeed.paaro.lib.repository;

import com.zeed.paaro.lib.enums.TransactionStatus;
import com.zeed.paaro.lib.models.WalletTransferTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransferTransactionRepository extends JpaRepository<WalletTransferTransaction, Long> {

    List<WalletTransferTransaction> findAllByManagedUser_EmailAndFromCurrency_Type(String email, String currency);

    Page<WalletTransferTransaction> findAllByManagedUser_EmailAndFromCurrency_Type(String email, String currency, Pageable pageable);

    List<WalletTransferTransaction> findAllByManagedUser_Email(String email);

    Page<WalletTransferTransaction> findAllByManagedUser_Email(String email, Pageable pageable);

    Page<WalletTransferTransaction> findAllByManagedUser_EmailAndToAccountNameLike(String email, String accountName, Pageable pageable);

    List<WalletTransferTransaction> findAllByManagedUser_EmailAndTransactionStatus(String email, TransactionStatus transactionStatus);

    Page<WalletTransferTransaction> findAllByManagedUser_EmailAndTransactionStatus(String email, TransactionStatus transactionStatus, Pageable pageable);

    List<WalletTransferTransaction> findAllByFromCurrency_Type(String currency);

    List<WalletTransferTransaction> findAllByFromCurrency_TypeNotIn(String currency);

    List<WalletTransferTransaction> findAllByFromCurrency_TypeAndTransferRequestMapIsNull(String currency);

    List<WalletTransferTransaction> findAllByFromCurrency_TypeNotInAndTransferRequestMapIsNull(String currency);

}
