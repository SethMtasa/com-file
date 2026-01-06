package prac.com_file.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prac.com_file.dto.*;
import prac.com_file.model.User;
import prac.com_file.service.UserService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class AuthenticationController {
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    // Existing endpoints from previous project
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse<String>> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(userService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse<String>> registerUser(@RequestBody RegistrationRequest registrationRequest) throws Exception {
        return ResponseEntity.ok(userService.registerUser(registrationRequest));
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<ApiResponse<String>> activateUser(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByUsername(@PathVariable String username) {
        ApiResponse<UserResponseDto> apiResponse = userService.getUserByUsername(username, true);
        return new ResponseEntity<>(apiResponse, apiResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllActiveUsers() {
        ApiResponse<List<UserResponseDto>> apiResponse = userService.getAllActiveUsers();
        return new ResponseEntity<>(apiResponse, apiResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        ApiResponse<List<UserResponseDto>> apiResponse = userService.getAllUsers();
        return new ResponseEntity<>(apiResponse, apiResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/inactive")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getInactiveUsers() {
        ApiResponse<List<UserResponseDto>> apiResponse = userService.getInactiveUsers();
        return new ResponseEntity<>(apiResponse, apiResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @RequestBody RegistrationRequest updateRequest) {
        ApiResponse<UserResponseDto> apiResponse = userService.updateUser(id, updateRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    // Unique endpoints for this project
    @GetMapping("/active-kars")
    public ResponseEntity<List<User>> getAllActiveKARs() {
        List<User> activeKARs = userService.findAllActiveKARs();
        return new ResponseEntity<>(activeKARs, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
        List<User> users = userService.findByNameContainingIgnoreCase(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<User>> getUsersByRoleAndActive(@PathVariable String roleName) {
        List<User> users = userService.findByRoleNameAndActiveStatus(roleName);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/active-kars/count")
    public ResponseEntity<Long> countActiveKARs() {
        long count = userService.countActiveKARs();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/with-assigned-files")
    public ResponseEntity<List<User>> getUsersWithAssignedFiles() {
        List<User> users = userService.findUsersWithAssignedFiles();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Keep for backward compatibility
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}