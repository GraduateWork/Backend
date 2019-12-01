package org.graduatework.backend.services;

import org.graduatework.backend.config.Configuration;
import org.graduatework.backend.db.DBAdaptor;
import org.graduatework.backend.utils.EmailService;
import org.graduatework.backend.utils.KeyStore;
import org.graduatework.backend.dto.DBUser;
import org.graduatework.backend.dto.VerificationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String OTP_SUBJECT = "Placard One-Time Password";
    private static final String OTP_TEXT = "Your One-Time Password: ";
    private static final long MILLISECONDS_IN_MINUTE = 1000 * 60;
    private Configuration config;
    private DBAdaptor dbAdaptor;
    private PasswordEncoder encoder;

    @Autowired
    public AuthService(Configuration config) {
        this.config = config;
        dbAdaptor = new DBAdaptor(config.getJdbcUrl());
        encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private void sendOTP(DBUser user) {
        String code = KeyStore.generateRandomCode(4);
        if (EmailService.getInstance().sendEmail(OTP_SUBJECT, OTP_TEXT + code, user.getEmail())) {
            KeyStore.getInstance().setCode(user.getUsername(), new VerificationCode(code, System.currentTimeMillis()));
        } else {
            throw new IllegalArgumentException("Cannot send OTP using user email: " + user.getEmail());
        }
    }

    public void registerUser(DBUser user) throws IllegalArgumentException {
        user.setPassword(encoder.encode(user.getPassword()));
        //dbAdaptor.insertUser(user);
        sendOTP(user);
    }

    public void activateUser(String username, String code) throws IllegalArgumentException, IllegalStateException {
        DBUser user = dbAdaptor.getUser(username);
        VerificationCode verificationCode = KeyStore.getInstance().getCode(username);
        if (System.currentTimeMillis() - verificationCode.getCreationTime() > 60 * MILLISECONDS_IN_MINUTE) {
            sendOTP(user);
            throw new IllegalStateException("OTP is expired for user: " + user.getUsername());
        } else {
            if (verificationCode.getCode().equals(code)) {
                dbAdaptor.activateUser(username);
            } else {
                throw new IllegalArgumentException("Cannot verify OTP for user: " + username);
            }
        }
    }
}
