package org.graduatework.backend.utils;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private static EmailService ourInstance;

    private Session session;

    public static EmailService getInstance() {
        if (ourInstance == null) {
            ourInstance = new EmailService();
        }
        return ourInstance;
    }

    private EmailService() {
        Properties properties = new Properties();
        properties.putAll(System.getProperties());
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.starttls.required", true);
        properties.put("mail.smtp.socketFactory.port", 465);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.debug", false);
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 465);

        session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("placardnotifications@gmail.com", "password1234!");
            }
        });
    }

    public boolean sendEmail(String subject, String text, String address) {
        if (!address.matches("[^@]+@[^@]+")) {
            System.out.println("Wrong email address: " + address);
            return false;
        }
        try {
            MimeMessage message = new MimeMessage(session);
            InternetAddress internetAddress = new InternetAddress("placardnotifications@gmail.com");
            message.setFrom(internetAddress);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
            message.setSubject(subject);
            message.setContent(text, "text/html");
            Transport.send(message);
        } catch (AddressException e) {
            System.out.println("Wrong email address. " + e);
            return false;
        } catch (MessagingException e) {
            System.out.println("Failed to send email. " + e);
            return false;
        } catch (Exception e) {
            System.out.println("Unexpected exception: " + e);
            return false;
        }
        return true;
    }
}
