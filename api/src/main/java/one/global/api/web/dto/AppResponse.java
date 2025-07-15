package one.global.api.web.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppResponse<T> {
    private int status;
    private Boolean success;
    private String message;
    private T content;
    private List<AppErrorResponse> errors;
    private HashMap<String, Object> parameters;

    @JsonIgnore
    public boolean isValid() {
        return !isInvalid();
    }

    @JsonIgnore
    public boolean isInvalid() {
        return this.errors != null && !this.errors.isEmpty();
    }

    @JsonIgnore
    public AppResponse<T> addError(String code, String description, String traceId) {
        return this.addError(code, description, traceId, HttpStatus.BAD_REQUEST.value());
    }

    @JsonIgnore
    public AppResponse<T> addError(String code, String description, String traceId, int statusCode) {
        return this.addError(new AppErrorResponse(code, description, traceId), statusCode);
    }

    @JsonIgnore
    public AppResponse<T> addError(AppErrorResponse appErrorResponse) {
        return this.addError(appErrorResponse, HttpStatus.BAD_REQUEST.value());
    }

    @JsonIgnore
    public AppResponse<T> addError(AppErrorResponse appErrorResponse, int statusCode) {
        return this.addError(Collections.singletonList(appErrorResponse), statusCode);
    }

    @JsonIgnore
    public AppResponse<T> addError(List<AppErrorResponse> errors, int statusCode) {
        this.status = statusCode;
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.addAll(errors);
        return this;
    }

    @JsonIgnore
    public AppResponse<T> addParameter(String key, Object value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(key, value);
        return this;
    }

    @JsonIgnore
    public ResponseEntity<AppResponse<T>> getResponseEntity() {
        if (this.isValid()) {
            if (ObjectUtils.isEmpty(this.getContent()) &&
                    this.status != HttpStatus.OK.value()) {
                this.status = 204;
            }
        } else {
            if (this.status < 400) {
                this.status = 400;
            }
        }
        return ResponseEntity.status(this.status).body(this);
    }

    @JsonIgnore
    public AppResponse<T> buildParametersPagination(int page, int size, long totalElements, int totalPages) {
        var pagination = new HashMap<String, Object>();
        pagination.put("page", page);
        pagination.put("size", size);
        pagination.put("totalElements", totalElements);
        pagination.put("totalPages", totalPages);
        this.addParameter("pagination", pagination);
        return this;
    }

    public static <T> AppResponse<T> ok(String message, T content) {
        return new AppResponse<>(HttpStatus.OK.value(), true, message, content, null, null);
    }

    public static <T> AppResponse<T> created(String message, T content) {
        return new AppResponse<>(HttpStatus.CREATED.value(), true, message, content, null, null);
    }

    public static <T> AppResponse<T> deleted(String message) {
        return new AppResponse<>(HttpStatus.OK.value(), true, message, null,null, null);
    }

    public static <T> AppResponse<T> invalid(String message, HttpStatus httpStatus, String errorCode,
                                             String descriptionError, String traceId) {
        return new AppResponse<>(httpStatus.value(),
                false,
                message,
                null,
                Collections.singletonList(AppErrorResponse.builder().code(errorCode).description(descriptionError)
                        .traceId(traceId).build()),
                null);
    }

    public static <T> AppResponse<T> invalid(String message, HttpStatus httpStatus, List<AppErrorResponse> errors) {
        return new AppResponse<>(httpStatus.value(),
                false,
                message,
                null,
                errors,
                null);
    }

    public static <T> AppResponse<T> badRequest(String message, String errorCode, String descriptionError,
                                                String traceId) {
        return AppResponse.invalid(message, HttpStatus.BAD_REQUEST, errorCode, descriptionError, traceId);
    }

    public static <T> AppResponse<T> internalServerError(String message, String errorCode, String descriptionError,
                                                         String traceId) {
        return AppResponse.invalid(message, HttpStatus.INTERNAL_SERVER_ERROR, errorCode, descriptionError, traceId);
    }

    public static <T> AppResponse<T> notFound(String message, String errorCode, String descriptionError,
                                              String traceId) {
        return AppResponse.invalid(message, HttpStatus.NOT_FOUND, errorCode, descriptionError, traceId);
    }
}