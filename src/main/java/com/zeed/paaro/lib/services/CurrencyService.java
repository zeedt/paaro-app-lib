package com.zeed.paaro.lib.services;

import com.zeed.paaro.lib.apirequestmodel.CurrencyRequest;
import com.zeed.paaro.lib.apiresponsemodel.CurrencyResponse;
import com.zeed.paaro.lib.enums.ApiResponseCode;
import com.zeed.paaro.lib.models.Currency;
import com.zeed.paaro.lib.repository.CurrencyRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    public CurrencyResponse addCurrency(CurrencyRequest currencyRequest) {

        if (currencyRequest == null ) {
            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request");
        }

        if (StringUtils.isEmpty(currencyRequest.getType()) || currencyRequest.getRateToNaira() == null || currencyRequest.getRateToNaira() <= 0 ) {

            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank and rate must be greater than 0 ");

        }

        Currency currency = currencyRepository.findCurrencyByType(currencyRequest.getType().trim());

        if (currency == null) {

            Currency newCurrency = new Currency();
            newCurrency.setDescription(currencyRequest.getDecsription());
            newCurrency.setRateToNaira(currencyRequest.getRateToNaira());
            newCurrency.setType(currencyRequest.getType());

            currencyRepository.save(newCurrency);
            CurrencyResponse currencyResponse = new CurrencyResponse();
            currencyResponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
            currencyResponse.setCurrency(newCurrency);
            currencyResponse.setMessage("Successfully added currency");

            return currencyResponse;

        }

        return CurrencyResponse.returnResponseWithCode(ApiResponseCode.ALREADY_EXIST, "Currency already exist");

    }

}
