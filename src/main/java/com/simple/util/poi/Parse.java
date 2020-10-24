package com.simple.util.poi;

import com.simple.util.enums.ExcelEnum;
import com.simple.util.factory.POIProcessFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/22 14:53
 */
public class Parse {

    private static final Logger LOG = LoggerFactory.getLogger(Parse.class);
    private Function<Map<ExcelEnum, Object>, Boolean> validate;
    private final Map<String, Map<Object, Object>> parseMap = new LinkedHashMap<>(2);
    private final POIHelper poi;

    public Map<String, Map<Object, Object>> getParseMap() {
        return parseMap;
    }

    private Parse(POIHelper poi) {

        this.poi = poi;
    }

    static Parse getInstance(POIHelper poi) {

        return new Parse(poi);
    }

    /**
     * 构造校验function
     *
     * @param validate
     * @return
     */
    public Parse validate(Function<Map<ExcelEnum, Object>, Boolean> validate) {

        this.validate = validate;
        return this;
    }

    /**
     * 将excel sheet表中的数据解析并以指定类型封装到传入的集合中
     *
     * @param dataList         数据集合
     * @param dataClass        数据的类型
     * @param sheetIndex       sheet 索引
     * @param tableHeaderIndex 表头索引
     * @return
     */
    public <D> Parse parseSheet(Collection<D> dataList, Class<D> dataClass, int sheetIndex, int tableHeaderIndex) {

        int successCount = 0;
        Set<Integer> failSet = new LinkedHashSet<>();
        Map<Object, Object> map = new LinkedHashMap<>(4);
        Map<ExcelEnum, D> validateMap = new LinkedHashMap<>(2);
        // 获取sheet
        Sheet sheet = poi.workbook.getSheetAt(sheetIndex);
        String sheetName = "";
        if (sheet != null) {
            // 获取row开始与结束索引
            int rowBeginIndex = tableHeaderIndex + 1;
            int rowEndIndex = sheet.getLastRowNum();
            // 获取集合中的class
            for (; rowBeginIndex <= rowEndIndex; rowBeginIndex++) {
                // 获取表头row 与 row
                Row tableHeaderRow = sheet.getRow(tableHeaderIndex);
                Row row = sheet.getRow(rowBeginIndex);
                short lastCellNum = tableHeaderRow.getLastCellNum();
                if (null != row) {

                    int rowNum = row.getRowNum() + 1;
                    int firstCellIndex = 0;
                    // 解析单元格
                    D instance = null;
                    try {
                        instance = dataClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Objects.isNull(instance)) {
                        throw new NullPointerException("[parseSheet] instance is null!");
                    }
                    D data = POIProcessFactory.getInstance(dataClass)
                            .parseProcess(instance, firstCellIndex, lastCellNum, tableHeaderRow, row);
                    if (null == data) {

                        failSet.add(rowNum);
                        LOG.warn("[parseSheet] parseCell fail!,rowNum: {}", rowNum);
                        continue;
                    }
                    // 校验处理
                    if (null != validate) {

                        validateMap.put(ExcelEnum.ROW_DATA, data);
                        if (validate(validate, validateMap)) {

                            failSet.add(rowNum);
                            LOG.warn("[parseSheet] validate fail!,rowNum: {}", rowNum);
                            continue;
                        }
                    }

                    successCount++;
                    dataList.add(data);
                }
            }
            sheetName = sheet.getSheetName();
        }
        //        添加对应sheet对应错误信息
        map.put(ExcelEnum.SHEET_NAME, sheetName);
        map.put(ExcelEnum.FAIL_ROW, failSet);
        map.put(ExcelEnum.FAIL_COUNT, failSet.size());
        map.put(ExcelEnum.SUCCESS_COUNT, successCount);
        parseMap.put(ExcelEnum.SHEET + String.valueOf(sheetIndex), map);
        return this;
    }

    /**
     * 校验处理
     *
     * @param function
     * @param map
     * @return
     */
    private boolean validate(Function<Map<ExcelEnum, Object>, Boolean> function, Map map) {

        return function.apply(map);
    }
}