package com.simple.util;

/**
 * @author wujing
 * @date 2020/8/5 12:24
 */
public final class StringUtil {

    private static final ThreadLocal<StringBuilder> SB_TL = ThreadLocal.withInitial(StringBuilder::new);

    private StringUtil() {
    }


    public static String merge(final Object... elements) {

        if (elements.length == 1) {
            return String.valueOf(elements[0]);
        }
        StringBuilder sb = SB_TL.get();
        for (Object element : elements) {

            sb.append(element);
        }
        final String result = sb.toString();
        sb.delete(0, sb.length());
        return result;
    }

    public void remove() {

        SB_TL.remove();
    }
}