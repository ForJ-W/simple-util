package com.simple.util.exception;

import org.springframework.http.HttpStatus;

/**
 * @author wujing
 * @date 2020/9/8 14:12
 */
public final class AopException extends GenericException {

    public AopException(Integer code, String message) {
        super(code, message);
    }

    public AopException(HttpStatus status, String message) {
        super(status, message);
    }

    public AopException(String message) {
        super(message);
    }
}
