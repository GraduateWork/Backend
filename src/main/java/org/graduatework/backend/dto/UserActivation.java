package org.graduatework.backend.dto;

public class UserActivation {

    private String username;
    private String code;

    public UserActivation() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
