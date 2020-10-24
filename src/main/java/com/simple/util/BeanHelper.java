package com.simple.util;


import com.simple.util.exception.UtilException;
import com.simple.util.reflet.ClassSuper;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.simple.util.constant.StringConstant.NONE;
import static com.simple.util.constant.StringConstant.UNDERLINE;

/**
 * @author wujing
 * @date 2020/4/22 15:22
 */
@SuppressWarnings("unchecked")
public final class BeanHelper {

    private static final String BEAN_PROCESS_ERROR = "bean process error";
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanHelper.class);

    private static Rule RULE;
    private static final Empty DEFAULT_MAP_VALUE = new Empty();

    private BeanHelper() {
    }


    private static class Empty {
        private Empty() {
        }
    }

    /**
     * 将源对象属性值拷贝到目标类
     *
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copyProperties(Object source, Class<T> target) {
        try {

            T t = target.newInstance();
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (Exception e) {

            LOGGER.error("[copyProperties] data convert fail，target Object {} construct exception ", target.getName(), e);
            throw new UtilException(BEAN_PROCESS_ERROR);
        }
    }

    /**
     * 通过json序列化将mapList装换成BeanList
     *
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> List<T> mapToBeans(List source, Class<T> target) {

        List<T> list = new ArrayList<>();
        for (Object o : source) {

            T t = JsonUtils.toBean(JsonUtils.toString(o), target);
            list.add(t);
        }

        source.clear();
        return list;
    }


    /**
     * 将map 中的值根据key 存入bean中 不区分大小写
     *
     * @param sourceMap
     * @param targetBean
     */
    public static Rule mapInToBean(Map sourceMap, Object targetBean) {

        initRule(sourceMap, targetBean);
        return RULE;
    }

    /**
     * init rule
     *
     * @param sourceMap
     * @param targetBean
     */
    private static void initRule(Map sourceMap, Object targetBean) {

        if (null == RULE) {

            RULE = new Rule();
        }

        RULE.reserve = true;
        RULE.caseInsensitive = true;
        RULE.takeOutUnderline = false;

        /**
         * 并发时导致build迭代异常
         * 通过ConcurrentHashMap进行包装
         * 注意ConcurrentHashMap k v不可为null
         */
        synchronized (BeanHelper.class) {
            RULE.sourceMap = new ConcurrentHashMap(sourceMap.size());
            for (Object key : sourceMap.keySet()) {

                Object value = sourceMap.get(key);
                RULE.sourceMap.put(key, Objects.isNull(value) ? DEFAULT_MAP_VALUE : value);
            }
        }
        if (null == RULE.targetBean || RULE.targetBean.getClass() != targetBean.getClass()) {

            RULE.fieldArr = ClassSuper.getInstance(targetBean.getClass()).getExtentsFields();
        }

        RULE.targetBean = targetBean;
    }

    /**
     * Rule class
     */
    public static class Rule {

        private Rule() {
        }

        private Field[] fieldArr;
        private Map sourceMap;
        private Object targetBean;
        private Boolean reserve = true;
        private Boolean takeOutUnderline = false;
        private Boolean caseInsensitive = true;

        /**
         * build
         */
        public void build() {

            try {

                Iterator iterator = sourceMap.entrySet().iterator();
                out:
                while (iterator.hasNext()) {

                    Map.Entry entry = (Map.Entry) iterator.next();

                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    // 是否区分大小写
                    String keyFinally = caseInsensitive
                            ? String.valueOf(key)
                            : String.valueOf(key).toLowerCase();
                    // 是否去除下划线
                    keyFinally = takeOutUnderline
                            ? keyFinally.replace(UNDERLINE, NONE)
                            : keyFinally;

                    in:
                    for (int i = 0; i < fieldArr.length; i++) {

                        Field field = fieldArr[i];
                        String fieldName = field.getName();
                        // 是否区分大小写
                        String fieldNameFinally = caseInsensitive
                                ? field.getName()
                                : field.getName().toLowerCase();
                        // 是否去除下划线
                        fieldNameFinally = takeOutUnderline
                                ? fieldNameFinally.replace(UNDERLINE, NONE)
                                : fieldNameFinally;

                        if (keyFinally.equals(fieldNameFinally)) {

                            if (field.getType() == Date.class) {

                                ConvertUtils.register(new DateConverter(value.getClass() == Empty.class ? null : value), Date.class);
                            }
                            String fieldValue = org.apache.commons.beanutils.BeanUtils.getProperty(targetBean, fieldName);
                            if (reserve) {
                                // 保留目标bean已有的属性值
                                if (StringUtils.isBlank(fieldValue)) {
                                    org.apache.commons.beanutils.BeanUtils.setProperty(targetBean, fieldName, value.getClass() == Empty.class ? null : value);
                                }
                            } else {

                                org.apache.commons.beanutils.BeanUtils.setProperty(targetBean, fieldName, value.getClass() == Empty.class ? null : value);
                            }
                            break in;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("[build] map into bean fail!");
                throw new UtilException(BEAN_PROCESS_ERROR);
            }
        }

        /**
         * 不保留targetBean中已有的属性值
         *
         * @return
         */
        public Rule notReserve() {

            this.reserve = false;
            return this;
        }

        /**
         * 不区分key与字段的大小写
         *
         * @return
         */
        public Rule notCaseInsensitive() {

            this.caseInsensitive = false;
            return this;
        }

        /**
         * 去除key与字段的下划线
         *
         * @return
         */
        public Rule takeOutUnderline() {

            this.takeOutUnderline = true;
            return this;
        }
    }

    /**
     * 通过json序列化将bean装换成map
     *
     * @param bean
     * @return
     */
    public static Map<String, Object> beanToMap(Object bean) {

        return JsonUtils.toMap(JsonUtils.toString(bean), String.class, Object.class);
    }

    /**
     * 将源对象属性值列表拷贝到目标类列表 List
     *
     * @param sourceList
     * @param target
     * @param <T>
     * @return
     */
    public static <T> List<T> copyWithCollection(List<?> sourceList, Class<T> target) {

        try {

            return sourceList.stream().map(s -> copyProperties(s, target)).collect(Collectors.toList());
        } catch (Exception e) {

            LOGGER.error("[copyWithCollection] data convert fail，target Object {} construct exception ", target.getName(), e);
            throw new UtilException(BEAN_PROCESS_ERROR);
        }
    }

    /**
     * 将源对象属性值列表拷贝到目标类列表 Set
     *
     * @param sourceList
     * @param target
     * @param <T>
     * @return
     */
    public static <T> Set<T> copyWithCollection(Set<?> sourceList, Class<T> target) {

        try {

            return sourceList.stream().map(s -> copyProperties(s, target)).collect(Collectors.toSet());
        } catch (Exception e) {

            LOGGER.error("[copyWithCollection] data convert fail，target Object {} construct exception ", target.getName(), e);
            throw new UtilException(BEAN_PROCESS_ERROR);
        }
    }
}
