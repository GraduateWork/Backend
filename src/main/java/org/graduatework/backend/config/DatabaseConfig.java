package org.graduatework.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    private static DataSource dataSource;

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        HikariDataSource basicDataSource = new HikariDataSource();
        basicDataSource.setJdbcUrl(dbUrl);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
        basicDataSource.setMaxLifetime(180000);
        basicDataSource.setIdleTimeout(60000);
        basicDataSource.setMaximumPoolSize(30);
        dataSource = basicDataSource;

        return basicDataSource;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
