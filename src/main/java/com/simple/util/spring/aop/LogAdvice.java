package com.simple.util.spring.aop;

import com.simple.util.constant.StringConstant;
import com.simple.util.exception.ExceptionUtil;
import com.simple.util.exception.UtilException;
import com.simple.util.spring.SpringAopUtil;
import com.simple.util.spring.annotation.Log;
import org.apache.commons.lang3.ClassUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.simple.util.StringUtil.merge;

/**
 * @author wujing
 * @date 2020/5/14 16:23
 */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE << 1)
public final class LogAdvice {
    private LogAdvice() {
    }

    private static final String LOG_PREFIX = "@Log";
    private static final Logger LOGGER = LoggerFactory.getLogger(LOG_PREFIX);


    /**
     * enhance all classes or methods with @Log annotation
     *
     * @param pjp
     * @return
     */
    @Around("PointcutModel.logClass()||PointcutModel.logMethod()")
    private Object log(ProceedingJoinPoint pjp) {

        Object pjpTarget = pjp.getTarget();
        String className = null;
        Object result = null;
        String methodName = "";
        String parameterName = "";
        boolean printExpendTime = false;
        long extendTimeMs = 0;
        try {
            // run source method
            long runStartTimeMs = System.currentTimeMillis();
            result = pjp.proceed(pjp.getArgs());
            extendTimeMs = System.currentTimeMillis() - runStartTimeMs;
            // if @Log on the class
            Class<?> targetClass = SpringAopUtil.acquireClassForAnnotation(Log.class, pjpTarget.getClass());
            printExpendTime = fieldToString(pjpTarget, targetClass);
            className = targetClass.getName();
            // get current class method
            Method currentMethod = SpringAopUtil.acquireCurrentMethod(pjp, targetClass);
            methodName = currentMethod.getName();
            // if @Log on method
            if (currentMethod.isAnnotationPresent(Log.class) && !printExpendTime) {
                printExpendTime = currentMethod.getAnnotation(Log.class).runExpendTime();
            }
            parameterName = SpringAopUtil.acquireMethodParameterName(currentMethod);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            LOGGER.error("{}.{}({}) execute fail !", className, methodName, parameterName, throwable);
            ExceptionUtil.throwAopException("log fail!");
        }
        LOGGER.info("{}.{}({}) execute ok ! {}",
                className, methodName, parameterName, printExpendTime ? merge("expend time: ", extendTimeMs, "ms") : StringConstant.NONE);
        return result;
    }

    /**
     * print field toString for assign annotation
     *
     * @param pjpTarget
     * @param targetClass
     * @throws IllegalAccessException
     */
    private boolean fieldToString(Object pjpTarget, Class<?> targetClass) throws IllegalAccessException {

        boolean printExpendTime = false;
        if (targetClass.isAnnotationPresent(Log.class)) {

            Field[] fieldArr = targetClass.getDeclaredFields();
            Log log = targetClass.getAnnotation(Log.class);
            printExpendTime = log.runExpendTime();
            for (int i = 0; i < fieldArr.length; i++) {

                Field field = fieldArr[i];
                field.setAccessible(true);
                Class<?> type = field.getType();
                Class<? extends Annotation>[] fieldToStringAnnotation = log.fieldToString();
                for (int j = 0; j < fieldToStringAnnotation.length; j++) {

                    Class<? extends Annotation> annotationClass = fieldToStringAnnotation[j];
                    if (type.isAnnotationPresent(annotationClass)) {

                        Object value = field.get(pjpTarget);
                        if (null != value) {
                            // properties log
                            LOGGER.info("data info: {}", value);
                        }
                    }
                }
            }
        }
        return printExpendTime;
    }
}
