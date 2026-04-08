package com.codeTutor.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Converts Render's DATABASE_URL format (postgres://user:pass@host:port/db)
 * to the JDBC format Spring requires (jdbc:postgresql://host:port/db).
 */
@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        String jdbcUrl = convertToJdbcUrl(databaseUrl);

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }

    private String convertToJdbcUrl(String url) {
        // Already in jdbc format — return as is
        if (url.startsWith("jdbc:")) {
            return url;
        }

        // Convert postgres://user:password@host:port/db → jdbc:postgresql://host:port/db?user=user&password=password
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
