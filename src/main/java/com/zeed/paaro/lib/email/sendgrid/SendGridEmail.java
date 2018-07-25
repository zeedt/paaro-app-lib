package com.zeed.paaro.lib.email.sendgrid;

import com.sendgrid.*;
import com.zeed.paaro.lib.models.EmailNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmail {

    @Autowired
    private SendGrid sendGrid;

    @Value("${email.sender.address:Paaro@gmail.com}")
    private String senderEmail;


    // Minimum required to send an email
    public static Mail buildHelloEmail() throws IOException {
        Email from = new Email("test@example.com");
        String subject = "Hello World from the SendGrid Java Library";
        Email to = new Email("yusufsaheedtaiwo@gmail.com");
        Content content = new Content("text/plain", "some text here");
        // Note that when you use this constructor an initial personalization object
        // is created for you. It can be accessed via
        // mail.personalization.get(0) as it is a List object
        Mail mail = new Mail(from, subject, to, content);
        Email email = new Email("soluwawunmi@gmail.com");
        mail.personalization.get(0).addTo(email);

        return mail;
    }

    public static void baselineExample() throws IOException {
        SendGrid sg = new SendGrid("");
        sg.addRequestHeader("X-Mock", "true");

        Request request = new Request();
        Mail helloWorld = buildHelloEmail();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(helloWorld.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }

    public void sendEmailWithNoAttachment(EmailNotification emailNotification) throws IOException {

        Email sender = new Email(senderEmail);
        Email receiver = new Email(emailNotification.getTo());
        Content content = new Content("text/html",emailNotification.getContent());

        Mail mail = new Mail(sender, emailNotification.getSubject(), receiver, content);

        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);

    }

    public static void main(String[] args) throws IOException {
        baselineExample();
    }
}
