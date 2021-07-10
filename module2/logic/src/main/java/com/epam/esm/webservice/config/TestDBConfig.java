package com.epam.esm.webservice.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.epam.esm.webservice")
@PropertySource({"classpath:application.properties", "classpath:application-test.properties"})
@Profile("test")
public class TestDBConfig {

    private static final String TEST_DATA_SOURCE_PROPERTIES = "/application-test.properties";

    @Bean
    public DataSource configureTestDataSource() {
        LogManager.getLogger().debug("\n\n TEST PROFILE IS RUNNING!\n");
        HikariConfig config = new HikariConfig(TEST_DATA_SOURCE_PROPERTIES);
        return new HikariDataSource(config);
    }

    @Bean
    public PlatformTransactionManager configureTransactionManager(@Autowired DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

