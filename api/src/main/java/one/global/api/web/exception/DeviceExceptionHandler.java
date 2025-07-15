package one.global.api.web.exception;

import io.micrometer.tracing.Tracer;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.global.api.domain.exception.CreateDeviceException;
import one.global.api.domain.exception.DeviceInUseException;
import one.global.api.domain.exception.DeviceNotFoundException;
import one.global.api.web.dto.AppErrorResponse;
import one.global.api.web.dto.AppResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class DeviceExceptionHandler {

    private final Tracer tracer;

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<AppResponse<Object>> handleDeviceNotFoundException(
            DeviceNotFoundException ex, WebRequest request) {
        AppErrorResponse error = AppErrorResponse.builder()
                .code("DEVICE_NOT_FOUND")
                .description(ex.getMessage())
                .traceId(getTraceId())
                .build();

        log.warn("Dispositivo não encontrado: {}", ex.getMessage());
        return AppResponse.invalid("Dispositivo não encontrado", HttpStatus.NOT_FOUND,
                Collections.singletonList(error)).getResponseEntity();
    }

    @ExceptionHandler(DeviceInUseException.class)
    public ResponseEntity<AppResponse<Object>> handleDeviceInUseException(
            DeviceInUseException ex, WebRequest request) {
        AppErrorResponse error = AppErrorResponse.builder()
                .code("DEVICE_STATE_CONFLICT")
                .description(ex.getMessage())
                .traceId(getTraceId())
                .build();

        log.warn("Conflict: Device in use\": {}", ex.getMessage());
        return AppResponse.invalid("Conflict: Device in use", HttpStatus.CONFLICT,
                Collections.singletonList(error)).getResponseEntity();
    }

    @ExceptionHandler(CreateDeviceException.class)
    public ResponseEntity<AppResponse<Object>> handleCreateDeviceException(
            CreateDeviceException ex, WebRequest request) {
        AppErrorResponse error = AppErrorResponse.builder()
                .code("DEVICE_CREATION_ERROR")
                .description(ex.getMessage())
                .traceId(getTraceId())
                .build();

        log.warn("Error - verify parameter values: {}", ex.getMessage());
        return AppResponse.invalid("Error - verify parameter values", HttpStatus.BAD_REQUEST,
                Collections.singletonList(error)).getResponseEntity();
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<AppResponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        List<AppErrorResponse> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> AppErrorResponse.builder()
                        .code("VALIDATION_ERROR")
                        .description(error.getDefaultMessage())
                        .traceId(getTraceId())
                        .build())
                .toList();

        log.warn("Argument validation error: {}", ex.getMessage());
        return AppResponse.invalid("Validation Error", HttpStatus.BAD_REQUEST, errors)
                .getResponseEntity();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<AppResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex) {

        List<AppErrorResponse> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> {
            errors.add(AppErrorResponse.builder()
                    .description(violation.getMessage())
                    .traceId(getTraceId())
                    .build());
        });
        var result = AppResponse.invalid("Constraint Violation", HttpStatus.BAD_REQUEST, errors);

        log.warn("ConstraintViolationException: {}", result);
        return result.getResponseEntity();

    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<AppResponse<Object>> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex, WebRequest request) {

        List<AppErrorResponse> errors = ex.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .map(error -> {
                    String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : "parâmetro";
                    return AppErrorResponse.builder()
                            .code("VALIDATION_ERROR")
                            .description(fieldName + ": " + error.getDefaultMessage())
                            .traceId(getTraceId())
                            .build();
                }).toList();

        log.warn("Error in method validation : {}", ex.getMessage());
        return AppResponse.invalid("\"Error in method validation", HttpStatus.BAD_REQUEST, errors)
                .getResponseEntity();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<AppResponse<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        AppErrorResponse error = AppErrorResponse.builder()
                .code("INVALID_PARAMETER_TYPE")
                .description(String.format("The parameter '%s' has an invalid type. Value '%s' could not be converted to  '%s'.",
                        ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"))
                .traceId(getTraceId())
                .build();

        log.warn("Invalid argument type: {}", ex.getMessage());
        return AppResponse.invalid("Invalid Argument type", HttpStatus.BAD_REQUEST,
                Collections.singletonList(error)).getResponseEntity();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<AppResponse<Object>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, WebRequest request) {
        AppErrorResponse error = AppErrorResponse.builder()
                .code("MISSING_PARAMETER")
                .description("Missing required parameter: " + ex.getParameterName())
                .traceId(getTraceId())
                .build();

        log.warn("Missing required parameter: {}", ex.getMessage());
        return AppResponse.invalid("Missing required parameter", HttpStatus.BAD_REQUEST,
                Collections.singletonList(error)).getResponseEntity();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<AppResponse<Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, WebRequest request) {
        AppErrorResponse error = AppErrorResponse.builder()
                .code("DATA_INTEGRITY_VIOLATION")
                .description("Data integrity violation: " + ex.getMostSpecificCause().getMessage())
                .traceId(getTraceId())
                .build();

        log.error("Data integrity violation: ", ex);
        return AppResponse.invalid("Data integrity violation", HttpStatus.CONFLICT,
                Collections.singletonList(error)).getResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppResponse<Object>> handleGenericExceptions(
            Exception ex, WebRequest request) {
        AppErrorResponse error = AppErrorResponse.builder()
                .code("SERVICE_UNAVAILABLE")
                .description("Unexpected error: " + ex.getMessage())
                .traceId(request.getHeader("X-Trace-ID"))
                .build();

        log.error("Unexpected error: ", ex);

        return AppResponse.invalid("Unexpected error", HttpStatus.SERVICE_UNAVAILABLE,
                Collections.singletonList(error)).getResponseEntity();
    }

    private String getTraceId() {
        try {
            return Objects.requireNonNull(tracer.currentTraceContext().context().traceId());
        } catch (Exception e) {
            return  null;
        }
    }

}
