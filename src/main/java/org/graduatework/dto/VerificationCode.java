package org.graduatework.dto;

public class VerificationCode {

    private String code;
    private long creationTime;

    public VerificationCode(String code, long creationTime) {
        this.code = code;
        this.creationTime = creationTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}
