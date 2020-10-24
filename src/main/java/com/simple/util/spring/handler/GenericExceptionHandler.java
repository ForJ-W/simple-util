package com.simple.util.spring.handler;


import com.simple.util.JsonUtils;
import com.simple.util.exception.GenericException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wujing
 * @date 2020/9/4 10:40
 * @see org.springframework.web.method.annotation.ExceptionHandlerMethodResolver
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE << 1)
final class GenericExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionHandler.class);

    @ExceptionHandler(value = GenericException.class)
    @ResponseBody
    public Map<String, Object> businessExceptionHandler(GenericException e, HttpServletResponse response) {
        LOG.error("=====================Generic Exception Catch=======================");
        HashMap<String, Object> map = new HashMap<>(3);
        response.setStatus(e.getCode());
        map.put("code", e.getCode());
        map.put("message", e.getMessage());
        map.put("data", null);
        LOG.error(JsonUtils.toString(map));
        return map;
    }
}
