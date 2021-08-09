package com.epam.esm.webservice.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@Component
@Log4j2
public class JwtFilter extends GenericFilterBean {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer";

    private final ApplicationUserDetailsService service;

    @Autowired
    public JwtFilter(ApplicationUserDetailsService service) {
        this.service = service;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("Process request to check for a JSON Web Token");
        String headerValue = ((HttpServletRequest) servletRequest).getHeader(AUTHORIZATION);
        getBearerToken(headerValue).ifPresent(token-> {
            service.loadUserByJwtToken(token).ifPresent(userDetails -> {
                SecurityContextHolder.getContext().setAuthentication(
                        new PreAuthenticatedAuthenticationToken(userDetails, "", userDetails.getAuthorities()));
            });
        });
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Optional<String> getBearerToken(String headerVal) {
        if (headerVal != null && headerVal.startsWith(BEARER)) {
            return Optional.of(headerVal.replace(BEARER, "").trim());
        }
        return Optional.empty();
    }
}
