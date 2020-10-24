package com.simple.util.spring.aop;

import com.simple.util.spring.SpringAopUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static com.simple.util.StringUtil.merge;
import static com.simple.util.constant.StringConstant.RIGHT_ARROWS;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/23 16:27
 */
public interface RedisKeyProcess {


    /**
     * cache key handler
     * When the key is not actively set, use the full path name of the current method as the key prefix
     *
     * @param className
     * @param args
     * @param key
     * @param currentMethod
     * @return
     */
    default String keyProcess(String className, Object[] args, String key, Method currentMethod) {

        String argsValueStr = "";
        for (Object arg : args) {
            // null
            if (Objects.isNull(arg)) {
                argsValueStr = merge(argsValueStr, "null");
            }
            // empty
            else if ("".equals(arg)) {
                argsValueStr = merge(argsValueStr, "empty");
            } else {
                argsValueStr = merge(argsValueStr, arg.getClass().isArray() ? Arrays.toString((Object[]) arg) : arg);
            }
        }
        String parameterTypeName = SpringAopUtil.acquireMethodParameterName(currentMethod);
        String currentMethodName = currentMethod.getName();
        String keySuffix = merge("(", parameterTypeName, ")", RIGHT_ARROWS, argsValueStr);
        return StringUtils.isBlank(key)
                ? merge(className, ":", currentMethodName, ":", keySuffix)
                : key;
    }
}
