package com.kkaebiz.api_server.common;

public record ApiResult<T>(
        boolean success,
        String message,
        T data
) {}
