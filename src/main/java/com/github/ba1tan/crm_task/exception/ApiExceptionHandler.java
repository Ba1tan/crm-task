package com.github.ba1tan.crm_task.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    ProblemDetail onApi(ApiException e) {
        return problem(e.status(), e.code(), e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail onIntegrity(DataIntegrityViolationException e) {
        String msg = String.valueOf(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        if (msg.contains("no_manager_overlap")) {
            return problem(HttpStatus.CONFLICT, "MANAGER_BUSY", "У менеджера уже есть встреча в это время");
        }
        if (msg.contains("no_company_overlap")) {
            return problem(HttpStatus.CONFLICT, "COMPANY_BUSY", "У компаний уже есть встреча в это время");
        }
        if (msg.contains("uq_companies_bin")) {
            return problem(HttpStatus.CONFLICT, "BIN_ALREADY_EXISTS", "Компания с таким БИН уже есть");
        }
        log.error("Unexpected integrity violation", e);
        return internal();
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail onAny(Exception e) {
        log.error("Unhandled exception", e);
        return internal();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return handleExceptionInternal(ex, problem(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", detail),
                headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {
        ResponseEntity<Object> res = super.handleExceptionInternal(ex, body, headers, statusCode, request);
        if (res != null && res.getBody() instanceof ProblemDetail pd
                && (pd.getProperties() == null || !pd.getProperties().containsKey("code"))) {
            int s = statusCode.value();
            pd.setProperty("code", s == 404 ? "NOT_FOUND" : s == 400 ? "VALIDATION_ERROR" : "REQUEST_ERROR");
        }
        return res;
    }
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("Unreadable request body: {}", ex.getMostSpecificCause().getMessage());
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    private static ProblemDetail internal() {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Внутренняя ошибка сервера");
    }

    private static ProblemDetail problem(HttpStatus status, String code, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setProperty("code", code);
        return pd;
    }
}
