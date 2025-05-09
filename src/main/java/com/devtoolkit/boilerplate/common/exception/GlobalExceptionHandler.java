package com.devtoolkit.boilerplate.common.exception;

import com.devtoolkit.boilerplate.common.response.Response;
import com.devtoolkit.boilerplate.common.response.model.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex) {
        return switch (ex.getCodeEnum()) {

            default ->
                    ResponseEntity.badRequest().body(
                            Response.Fail.of(ex.getCodeEnum(), "비즈니스 예외가 발생했습니다.")
                    );
        };
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response.Fail> handleValidation(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(
                Response.Fail.of(ResponseCode.VALIDATION_ERROR, errorMsg)
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response.Fail> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = String.format("필수 요청 파라미터 '%s'가 누락되었습니다.", ex.getParameterName());

        return ResponseEntity.badRequest().body(
                Response.Fail.of(ResponseCode.MISSING_PARAMETER, message)
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response.Fail> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String parameterName = ex.getName();
        String parameterValue = Optional.ofNullable(ex.getValue()).orElse("null").toString();
        String errorMessage = String.format("잘못된 요청 파라미터: %s, 값: %s", parameterName, parameterValue);

        log.error("MethodArgumentTypeMismatchException: {}", errorMessage);

        return ResponseEntity.badRequest().body(
                Response.Fail.of(ResponseCode.TYPE_MISMATCH, errorMessage)
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response.Fail> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                Response.Fail.of(ResponseCode.INVALID_REQUEST, ex.getMessage())
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Response.Fail> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.info("No resource found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Response.Fail.of(ResponseCode.NOT_FOUND, "Requested resource not found.")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response.Fail> handleException(Exception ex) {
        log.error("Unhandled Exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Response.Fail.of(ResponseCode.ERROR, ex.getMessage())
        );
    }
}
