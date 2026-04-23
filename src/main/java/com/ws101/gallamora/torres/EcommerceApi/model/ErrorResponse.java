package com.ws101.gallamora.torres.EcommerceApi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * This is what gets returned when there is an error
 * includes the time, status code, and message
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    /** When the error happened */
    private LocalDateTime timestamp;

    /** HTTP status code (e.g., 404, 400) */
    private int status;

    /** Short error label */
    private String error;

    /** Detailed message explaining what went wrong */
    private String message;
}