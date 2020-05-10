package org.graduatework.backend.services;

import org.graduatework.backend.config.Configuration;
import org.graduatework.backend.db.DBAdaptor;
import org.graduatework.backend.db.DBAdaptorInfo;

public abstract class BaseService {

    protected Configuration config;
    protected DBAdaptorInfo dbAdaptor;

    public BaseService(Configuration config) {
        this.config = config;
        dbAdaptor = new DBAdaptor(config.getJdbcUrl());
    }

    public BaseService(Configuration config, DBAdaptorInfo dbAdaptor) {
        this.config = config;
        this.dbAdaptor = dbAdaptor;
    }
}
