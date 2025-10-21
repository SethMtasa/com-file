package prac.com_file.dto;

/**
 * Generic record for standardized API responses.
 * @param <T> The type of the response body.
 */
public record ApiResponse<T>(boolean success, String message, T body) {
}
