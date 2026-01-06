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
        if (user.isPresent() && user.get().isActiveStatus() && user.get().getEnabled()) {
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
            if (existingUser.isPresent() && !existingUser.get().isActiveStatus()) {
                User user = existingUser.get();
                user.setActiveStatus(true);
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
            user.setActiveStatus(true);
            user.setEnabled(true);

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
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if ((activeStatus && user.isActiveStatus()) || !activeStatus) {
                return new ApiResponse<>(true, "User found", new UserResponseDto(user));
            }
        }
        return new ApiResponse<>(false, "User not found with username: " + username, null);
    }

    @Override
    public ApiResponse<String> deactivateUser(Long id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.isActiveStatus()) {
                    user.setActiveStatus(false);
                    user.setEnabled(false);
                    userRepository.save(user);
                    return new ApiResponse<>(true, "User deactivated successfully.", null);
                } else {
                    return new ApiResponse<>(false, "User is already inactive.", null);
                }
            } else {
                return new ApiResponse<>(false, "User not found.", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error deactivating user: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<String> activateUser(Long id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (!user.isActiveStatus()) {
                    user.setActiveStatus(true);
                    user.setEnabled(true);
                    userRepository.save(user);
                    return new ApiResponse<>(true, "User activated successfully.", null);
                } else {
                    return new ApiResponse<>(false, "User is already active.", null);
                }
            } else {
                return new ApiResponse<>(false, "User not found.", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error activating user: " + e.getMessage(), null);
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
    public ApiResponse<List<UserResponseDto>> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        if (!allUsers.isEmpty()) {
            List<UserResponseDto> userDtos = allUsers.stream()
                    .map(UserResponseDto::new)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "All users retrieved successfully", userDtos);
        } else {
            return new ApiResponse<>(false, "No users found", null);
        }
    }

    @Override
    public ApiResponse<List<UserResponseDto>> getInactiveUsers() {
        List<User> inactiveUsers = userRepository.findByActiveStatus(false);
        if (!inactiveUsers.isEmpty()) {
            List<UserResponseDto> userDtos = inactiveUsers.stream()
                    .map(UserResponseDto::new)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Inactive users retrieved successfully", userDtos);
        } else {
            return new ApiResponse<>(false, "No inactive users found", null);
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

    @Override
    public ApiResponse<String> deleteUser(Long id) {
        // Keep for backward compatibility, but implement as deactivate
        return deactivateUser(id);
    }

    // Unique methods for this project - implement these
    @Override
    public List<User> findAllActiveKARs() {
        return userRepository.findAllActiveKARs();
    }

    @Override
    public List<User> findByNameContainingIgnoreCase(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<User> findByRoleNameAndActiveStatus(String roleName) {
        return userRepository.findByRoleNameAndActiveStatus(roleName);
    }

    @Override
    public long countActiveKARs() {
        return userRepository.countActiveKARs();
    }

    @Override
    public List<User> findUsersWithAssignedFiles() {
        return userRepository.findUsersWithAssignedFiles();
    }

    private AuthenticationResponse<String> validateUser(String username, String email) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().isActiveStatus()) {
            return new AuthenticationResponse<>(false, "Username already exists.", null);
        }
        Optional<User> emailUser = userRepository.findByEmail(email);
        if (emailUser.isPresent() && emailUser.get().isActiveStatus()) {
            return new AuthenticationResponse<>(false, "Email already exists.", null);
        }
        return new AuthenticationResponse<>(true, "", null);
    }
}