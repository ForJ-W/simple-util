package com.simple.util.asserts;

import com.simple.util.exception.DataBaseException;

/**
 * @author wujing
 * @date 2020/9/7 19:04
 */
public final class DataBaseAssert {

    private DataBaseAssert() {
    }

    /**
     * 数据行是否成功更新
     *
     * @param flag
     * @param message
     */
    public static void updateRow(boolean flag, String message) {
        if (flag) {
            throw new DataBaseException(message);
        }
    }
}
