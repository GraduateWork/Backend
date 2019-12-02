package org.graduatework.backend.services;

import org.graduatework.backend.config.Configuration;
import org.graduatework.backend.db.DBAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SimpleUserDetailsService extends BaseService implements UserDetailsService {

    @Autowired
    public SimpleUserDetailsService(Configuration config) {
        super(config);
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
