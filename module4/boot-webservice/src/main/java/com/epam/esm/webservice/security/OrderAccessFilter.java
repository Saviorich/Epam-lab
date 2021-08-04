package com.epam.esm.webservice.security;

import com.epam.esm.webservice.dto.UserDTO;
import com.epam.esm.webservice.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Component
@Log4j2
public class OrderAccessFilter implements Filter {

    private static final String USERS_RESOURCE_URI_PART = "/users/";
    private static final String ORDERS_RESOURCE_URI_PART = "/orders/";

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer";
    private static final String ADMIN = "ROLE_ADMIN";

    private final ApplicationUserDetailsService detailsService;
    private final UserService userService;

    @Autowired
    public OrderAccessFilter(ApplicationUserDetailsService detailsService, UserService userService) {
        this.detailsService = detailsService;
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String uri = ((HttpServletRequest) servletRequest).getRequestURI();
        if (uri.contains(USERS_RESOURCE_URI_PART) && uri.contains(ORDERS_RESOURCE_URI_PART)) {
            String headerValue = ((HttpServletRequest) servletRequest).getHeader(AUTHORIZATION);
            getBearerToken(headerValue).flatMap(detailsService::loadUserByJwtToken).ifPresent(userDetails -> {
                UserDTO user = userService.findByEmail(userDetails.getUsername());
                if (!parseUserIdFromUri(uri).equals(user.getId()) && !user.getRole().getRole().equals(ADMIN)) {
                    sendError(servletResponse);
                }
            });
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void sendError(ServletResponse servletResponse) {
        try {
            ((HttpServletResponse) servletResponse).sendError(SC_FORBIDDEN);
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException("Unable to send error");
        }
    }

    private Integer parseUserIdFromUri(String uri) {
        char nextSlash = '/';
        String cropped = uri.replace(USERS_RESOURCE_URI_PART, "");
        return Integer.parseInt(cropped.substring(0, cropped.indexOf(nextSlash)));
    }

    private Optional<String> getBearerToken(String headerVal) {
        if (headerVal != null && headerVal.startsWith(BEARER)) {
            return Optional.of(headerVal.replace(BEARER, "").trim());
        }
        return Optional.empty();
    }
}
