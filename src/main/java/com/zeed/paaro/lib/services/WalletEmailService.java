package com.zeed.paaro.lib.services;

import com.zeed.paaro.lib.email.sendgrid.SendGridEmail;
import com.zeed.paaro.lib.models.EmailNotification;
import com.zeed.paaro.lib.models.Transaction;
import com.zeed.paaro.lib.models.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WalletEmailService {

    private TemplateEngine templateEngine;

    @Autowired
    private SendGridEmail sendGridEmail;

    @Value("${email.add-wallet-to-account:email-template/wallet-to-account}")
    private String addWalletToAccountPath;

    @Value("${email.add-wallet-to-account-subject:Paaro - Wallet Creation}")
    private String addWalletToAccountSubject;

    @Value("${email.fund-wallet:email-template/wallet-funding}")
    private String fundWalletPath;

    @Value("${email.fund-wallet-subject:Paaro - Wallet Funding}")
    private String fundWalletSubject;

    @Value("${email.fund-wallet:email-template/transfer-request}")
    private String transferRequestPath;

    @Value("${email.fund-wallet-subject:Paaro - Transfer Request}")
    private String transferRequestSubject;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);


    @Autowired
    private WalletEmailService(@Qualifier("emailTemplateEngine") TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendWalletAdditionToAccountEmail(Wallet wallet) throws IOException {

        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent(getAddWalletEmailContentFromWallet(wallet));
        emailNotification.setSubject(addWalletToAccountSubject);
        emailNotification.setTo("yusufsaheedtaiwo@gmail.com");
        emailNotification.addTo("soluwawunmi@gmail.com");
        emailNotification.addTo(wallet.getManagedUser().getEmail());

        executorService.submit(()->{
            try {
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (IOException e) {

            }
        });
    }

    @Async
    public void sendWalletFundingEmail(Wallet wallet, BigDecimal actualAmount) throws IOException {

        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent(getFundingEmailContentFromWallet(wallet, actualAmount));
        emailNotification.setSubject(fundWalletSubject);
        emailNotification.setTo("yusufsaheedtaiwo@gmail.com");
        emailNotification.addTo("soluwawunmi@gmail.com");
        emailNotification.addTo(wallet.getManagedUser().getEmail());

        executorService.submit(()->{
            try {
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (IOException e) {

            }
        });
    }

    @Async
    public void sendTransferRequestEmail(Transaction transaction) throws IOException {

        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent(getTransferRequestEmailContent(transaction));
        emailNotification.setSubject(transferRequestSubject);
        emailNotification.setTo("yusufsaheedtaiwo@gmail.com");
        emailNotification.addTo("soluwawunmi@gmail.com");
        emailNotification.addTo(transaction.getWallet().getManagedUser().getEmail());

        executorService.submit(()->{
            try {
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (IOException e) {

            }
        });
    }


    private String getAddWalletEmailContentFromWallet(Wallet wallet) {
        Context context = new Context();
        context.setVariable("username", wallet.getManagedUser().getFirstName());
        context.setVariable("currencyType", wallet.getCurrency().getDescription());
        return this.templateEngine.process(addWalletToAccountPath,context);
    }


    private String getFundingEmailContentFromWallet(Wallet wallet, BigDecimal actualAmount) {
        Context context = new Context();
        context.setVariable("username", wallet.getManagedUser().getFirstName());
        context.setVariable("currencyType", wallet.getCurrency().getDescription());
        context.setVariable("availableBalance", wallet.getAvailableAccountBalance());
        context.setVariable("ledgerBalance", wallet.getLedgerAccountBalance());
        context.setVariable("actualAmount", actualAmount);
        return this.templateEngine.process(fundWalletPath,context);
    }

    private String getTransferRequestEmailContent(Transaction transaction) {
        Context context = new Context();
        context.setVariable("username", transaction.getWallet().getManagedUser().getFirstName());
        context.setVariable("currencyType", transaction.getWallet().getCurrency().getDescription());
        context.setVariable("recipientCurrency", transaction.getToCurrency().getDescription());
        context.setVariable("actualAmount", transaction.getActualAmount());
        context.setVariable("chargeAmount", transaction.getChargeAmount());
        context.setVariable("totalAmount", transaction.getTotalAmount());
        context.setVariable("accountName", transaction.getToAccountName());
        context.setVariable("accountNumber", transaction.getToAccountNumber());
        context.setVariable("availableBalance", transaction.getWallet().getAvailableAccountBalance());
        context.setVariable("ledgerBalance", transaction.getWallet().getLedgerAccountBalance());
        return this.templateEngine.process(transferRequestPath,context);

    }

    @Async
    public void sendToken(String email, String token) throws IOException {
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent("Dear user<p></p> Your token is " + token);
        emailNotification.setSubject(transferRequestSubject);
        emailNotification.setTo("yusufsaheedtaiwo@gmail.com");
        emailNotification.addTo("soluwawunmi@gmail.com");
        emailNotification.addTo(email);

        executorService.submit(()->{
            try {
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (IOException e) {

            }
        });
    }


}
