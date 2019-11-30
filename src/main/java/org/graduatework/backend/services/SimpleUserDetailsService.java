package org.graduatework.backend.services;

import org.graduatework.backend.config.Configuration;
import org.graduatework.backend.db.DBAdaptor;
import org.graduatework.dto.DBUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SimpleUserDetailsService implements UserDetailsService {

    private Configuration config;
    private DBAdaptor dbAdaptor;

    @Autowired
    public SimpleUserDetailsService(Configuration config) {
        this.config = config;
        dbAdaptor = new DBAdaptor(config.getJdbcUrl());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return dbAdaptor.getUser(username);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException(e.getLocalizedMessage());
        }
    }
}
