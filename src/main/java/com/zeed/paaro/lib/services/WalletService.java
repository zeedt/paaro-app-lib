package com.zeed.paaro.lib.services;

import com.zeed.paaro.lib.apirequestmodel.WalletRequest;
import com.zeed.paaro.lib.apiresponsemodel.WalletResponse;
import com.zeed.paaro.lib.email.sendgrid.SendGridEmail;
import com.zeed.paaro.lib.enums.ApiResponseCode;
import com.zeed.paaro.lib.models.Currency;
import com.zeed.paaro.lib.models.EmailNotification;
import com.zeed.paaro.lib.models.Wallet;
import com.zeed.paaro.lib.repository.CurrencyRepository;
import com.zeed.paaro.lib.repository.WalletRepository;
import com.zeed.usermanagement.security.UserDetailsTokenEnvelope;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private SendGridEmail sendGridEmail;

    public WalletResponse findWalletById(Long id) {

        if (id == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Null id passed");
        }

        Optional<Wallet> wallet = walletRepository.findById(id);

        if (wallet.isPresent()) {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWallet(wallet.get());
            return walletResponse;
        } else {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Record not found");
        }

    }
    public WalletResponse findWalletsByUserId(Long id) {

        if (id == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Null id passed");
        }

        List<Wallet> wallets = walletRepository.findAllByUserId(id);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No wallet found for user");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }

    public WalletResponse findWalletsByEmail(String email) {

        if (StringUtils.isEmpty(email)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Email cannot be blank");
        }

        List<Wallet> wallets = walletRepository.findAllByEmail(email);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No wallet found for email");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }
    public WalletResponse findWalletsByCurrency(String currency) {

        if (StringUtils.isEmpty(currency)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        List<Wallet> wallets = walletRepository.findAllByCurrency_Type(currency);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No wallet found for currency");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }
    public WalletResponse findLoggedInUserWallets() {
        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.managedUser == null || StringUtils.isEmpty(userDetailsTokenEnvelope.managedUser.getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid credentials");
        }

        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        List<Wallet> wallets = walletRepository.findAllByEmail(email);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "No wallet found for user");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }

    public WalletResponse addWallet(WalletRequest walletRequest) {

        if ( walletRequest == null || StringUtils.isEmpty(walletRequest.getCurrencyType()) ) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.managedUser == null || StringUtils.isEmpty(userDetailsTokenEnvelope.managedUser.getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid credentials");
        }

        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        Currency currency = currencyRepository.findCurrencyByType(walletRequest.getCurrencyType());

        if (currency == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "The currency type is not found on the system. Please choose the appropriate currency");
        }

        List<Wallet> wallets = walletRepository.findAllByCurrency_TypeAndEmail(walletRequest.getCurrencyType(), email);

        if (!CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.ALREADY_EXIST, "Wallet of the passed currency already exist for user");

        } else {
            Wallet wallet = new Wallet();
            wallet.setCurrency(currency);
            wallet.setActive(true);
            wallet.setUserId(userDetailsTokenEnvelope.managedUser.getId());
            wallet.setEmail(email);
            walletRepository.save(wallet);

            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWallet(wallet);

            //Notification test
            sendWalletNotification();
            return walletResponse;
        }

    }

    public WalletResponse fundWallet(String currency) {

        if (StringUtils.isEmpty(currency)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        List<Wallet> wallets = walletRepository.findAllByCurrency_Type(currency);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No wallet found for currency");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }


    public void sendWalletNotification() {
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent("<h3>Dear user</h3> A new wallet has been added to your account");
        emailNotification.setSubject("Paaro - Wallet creation");
        emailNotification.setTo("yusufsaheedtaiwo@gmail.com");

        try {
            sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            emailNotification.setTo("soluwawunmi@gmail.com");
            sendGridEmail.sendEmailWithNoAttachment(emailNotification);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
