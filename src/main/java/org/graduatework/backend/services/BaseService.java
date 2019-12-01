package org.graduatework.backend.services;

import org.graduatework.backend.config.Configuration;
import org.graduatework.backend.db.DBAdaptor;

public class BaseService {

    protected Configuration config;
    protected DBAdaptor dbAdaptor;

    public BaseService(Configuration config) {
        this.config = config;
        dbAdaptor = new DBAdaptor(config.getJdbcUrl());
    }
}
