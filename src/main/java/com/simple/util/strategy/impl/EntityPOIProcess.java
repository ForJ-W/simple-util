package com.simple.util.strategy.impl;


import com.simple.util.annotation.ExportIgnore;
import com.simple.util.entity.RowBuild;
import com.simple.util.poi.TableHeader;
import com.simple.util.reflet.ClassSuper;
import com.simple.util.strategy.POIProcess;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * @author wujing
 * @date 2020/5/10 13:32
 */
public class EntityPOIProcess<D> implements POIProcess<D> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityPOIProcess.class);

    @Override
    public D parseProcess(D instance, int firstCellIndex, int lastCellIndex,
                          Row tableHeaderRow, Row row) {
        try {
            // 获取类型字段数组
            final Field[] fieldArr = ClassSuper.getInstance(instance.getClass()).getExtentsReverseFields();
            while (firstCellIndex < fieldArr.length) {
                // 获取cellValue
                String cellValue = convertCellValueToString(row.getCell(firstCellIndex));
                if (StringUtils.isBlank(cellValue)) {

                    cellValue = "";
                }
                // 设置属性值
                Field field = fieldArr[firstCellIndex++];
                field.setAccessible(true);
                field.set(instance, cellValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }

    @Override
    public void exportProcess(D data, Row row, RowBuild rowBuild) {
        // 确定数据长度
        final Field[] fieldArr = ClassSuper.getInstance(data.getClass()).getExtentsReverseFields();
        int[] dataIndexArr = rowBuild.getDataIndexArr();
        int[] cellIndexArr = rowBuild.getCellIndexArr();
        int dataLength = Math.max(dataIndexArr.length, cellIndexArr.length);
        if (lengthJudge(dataIndexArr, cellIndexArr)) {
            // 未设置偏移量默认偏移字段个数
            Integer offsetValue = rowBuild.getOffsetValue();
            dataLength = null == offsetValue ? fieldArr.length : offsetValue;
        }
        // 索引数组处理
        final boolean greaterThanOne = dataIndexArr.length > 1;
        dataIndexArr = indexArrProcess(dataIndexArr, dataLength, greaterThanOne, rowBuild.getDataBeginIndex());
        cellIndexArr = indexArrProcess(cellIndexArr, dataLength, greaterThanOne, rowBuild.getCellBeginIndex());
        int iData = 0;
        int iCell = 0;
        int iTableHeader = 0;
        TableHeader tableHeader = rowBuild.getTableHeader();
        for (; iData < dataLength; iData++) {
            Field field = fieldArr[dataIndexArr[iData]];
            field.setAccessible(true);
            if (!greaterThanOne && field.isAnnotationPresent(ExportIgnore.class)) {
                continue;
            }
            // 创建cell
            Cell cell = row.createCell(cellIndexArr[iCell++]);
            cell.setCellStyle(rowBuild.getCellStyle());
            if (null == tableHeader) {
                // 若未设置表头,那么直接插入数据
                try {
                    convertValueToCellType(cell, field.get(data));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                // 设置表头
                iTableHeader = tableHeaderProcess(iTableHeader, tableHeader, field.getName(), cell);
            }
        }
    }
}
