package com.simple.util.spring;

import com.simple.util.enums.ProcessEnum;
import com.simple.util.reflet.ClassSuper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.simple.util.constant.MethodTypeConstant.*;
import static com.simple.util.enums.ProcessEnum.*;

/**
 * SpringAopUtil
 *
 * @author wujing
 * @date 2020/8/21 22:07
 * @see org.springframework.beans.factory.ListableBeanFactory#getBeanNamesForAnnotation(Class)
 * @see ProceedingJoinPoint
 */
@Component
public final class SpringAopUtil {

    @Autowired
    private ApplicationContext applicationContext;
    private static SpringAopUtil springAopUtil;

    @PostConstruct
    private void init() {
        springAopUtil = this;
    }

    private SpringAopUtil() {
    }

    /**
     * Get the class object identified by the annotation
     *
     * @param targetClass
     * @param annotation
     * @return
     */
    @Deprecated
    public static Class<?> acquireClassForAnnotationOld(Class<?> targetClass, Class<? extends Annotation> annotation) {

        ApplicationContext applicationContext = springAopUtil.applicationContext;
        if (targetClass.isAnnotationPresent(annotation)) {
            return targetClass;
        }
        Class<?>[] interfaceArr = targetClass.getInterfaces();
        for (Class<?> interfaceClz : interfaceArr) {
            if (interfaceClz.isAnnotationPresent(annotation)) {
                return interfaceClz;
            }
            String[] beanNames = applicationContext.getBeanNamesForType(interfaceClz);
            for (String beanName : beanNames) {
                Class<?> clz = applicationContext.getBean(beanName).getClass();
                if (clz.isAnnotationPresent(annotation)) {
                    targetClass = clz;
                    break;
                }
                targetClass = interfaceClz;
            }
        }
        return targetClass;
    }

    /**
     * Get the class object identified by the annotation
     *
     * @param annotation
     * @param targetClass
     * @return
     */
    public static Class<?> acquireClassForAnnotation(Class<? extends Annotation> annotation, Class<?> targetClass) {

        // 判断继承体系中的类上是否存在注解(不包括自身代理类)
        for (Class<?> cls : ClassSuper.getInstance(targetClass)) {
            if (cls != targetClass && cls.isAnnotationPresent(annotation)) {
                return cls;
            }
        }
        // 判断继承体系中的接口上是否存在注解(不包括自身代理类)
        for (Class<?> clsI : targetClass.getInterfaces()) {
            if (clsI.isAnnotationPresent(annotation)) {
                return clsI;
            }
        }
        // 都没有返回当前目标class
        return targetClass;
    }

    /**
     * acquire NotProxy Class
     *
     * @param targetClass
     * @return
     */
    public static Class<?> acquireNotProxyClass(Class<?> targetClass) {

        // 判断该代理类是否存在父类 存在取顶层父类
        List<Class<?>> extentsReverseList = (List<Class<?>>) ClassSuper.getInstance(targetClass).getExtentsReverseList();
        if (extentsReverseList.size() > 1) {
            return extentsReverseList.get(0);
        }
        // 判断该代理类是否存在接口 存在取顶层接口
        Class<?>[] interfaceArr = targetClass.getInterfaces();
        if (interfaceArr.length > 0) {
            return interfaceArr[interfaceArr.length - 1];
        }
        // 都没有返回当前目标class
        return targetClass;
    }

    /**
     * Get the current method object
     *
     * @param pjp
     * @param targetClass
     * @return
     * @throws NoSuchMethodException
     */
    public static Method acquireCurrentMethod(ProceedingJoinPoint pjp, Class<?> targetClass) throws NoSuchMethodException {

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        return targetClass.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }

    /**
     * Get method parameter name
     *
     * @param method
     * @return
     */
    public static String acquireMethodParameterName(Method method) {

        Parameter[] parameters = method.getParameters();
        String parameterTypeNameListStr = Arrays.stream(parameters)
                .map(Parameter::getType)
                .map(Class::getName)
                .collect(Collectors.toList())
                .toString();
        return parameterTypeNameListStr.substring(1, parameterTypeNameListStr.length() - 1);
    }


    /**
     * acquire Method Process Type
     *
     * @param methodName
     * @return
     */
    public static ProcessEnum acquireMethodProcessType(String methodName) {

        if (methodName.startsWith(DELETE)
                || methodName.startsWith(DEL)
                || methodName.startsWith(REMOVE)) {
            return D;
        } else if (methodName.startsWith(ADD)
                || methodName.startsWith(CREATE)
                || methodName.startsWith(INSERT)) {
            return C;
        } else if (methodName.startsWith(UPDATE)
                || methodName.startsWith(MODIFY)
                || methodName.endsWith(PROCESS)) {
            return U;
        } else {
            return R;
        }
    }

    /**
     * acquire Method Name
     *
     * @param targetClass
     * @return
     */
    public static String[] acquireMethodNameArr(Class<?> targetClass) {

        Method[] declaredMethodArr = targetClass.getDeclaredMethods();
        return Arrays.stream(declaredMethodArr)
                .map(Method::getName)
                .toArray(String[]::new);
    }

    /**
     * acquire StarterBean For Annotation
     *
     * @param annotation
     * @return
     */
    public static Object acquireStarterBeanForAnnotation(Class<? extends Annotation> annotation) {

        ApplicationContext applicationContext = springAopUtil.applicationContext;
        String[] beanNamesForAnnotation = applicationContext.getBeanNamesForAnnotation(SpringBootApplication.class);
        Object bean = null;
        for (String beanName : beanNamesForAnnotation) {

            bean = applicationContext.getBean(beanName);
            if (bean.getClass().isAnnotationPresent(annotation)) {

                break;
            }
        }
        return bean;
    }

    /**
     * acquire Generic Type
     *
     * @param currentMethod
     * @param genericIndex
     * @return
     */
    public static Type acquireGenericType(Method currentMethod, int genericIndex) {

        Type[] acquireGenericTypeArr = acquireGenericTypeArr(currentMethod);
        Type actualTypeArgument = null;
        if (acquireGenericTypeArr.length > 0) {
            actualTypeArgument = acquireGenericTypeArr[genericIndex];
        }
        return actualTypeArgument;
    }

    /**
     * acquire Generic Type Arr
     *
     * @param currentMethod
     * @return
     */
    public static Type[] acquireGenericTypeArr(Method currentMethod) {

        Type genericReturnType = currentMethod.getGenericReturnType();
        Type[] actualTypeArguments = null;
        if (genericReturnType instanceof ParameterizedType) {
            actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
        }
        return actualTypeArguments;
    }
}
