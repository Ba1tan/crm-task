package com.github.ba1tan.crm_task.exception;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException{
    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message){
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus status(){
        return status;
    }

    public String code(){
        return code;
    }


    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }
    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }
    public static ApiException conflict(String code, String message) {
        return new ApiException(HttpStatus.CONFLICT, code, message);
    }
    public static ApiException unprocessable(String code, String message) {
        return new ApiException(HttpStatus.UNPROCESSABLE_CONTENT, code, message);
    }


}
