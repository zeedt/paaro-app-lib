package com.zeed.paaro.lib.repository;

import com.zeed.paaro.lib.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findAllByUserId(Long id);

    List<Wallet> findAllByCurrency_Type(String type);

    List<Wallet> findAllByCurrency_TypeAndEmail(String type, String email);

    List<Wallet> findAllByEmail(String email);

    Wallet findByEmailAndCurrency_Type(String email, String currencyType);

}
