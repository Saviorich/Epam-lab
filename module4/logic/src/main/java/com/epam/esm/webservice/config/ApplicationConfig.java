package com.epam.esm.webservice.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Profile("prod")
@EnableTransactionManagement
public class ApplicationConfig {

    private static final String DATA_SOURCE_PROPERTIES = "/application-prod.properties";
    private static final String PACKAGES_TO_SCAN = "com.epam.esm.webservice";

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig(DATA_SOURCE_PROPERTIES);
        return new HikariDataSource(config);
    }

    @Bean(name="entityManagerFactory")
    public LocalSessionFactoryBean getSessionFactory(@Autowired DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan(PACKAGES_TO_SCAN);

        return sessionFactory;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Autowired SessionFactory sessionFactory) {
        return new JpaTransactionManager(sessionFactory);
    }
}
