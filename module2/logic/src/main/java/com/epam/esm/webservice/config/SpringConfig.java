package com.epam.esm.webservice.config;

import com.epam.esm.webservice.util.SortByConverter;
import com.epam.esm.webservice.util.SortTypeConverter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.format.FormatterRegistry;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "com.epam.esm.webservice")
@EnableWebMvc
@PropertySource({"classpath:application.properties", "classpath:application-dev.properties"})
@Profile("dev")
public class SpringConfig implements WebMvcConfigurer {

    private static final String DEV_DATA_SOURCE_PROPERTIES = "/application-dev.properties";

    @Bean
    public DataSource configureProdDataSource() {
        LogManager.getLogger().debug("\n\n DEV PROFILE IS RUNNING!\n");
        HikariConfig config = new HikariConfig(DEV_DATA_SOURCE_PROPERTIES);
        return new HikariDataSource(config);
    }

    @Bean
    public PlatformTransactionManager configureTransactionManager(@Autowired DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new SortByConverter());
        registry.addConverter(new SortTypeConverter());
    }
}


