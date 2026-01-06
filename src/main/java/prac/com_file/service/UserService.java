package prac.com_file.service;

import prac.com_file.dto.*;
import prac.com_file.model.User;

import java.util.List;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {
    /**
     * Authenticates a user and generates a JWT token.
     * @param loginRequest The DTO containing the username and password.
     * @return AuthenticationResponse containing success status, message, and JWT token.
     */
    AuthenticationResponse<String>  authenticateUser(LoginRequest loginRequest);

    /**
     * Registers a new user in the system.
     * @param registrationRequest The DTO containing user registration details.
     * @return AuthenticationResponse indicating success or failure.
     */
    AuthenticationResponse<String>  registerUser(RegistrationRequest registrationRequest);

    /**
     * Retrieves a user by their ID.
     * @param id The ID of the user.
     * @return ApiResponse containing the UserResponseDto.
     */
    ApiResponse<UserResponseDto> getUserById(Long id);

    /**
     * Retrieves a user by their username.
     * @param username The username of the user.
     * @return ApiResponse containing the UserResponseDto.
     */
    ApiResponse<UserResponseDto> getUserByUsername(String username, boolean activeStatus);

    /**
     * Deactivates a user (soft delete).
     * @param id The ID of the user to deactivate.
     * @return ApiResponse indicating success or failure.
     */
    ApiResponse<String> deactivateUser(Long id);

    /**
     * Activates a previously deactivated user.
     * @param id The ID of the user to activate.
     * @return ApiResponse indicating success or failure.
     */
    ApiResponse<String> activateUser(Long id);

    /**
     * Retrieves a list of all active users.
     * @return ApiResponse containing a list of UserResponseDto.
     */
    ApiResponse<List<UserResponseDto>> getAllActiveUsers();

    /**
     * Retrieves a list of all users (both active and inactive).
     * @return ApiResponse containing a list of UserResponseDto.
     */
    ApiResponse<List<UserResponseDto>> getAllUsers();

    /**
     * Retrieves a list of all inactive users.
     * @return ApiResponse containing a list of UserResponseDto.
     */
    ApiResponse<List<UserResponseDto>> getInactiveUsers();

    /**
     * Updates an existing user.
     * @param id The ID of the user to update.
     * @param editUserRequest The DTO with the updated user details.
     * @return ApiResponse indicating success or failure.
     */
    ApiResponse<UserResponseDto> updateUser(Long id, RegistrationRequest editUserRequest);

    /**
     * Deletes a user by their ID (kept for backward compatibility).
     * @param id The ID of the user to delete.
     * @return ApiResponse indicating success or failure.
     */
    ApiResponse<String> deleteUser(Long id);

    // Unique methods for this project - keep these as they are
    List<User> findAllActiveKARs();

    List<User> findByNameContainingIgnoreCase(String name);

    List<User> findByRoleNameAndActiveStatus(String roleName);

    long countActiveKARs();

    List<User> findUsersWithAssignedFiles();
}