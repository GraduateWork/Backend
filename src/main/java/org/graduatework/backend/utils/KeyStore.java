package org.graduatework.backend.utils;

import org.graduatework.backend.dto.VerificationCode;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class KeyStore {

    private static KeyStore ourInstance;

    private final Map<String, VerificationCode> codes = new HashMap<>();

    public static String generateRandomCode(int length) {
        SecureRandom r = new SecureRandom();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            key.append(Character.toString((char) (r.nextInt(26) + 97)));
        }
        return key.toString();
    }

    public static KeyStore getInstance() {
        if (ourInstance == null) {
            ourInstance = new KeyStore();
        }
        return ourInstance;
    }

    public void setCode(String username, VerificationCode code) {
        codes.put(username, code);
    }

    public VerificationCode getCode(String username) {
        return codes.get(username);
    }
}
