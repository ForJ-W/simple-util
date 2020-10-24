package com.simple.util.exception;

import org.springframework.http.HttpStatus;

/**
 * @author wujing
 * @date 2020/9/4 11:21
 * @see GenericException
 */
public final class ExceptionUtil {
    private ExceptionUtil() {
    }

    /**
     * throwDBException
     *
     * @param message
     */
    public static void throwDBException(String message) {
        throw new DataBaseException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }


    /**
     * throwServiceException
     *
     * @param message
     */
    public static void throwServiceException(String message) {
        throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }


    /**
     * throwServiceException
     *
     * @param status
     * @param message
     */
    public static void throwServiceException(HttpStatus status, String message) {
        throw new ServiceException(status, message);
    }

    /**
     * throwAopException
     *
     * @param message
     */
    public static void throwAopException(String message) {
        throw new AopException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
