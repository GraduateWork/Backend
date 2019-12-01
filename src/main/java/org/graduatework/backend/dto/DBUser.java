package org.graduatework.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class DBUser implements UserDetails {

    private String username;
    private String email;
    private String password;
    private boolean isActivated = false;
    private long creationTime;

    public DBUser() {
    }

    public DBUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isActivated = false;
    }

    public DBUser(String username, String email, String password, boolean isActivated, long creationTime) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isActivated = isActivated;
        this.creationTime = creationTime;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return isActivated;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    @JsonIgnore
    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}
