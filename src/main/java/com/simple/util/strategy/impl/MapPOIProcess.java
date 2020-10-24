package com.simple.util.strategy.impl;


import com.simple.util.entity.RowBuild;
import com.simple.util.poi.TableHeader;
import com.simple.util.strategy.POIProcess;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wujing
 * @date 2020/5/10 13:31
 */
@SuppressWarnings("unchecked")
public class MapPOIProcess<M> implements POIProcess<M> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapPOIProcess.class);

    public MapPOIProcess() {
    }

    @Override
    public M parseProcess(M instance, int firstCellIndex, int lastCellIndex,
                          Row tableHeaderRow, Row row) {
        // 创建map封装row
        while (firstCellIndex < lastCellIndex) {
            // 获取cellValue
            String tableHeaderValue = convertCellValueToString(tableHeaderRow.getCell(firstCellIndex));
            String cellValue = convertCellValueToString(row.getCell(firstCellIndex++));
            if (StringUtils.isBlank(tableHeaderValue)) {
                continue;
            }
            if (StringUtils.isBlank(cellValue)) {
                cellValue = "";
            }

            ((Map<String, String>) instance).put(tableHeaderValue, cellValue);
        }
        return instance;
    }

    @Override
    public void exportProcess(M data, Row row, RowBuild rowBuild) {
        // 确定数据长度
        Map map = (Map) data;
        List<String> keyList = new ArrayList<>(map.keySet());
        int[] dataIndexArr = rowBuild.getDataIndexArr();
        int[] cellIndexArr = rowBuild.getCellIndexArr();
        int dataLength = Math.max(dataIndexArr.length, cellIndexArr.length);
        if (lengthJudge(dataIndexArr, cellIndexArr)) {
            // 根据偏移确定数据长度
            Integer offsetValue = rowBuild.getOffsetValue();
            dataLength = null == offsetValue ? keyList.size() : offsetValue;
        }
        // 索引数组处理
        int iData = 0;
        int iCell = 0;
        int iTableHeader = 0;
        final boolean greaterThanOne = dataIndexArr.length > 1;
        dataIndexArr = indexArrProcess(dataIndexArr, dataLength, greaterThanOne, rowBuild.getDataBeginIndex());
        cellIndexArr = indexArrProcess(cellIndexArr, dataLength, greaterThanOne, rowBuild.getCellBeginIndex());
        TableHeader tableHeader = rowBuild.getTableHeader();
        for (; iData < dataLength; iData++) {

            int dataBeginIndex = rowBuild.getDataBeginIndex();
            String key = keyList.get(dataIndexArr[iData]);
            rowBuild.setDataBeginIndex(dataBeginIndex);
            // 创建cell
            Cell cell = row.createCell(cellIndexArr[iCell++]);
            cell.setCellStyle(rowBuild.getCellStyle());
            if (null == tableHeader) {
                // 若未设置表头,那么直接插入数据
                convertValueToCellType(cell, map.get(key));
            } else {
                // 设置表头
                iTableHeader = tableHeaderProcess(iTableHeader, tableHeader, key, cell);
            }
        }
    }
}
