package com.zeed.paaro.lib.repository;

import com.zeed.paaro.lib.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findAllByManagedUser_Id(Long id);

    List<Wallet> findAllByCurrency_Type(String type);

    List<Wallet> findAllByCurrency_TypeAndManagedUser_Email(String type, String email);

    List<Wallet> findAllByManagedUser_Email(String email);

    Wallet findByManagedUser_EmailAndCurrency_Type(String email, String currencyType);

}
