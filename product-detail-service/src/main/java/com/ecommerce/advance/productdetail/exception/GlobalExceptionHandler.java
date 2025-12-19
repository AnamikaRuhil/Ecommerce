package com.ecommerce.advance.productdetail.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Bad Request exception", ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError("BAD_REQUEST", ex.getMessage()));

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        log.error("Runtime exception", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        log.error("Validation error", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError("VALIDATION_ERROR", "Invalid request"));
    }


    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> handleDataNotFound(DataNotFoundException ex, HttpServletRequest request) {
        log.error("Data not found", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(ex.getMessage(), "DATA_NOT_FOUND"));
    }

    private ErrorResponse buildError(String message, String code) {
        return ErrorResponse.builder()
                .message(message)
                .errorCode(code)
                .traceId(MDC.get("traceId"))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError("INTERNAL_ERROR", "Something went wrong"));
    }

}

