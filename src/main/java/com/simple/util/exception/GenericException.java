package com.simple.util.exception;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

import static com.simple.util.StringUtil.merge;

/**
 * @author wujing
 * @date 2020/9/4 10:40
 * @see com.simple.util.spring.handler.GenericExceptionHandler
 */
public abstract class GenericException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -1375093631909763541L;
    private Integer code;
    private static final Logger LOG = LoggerFactory.getLogger(GenericException.class);
    private static String postMessage;

    public GenericException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public GenericException(HttpStatus status, String message) {

        super(messageProcess(status, message));
        this.code = status.value();
    }

    private static String messageProcess(HttpStatus status, String message) {

        int lastIndex = message.indexOf("]") + 1;
        String preMessage = message.substring(0, lastIndex);
        if (StringUtils.isNotBlank(preMessage)) {
            LOG.error(merge("exception method -> ", preMessage));
        }
        String postMessage = message.substring(lastIndex);
        return merge(status.getReasonPhrase(), ": ", postMessage);
    }

    public GenericException(String message) {

        this(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
