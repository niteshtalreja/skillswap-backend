package com.skillswap.service;

import com.skillswap.entity.User;
import com.skillswap.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);

        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found with username or email: " + username);
                });

        if (!user.getIsActive()) {
            log.warn("User is inactive: {}", username);
            throw new UsernameNotFoundException("User account is not active");
        }

        log.debug("User details loaded successfully: {}", username);
        return user;
    }
}
