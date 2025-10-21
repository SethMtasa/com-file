package prac.com_file.service;

// File: AuthService.java
// Package: prac.lease.service;

import prac.com_file.dto.AuthenticationResponse;
import prac.com_file.dto.LoginRequest;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.RegistrationRequest;

/**
 * Service interface for handling authentication and user registration.
 */
public interface AuthService {
    /**
     * Authenticates a user and generates a JWT token.
     * @param loginRequest The DTO containing the username and password.
     * @return AuthenticationResponse containing success status, message, and JWT token.
     */
    AuthenticationResponse login(LoginRequest loginRequest);

    /**
     * Registers a new user in the system.
     * @param registrationRequest The DTO containing user registration details.
     * @return ApiResponse indicating success or failure.
     */
    ApiResponse register(RegistrationRequest registrationRequest);
}
