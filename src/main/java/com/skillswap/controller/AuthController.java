package com.skillswap.controller;

import com.skillswap.dto.*;
import com.skillswap.entity.PasswordResetToken;
import com.skillswap.entity.User;
import com.skillswap.repository.PasswordResetTokenRepository;
import com.skillswap.repository.UserRepository;
import com.skillswap.security.JwtUtil;
//import com.skillswap.service.EmailService;
import com.skillswap.service.OTPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository tokenRepository;
    //private final EmailService emailService;
    private final OTPService otpService;

    // ==================== REGISTER ====================
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("Email already registered");
            }

            // Check if phone number already exists
            if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                return ResponseEntity.badRequest().body("Phone number already registered");
            }

            // Create new user
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setCity(request.getCity());
            user.setBio(request.getBio());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPhoneVerified(false);

            User savedUser = userRepository.save(user);

            // Send welcome email
            try {
                //emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getName());
            } catch (Exception e) {
                // Log error but don't fail registration
                System.err.println("Failed to send welcome email: " + e.getMessage());
            }

            // Generate token
            String token = jwtUtil.generateToken(savedUser.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponseDTO(token, savedUser));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Registration failed. Please try again.");
        }
    }

    // ==================== LOGIN ====================
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Invalid email or password");
            }

            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(new AuthResponseDTO(token, user));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // ==================== FORGOT PASSWORD ====================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with this email"));

            // Check if there's already a token
            tokenRepository.deleteByUserId(user.getId());

            // Generate token
            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
            PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
            tokenRepository.save(resetToken);

            // Send email
            try {
                //emailService.sendResetPasswordEmail(user.getEmail(), token);
            } catch (Exception e) {
                System.err.println("Failed to send reset email: " + e.getMessage());
                // Still return success to avoid exposing email existence
            }

            return ResponseEntity.ok("Password reset link sent to your email");

        } catch (IllegalArgumentException e) {
            // Don't reveal if email exists or not
            return ResponseEntity.ok("If your email is registered, you will receive a reset link");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send reset email. Please try again.");
        }
    }

    // ==================== RESET PASSWORD ====================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            // Validate passwords match
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match");
            }

            PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

            if (resetToken.isExpired()) {
                return ResponseEntity.badRequest().body("Token has expired");
            }

            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            // Delete the token after use
            tokenRepository.delete(resetToken);

            return ResponseEntity.ok("Password reset successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to reset password. Please try again.");
        }
    }

    // ==================== SEND OTP ====================
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOTP(@Valid @RequestBody SendOTPRequest request) {
        try {
            String otp = otpService.generateOTP(request.getPhoneNumber());

            // Log OTP for testing
            System.out.println("📱 OTP for " + request.getPhoneNumber() + ": " + otp);

            // Try to send via email if user exists
            userRepository.findByPhoneNumber(request.getPhoneNumber())
                    .ifPresent(user -> {
                        try {
                          //  emailService.sendOTPEmail(user.getEmail(), otp);
                        } catch (Exception e) {
                            System.err.println("Failed to send OTP email: " + e.getMessage());
                        }
                    });

            return ResponseEntity.ok("OTP sent successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send OTP. Please try again.");
        }
    }

    // ==================== LOGIN WITH OTP ====================
    @PostMapping("/login-with-otp")
    public ResponseEntity<?> loginWithOTP(@Valid @RequestBody LoginWithOTPRequest request) {
        try {
            // Verify OTP
            if (!otpService.verifyOTP(request.getPhoneNumber(), request.getOtp())) {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }

            // Find or create user
            User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setPhoneNumber(request.getPhoneNumber());
                        newUser.setName("User_" + request.getPhoneNumber().substring(6));
                        newUser.setPhoneVerified(true);
                        return userRepository.save(newUser);
                    });

            // Generate token
            String token = jwtUtil.generateToken(
                    user.getEmail() != null ? user.getEmail() : user.getPhoneNumber()
            );

            return ResponseEntity.ok(new AuthResponseDTO(token, user));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Login failed. Please try again.");
        }
    }
}