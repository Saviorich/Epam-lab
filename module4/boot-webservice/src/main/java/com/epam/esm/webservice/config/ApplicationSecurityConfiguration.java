package com.epam.esm.webservice.config;

import com.epam.esm.webservice.security.JwtFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String CERTIFICATE_URI = "/certificates/**";
    private static final String LOGIN_URI = "/users/login";
    private static final String REGISTER_URI = "/users/register";

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(CERTIFICATE_URI).permitAll()
                .antMatchers(LOGIN_URI).permitAll()
                .antMatchers(REGISTER_URI).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
