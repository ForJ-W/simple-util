package com.simple.util.exception;

import org.springframework.http.HttpStatus;

/**
 * @author wujing
 * @date 2020/5/11 11:11
 */
public final class UtilException extends GenericException {

    public UtilException(Integer code, String message) {
        super(code, message);
    }

    public UtilException(HttpStatus status, String message) {
        super(status, message);
    }

    public UtilException(String message) {
        super(message);
    }
}
