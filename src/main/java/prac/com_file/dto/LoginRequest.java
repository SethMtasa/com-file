package prac.com_file.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Record for user login requests.
 */
public record LoginRequest(@NotBlank String username, @NotBlank String password) {
}