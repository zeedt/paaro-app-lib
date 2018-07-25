package com.zeed.paaro.lib.repository;

import com.zeed.paaro.lib.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency,Long> {

    Currency findCurrencyByType(String type);



}
