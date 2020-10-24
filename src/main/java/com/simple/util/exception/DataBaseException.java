package com.simple.util.exception;

import org.springframework.http.HttpStatus;

/**
 * @author wujing
 * @date 2020/9/4 10:40
 */
public final class DataBaseException extends GenericException {

    public DataBaseException(Integer code, String message) {
        super(code, message);
    }

    public DataBaseException(HttpStatus status, String message) {
        super(status, message);
    }

    public DataBaseException(String message) {
        super(message);
    }
}
