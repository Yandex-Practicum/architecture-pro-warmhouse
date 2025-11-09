package org.smarthome.common.errors;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public record ApiError(
        HttpStatus status,
        String message,
        List<String> errors
) {
    public ApiError(HttpStatus status, String message, String... errors) {
        this(status, message, Arrays.asList(errors));
    }
}
