package com.simple.util.exception;

import org.springframework.http.HttpStatus;

/**
 * @author wujing
 * @date 2020/9/4 10:40
 */
public final class ServiceException extends GenericException {

    public ServiceException(Integer code, String message) {
        super(code, message);
    }

    public ServiceException(HttpStatus status, String message) {
        super(status, message);
    }

    public ServiceException(String message) {
        super(message);
    }
}
