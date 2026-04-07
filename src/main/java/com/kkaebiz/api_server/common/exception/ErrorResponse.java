package com.kkaebiz.api_server.common.exception;

public class ErrorResponse {
    private String code;
    private String message;

    public ErrorResponse(int status, String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
