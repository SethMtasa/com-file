package prac.com_file.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prac.com_file.dto.*;
import prac.com_file.service.UserService;


import java.util.List;


@RestController
@CrossOrigin
public class AuthenticationController {
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse<String>> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(userService.authenticateUser(loginRequest));
    }


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse<String>> registerUser(@RequestBody RegistrationRequest registrationRequest) throws Exception {
        return ResponseEntity.ok(userService.registerUser(registrationRequest));
    }


    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByUsername(@PathVariable String username) {
        ApiResponse<UserResponseDto> apiResponse = userService.getUserByUsername(username, true);
        return new ResponseEntity<>(apiResponse, apiResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllActiveUsers() {
        ApiResponse<List<UserResponseDto>> apiResponse = userService.getAllActiveUsers();
        return new ResponseEntity<>(apiResponse, apiResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PutMapping("/user/update/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @RequestBody RegistrationRequest updateRequest) {
        ApiResponse<UserResponseDto> apiResponse = userService.updateUser(id, updateRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

}
