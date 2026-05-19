package com.codeTutor.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Converts Render's DATABASE_URL format (postgres://user:pass@host:port/db)
 * to the JDBC format Spring requires (jdbc:postgresql://host:port/db).
 * Auto-detects the correct JDBC driver based on the URL prefix.
 */
@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Value("${DATABASE_USERNAME:}")
    private String dbUsername;

    @Value("${DATABASE_PASSWORD:}")
    private String dbPassword;

    @Bean
    public DataSource dataSource() {
        String jdbcUrl = convertToJdbcUrl(databaseUrl);

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setDriverClassName(detectDriver(jdbcUrl));

        if (!dbUsername.isBlank()) {
            ds.setUsername(dbUsername);
        }
        if (!dbPassword.isBlank()) {
            ds.setPassword(dbPassword);
        }

        return ds;
    }

    private String detectDriver(String url) {
        if (url.startsWith("jdbc:h2:")) {
            return "org.h2.Driver";
        }
        return "org.postgresql.Driver";
    }

    private String convertToJdbcUrl(String url) {
        if (url.startsWith("jdbc:")) {
            return url;
        }

        String withoutScheme = url.replace("postgres://", "").replace("postgresql://", "");
        String[] userInfoAndRest = withoutScheme.split("@");
        String userInfo = userInfoAndRest[0];
        String hostAndDb = userInfoAndRest[1];

        String[] credentials = userInfo.split(":");
        String user = credentials[0];
        String password = credentials[1];

        return "jdbc:postgresql://" + hostAndDb + "?user=" + user + "&password=" + password + "&sslmode=require";
    }
}
