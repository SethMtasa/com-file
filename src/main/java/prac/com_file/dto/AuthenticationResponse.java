package prac.com_file.dto;

/**
 * Record for JWT authentication responses.
 */
public record AuthenticationResponse<String>(boolean success, String message, String token) {
}