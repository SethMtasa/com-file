package prac.com_file.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Record for user registration requests.
 */
public record RegistrationRequest(@NotBlank String firstName,
                                  @NotBlank String lastName,
                                  @Email @NotBlank String email,
                                  @NotBlank String username,
                                  @NotBlank String role) {
}