
package com.chatty.auth.service;

import com.chatty.auth.client.CustomerServiceClient;
import com.chatty.auth.dto.*;
import com.chatty.auth.exception.UserAlreadyExistsException;
import com.chatty.auth.exception.UserNotFoundException;
import com.chatty.auth.model.Role;
import com.chatty.auth.model.User;
import com.chatty.auth.repository.UserRepository;
import com.chatty.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomerServiceClient customerServiceClient;
    private final EmailService emailService;

    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        log.info("Registering user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }
        
        // Verify customer exists
        customerServiceClient.getCustomerById(request.getCustomerId());
        
        String token = UUID.randomUUID().toString();
        
        User user = User.builder()
                .customerId(request.getCustomerId())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .roles(request.getRoles() != null ? request.getRoles() : Set.of(Role.ROLE_CUSTOMER))
                .verified(false)
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .verificationToken(token)
                .verificationTokenExpiry(LocalDateTime.now().plusHours(24))
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), token);
        
        log.info("User registered successfully: {}", savedUser.getId());
        
        return UserRegistrationResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .customerId(savedUser.getCustomerId())
                .success(true)
                .message("User registered successfully. Please check your email for verification.")
                .build();
    }
    
    public boolean verifyEmail(VerifyEmailRequest request) {
        log.info("Verifying email: {}", request.getEmail());
        
        User user = userRepository.findByEmailAndVerificationToken(request.getEmail(), request.getToken())
                .orElseThrow(() -> new UserNotFoundException("Invalid verification token"));
                
        if (LocalDateTime.now().isAfter(user.getVerificationTokenExpiry())) {
            throw new RuntimeException("Verification token expired");
        }
        
        user.setVerified(true);
        user.setActive(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        // Verify customer as well
        customerServiceClient.verifyCustomer(user.getCustomerId());
        customerServiceClient.activateCustomer(user.getCustomerId());
        
        log.info("Email verified successfully: {}", user.getEmail());
        
        return true;
    }
    
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());
        
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> role.name())
                        .toList())
                .build();
        
        String token = jwtService.generateToken(userDetails);
        
        log.info("User logged in successfully: {}", user.getEmail());
        
        return LoginResponse.builder()
                .token(token)
                .customerId(user.getCustomerId())
                .email(user.getEmail())
                .name(user.getName())
                .message("Login successful")
                .build();
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
