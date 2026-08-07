package com.skillswap.service;

import com.skillswap.dto.*;
import com.skillswap.entity.Role;
import com.skillswap.entity.Token;
import com.skillswap.entity.User;
import com.skillswap.repository.RoleRepository;
import com.skillswap.repository.TokenRepository;
import com.skillswap.repository.UserRepository;
import com.skillswap.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Attempting to register user with email: {}", registerRequest.getEmail());

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("Registration failed: Username already exists: {}", registerRequest.getUsername());
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: Email already exists: {}", registerRequest.getEmail());
            throw new RuntimeException("Email already exists");
        }

        // Get default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name("USER")
                            .description("Default user role")
                            .build();
                    return roleRepository.save(role);
                });

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .isActive(true)
                .isEmailVerified(false)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        String accessToken = jwtUtil.generateToken(savedUser.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getUsername());

        // Save tokens
        Token accessTokenEntity = Token.builder()
                .tokenValue(accessToken)
                .tokenType(Token.TokenType.BEARER)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtExpirationMs / 1000))
                .isRevoked(false)
                .isExpired(false)
                .user(savedUser)
                .build();

        Token refreshTokenEntity = Token.builder()
                .tokenValue(refreshToken)
                .tokenType(Token.TokenType.REFRESH)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .isRevoked(false)
                .isExpired(false)
                .user(savedUser)
                .build();

        tokenRepository.save(accessTokenEntity);
        tokenRepository.save(refreshTokenEntity);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .user(convertToDto(savedUser))
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Attempting to login user: {}", loginRequest.getUsernameOrEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            User user = userRepository.findByUsernameOrEmail(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getUsernameOrEmail()
            ).orElseThrow(() -> new RuntimeException("User not found"));

            // Update last login time
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            String accessToken = jwtUtil.generateToken(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            // Save tokens
            Token accessTokenEntity = Token.builder()
                    .tokenValue(accessToken)
                    .tokenType(Token.TokenType.BEARER)
                    .expiresAt(LocalDateTime.now().plusSeconds(jwtExpirationMs / 1000))
                    .isRevoked(false)
                    .isExpired(false)
                    .user(user)
                    .build();

            Token refreshTokenEntity = Token.builder()
                    .tokenValue(refreshToken)
                    .tokenType(Token.TokenType.REFRESH)
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .isRevoked(false)
                    .isExpired(false)
                    .user(user)
                    .build();

            tokenRepository.save(accessTokenEntity);
            tokenRepository.save(refreshTokenEntity);

            log.info("User logged in successfully: {}", user.getUsername());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtExpirationMs / 1000)
                    .user(convertToDto(user))
                    .message("Login successful")
                    .build();

        } catch (AuthenticationException e) {
            log.error("Login failed for user: {}", loginRequest.getUsernameOrEmail());
            throw new RuntimeException("Invalid username or password");
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Attempting to refresh token");

        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            log.warn("Invalid or expired refresh token");
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String username = jwtUtil.extractUsername(request.getRefreshToken());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtUtil.generateToken(user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Save new tokens
        Token accessTokenEntity = Token.builder()
                .tokenValue(newAccessToken)
                .tokenType(Token.TokenType.BEARER)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtExpirationMs / 1000))
                .isRevoked(false)
                .isExpired(false)
                .user(user)
                .build();

        Token refreshTokenEntity = Token.builder()
                .tokenValue(newRefreshToken)
                .tokenType(Token.TokenType.REFRESH)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .isRevoked(false)
                .isExpired(false)
                .user(user)
                .build();

        tokenRepository.save(accessTokenEntity);
        tokenRepository.save(refreshTokenEntity);

        log.info("Token refreshed successfully for user: {}", username);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .user(convertToDto(user))
                .message("Token refreshed successfully")
                .build();
    }

    public void logout(String username) {
        log.info("Logging out user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Revoke all tokens for this user
        tokenRepository.findValidTokensByUser(user).forEach(token -> {
            token.setIsRevoked(true);
            tokenRepository.save(token);
        });

        log.info("User logged out successfully: {}", username);
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .bio(user.getBio())
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.getIsActive())
                .isEmailVerified(user.getIsEmailVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
