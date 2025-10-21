package prac.com_file.service;


//import prac.file.dto.*;
import prac.com_file.dto.*;

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
     * Retrieves a list of all users.
     * @return ApiResponse containing a list of UserResponseDto.
     */
    ApiResponse<List<UserResponseDto>> getAllActiveUsers();

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
     * Updates an existing user.
     * @param id The ID of the user to update.
     * @param editUserRequest The DTO with the updated user details.
     * @return ApiResponse indicating success or failure.
     */
    ApiResponse<UserResponseDto> updateUser(Long id, RegistrationRequest editUserRequest);



    /**
     * Deletes a user by their ID.
     * @param id The ID of the user to delete.
     * @return ApiResponse indicating success or failure.
     */
    ApiResponse<String> deleteUser(Long id);
}
