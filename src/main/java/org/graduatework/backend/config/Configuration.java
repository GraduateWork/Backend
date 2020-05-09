package org.graduatework.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@org.springframework.context.annotation.Configuration
@PropertySource("classpath:/config/application.properties")
public class Configuration {

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${datasource.url}")
    private String dataSourceUrl;

    @Value("${recommendationManager}")
    private String recommendationManager;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getDataSourceUrl() {
        return dataSourceUrl;
    }

    public void setDataSourceUrl(String dataSourceUrl) {
        this.dataSourceUrl = dataSourceUrl;
    }

    public String getRecommendationManager() {
        return recommendationManager;
    }

    public void setRecommendationManager(String recommendationManager) {
        this.recommendationManager = recommendationManager;
    }
}
