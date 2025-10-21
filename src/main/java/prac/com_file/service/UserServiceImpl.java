package prac.com_file.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import prac.com_file.dto.AuthenticationResponse;
import prac.com_file.dto.LoginRequest;
import prac.com_file.dto.RegistrationRequest;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.UserResponseDto;
import prac.com_file.model.Role;
import prac.com_file.model.User;
import prac.com_file.repository.RoleRepository;
import prac.com_file.repository.UserRepository;
import prac.com_file.security.JwtService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
    }

    @Override
    public AuthenticationResponse<String> authenticateUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );
        Optional<User> user = userRepository.findByUsername(loginRequest.username());
        if (user.isPresent()) {
            String token = jwtService.generateToken(user.get());
            return new AuthenticationResponse<>(true, "success", token);
        }
        return new AuthenticationResponse<>(false, "failed", null);
    }

    @Override
    public AuthenticationResponse<String> registerUser(RegistrationRequest registrationRequest) {
        String username = registrationRequest.username();
        String email = registrationRequest.email();

        AuthenticationResponse<String> validationResponse = validateUser(username, email);

        if (!validationResponse.success()) {
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent() && !existingUser.get().isEnabled()) {
                User user = existingUser.get();
                user.setEnabled(true);
                userRepository.save(user);
                return new AuthenticationResponse<>(true, "User activated successfully.", null);
            } else {
                return validationResponse;
            }
        } else {
            Optional<Role> userRole = roleRepository.findByName(registrationRequest.role());
            if (userRole.isEmpty()) {
                return new AuthenticationResponse<>(false, "Invalid role provided.", null);
            }

            User user = new User();
            user.setFirstName(registrationRequest.firstName());
            user.setLastName(registrationRequest.lastName());
            user.setEmail(email);
            user.setUsername(username);
            user.setRole(userRole.get());

            try {
                userRepository.save(user);
                return new AuthenticationResponse<>(true, "User created successfully.", null);
            } catch (Exception e) {
                return new AuthenticationResponse<>(false, "Failed to register user: " + e.getMessage(), null);
            }
        }
    }


    @Override
    public ApiResponse<UserResponseDto> getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.map(user -> new ApiResponse<>(true, "User found", new UserResponseDto(user)))
                .orElseGet(() -> new ApiResponse<>(false, "User not found with ID: " + id, null));
    }

    @Override
    public ApiResponse<UserResponseDto> getUserByUsername(String username, boolean activeStatus) {
        Optional<User> optionalUser = userRepository.findByUsernameAndActiveStatus(username, activeStatus);
        return optionalUser.map(user -> new ApiResponse<>(true, "User found", new UserResponseDto(user)))
                .orElseGet(() -> new ApiResponse<>(false, "User not found with username: " + username, null));
    }


    @Override
    public ApiResponse<String> deleteUser(Long id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setEnabled(false);
                userRepository.save(user);
                return new ApiResponse<>(true, "User deleted successfully.", null);
            } else {
                return new ApiResponse<>(false, "User not found.", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error deleting user: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<UserResponseDto>> getAllActiveUsers() {
        List<User> activeUsers = userRepository.findByActiveStatus(true);
        if (!activeUsers.isEmpty()) {
            List<UserResponseDto> userDtos = activeUsers.stream()
                    .map(UserResponseDto::new)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Active users retrieved successfully", userDtos);
        } else {
            return new ApiResponse<>(false, "No active users found", null);
        }
    }

    @Override
    public ApiResponse<UserResponseDto> updateUser(Long id, RegistrationRequest updateRequest) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                return new ApiResponse<>(false, "User not found.", null);
            }
            User user = userOptional.get();

            if (updateRequest.firstName() != null) {
                user.setFirstName(updateRequest.firstName());
            }
            if (updateRequest.lastName() != null) {
                user.setLastName(updateRequest.lastName());
            }
            if (updateRequest.email() != null) {
                user.setEmail(updateRequest.email());
            }
            if (updateRequest.username() != null) {
                user.setUsername(updateRequest.username());
            }

            if (updateRequest.role() != null) {
                Optional<Role> newRoleOptional = roleRepository.findByName(updateRequest.role());
                if (newRoleOptional.isEmpty()) {
                    return new ApiResponse<>(false, "Invalid role provided.", null);
                }
                user.setRole(newRoleOptional.get());
            }

            User savedUser = userRepository.save(user);
            return new ApiResponse<>(true, "User updated successfully.", new UserResponseDto(savedUser));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error updating user: " + e.getMessage(), null);
        }
    }

    private AuthenticationResponse<String> validateUser(String username, String email) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return new AuthenticationResponse<>(false, "Username already exists.", null);
        }
        Optional<User> emailUser = userRepository.findByEmail(email);
        if (emailUser.isPresent()) {
            return new AuthenticationResponse<>(false, "Email already exists.", null);
        }
        return new AuthenticationResponse<>(true, "", null);
    }
}