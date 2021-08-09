package com.epam.esm.webservice.security;

import com.epam.esm.webservice.dto.UserDTO;
import com.epam.esm.webservice.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.security.core.userdetails.User.withUsername;

@Component
@Log4j2
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final JwtProvider provider;

    @Autowired
    public ApplicationUserDetailsService(UserService userService, JwtProvider provider) {
        this.userService = userService;
        this.provider = provider;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = userService.findByEmail(username);
        return withUsername(user.getEmail())
                .password(user.getHash())
                .authorities(user.getRole().getRole())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public Optional<UserDetails> loadUserByJwtToken(String jwtToken) {
        if (!provider.validateToken(jwtToken)) {
            return Optional.empty();
        }
        return Optional.of(
                withUsername(provider.getLogin(jwtToken))
                        .authorities(provider.getRole(jwtToken))
                        .password("")
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .build());
    }
}
