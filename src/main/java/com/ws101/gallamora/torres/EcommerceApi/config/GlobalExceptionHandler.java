package com.ws101.gallamora.torres.EcommerceApi.config;

import com.ws101.gallamora.torres.EcommerceApi.model.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Global exception handler for the whole API.
 * <p>
 * Task 4: Extended to handle JPA-specific exceptions:
 * <ul>
 *   <li>{@link EntityNotFoundException} – returned when a requested entity does not
 *       exist in the database (mapped to HTTP 404).</li>
 *   <li>{@link DataIntegrityViolationException} – returned when a database constraint
 *       is violated, such as a duplicate unique field or a foreign-key conflict
 *       (mapped to HTTP 400).</li>
 * </ul>
 * All error responses use the {@link ErrorResponse} model so clients always receive
 * a consistent JSON structure.
 * </p>
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles the case where a requested resource was not found in the database.
     * Produces a 404 Not Found response with a descriptive message.
     *
     * @param ex the exception thrown by the service layer
     * @return 404 response with an {@link ErrorResponse} body
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                404,
                "Not Found",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles database constraint violations such as duplicate unique fields or
     * foreign-key conflicts that prevent a write operation from completing.
     * Produces a 400 Bad Request response.
     *
     * @param ex the exception thrown by the persistence layer
     * @return 400 response with an {@link ErrorResponse} body
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                400,
                "Bad Request",
                "Data integrity violation: " + ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles invalid input errors thrown explicitly in the service or controller layer.
     * Produces a 400 Bad Request response.
     *
     * @param ex the exception carrying the validation message
     * @return 400 response with an {@link ErrorResponse} body
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                400,
                "Bad Request",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Catch-all handler for any unexpected runtime exception.
     * Produces a 500 Internal Server Error response.
     *
     * @param ex the unexpected exception
     * @return 500 response with an {@link ErrorResponse} body
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
