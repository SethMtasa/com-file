package prac.com_file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for scenarios where a requested resource is not found.
 * <p>
 * This exception extends {@link RuntimeException}, making it an unchecked exception.
 * The {@link ResponseStatus} annotation ensures that if this exception is thrown
 * from a controller and not caught elsewhere, Spring will automatically generate
 * an HTTP 404 (Not Found) response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message The detail message, which is saved for later retrieval by the
     *                {@link #getMessage()} method. This message should clearly
     *                state which resource was not found.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method).
     * @param cause   The cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). (A null value is permitted,
     *                and indicates that the cause is nonexistent or unknown.)
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}