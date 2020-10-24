package com.simple.util.poi;

import com.simple.util.StreamUtils;
import com.simple.util.asserts.UtilAssert;
import com.simple.util.entity.RowBuild;
import com.simple.util.enums.ExcelEnum;
import com.simple.util.factory.POIProcessFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/22 13:53
 */
public class Export<D> {

    private static final Logger LOG = LoggerFactory.getLogger(Export.class);
    private static final Integer BUILD_DATA_FLAG = -2;
    private static final byte SHEET_NAME_LENGTH = 30;

    private final List<Map<ExcelEnum, Object>> exportMapList = new ArrayList<>();
    private OutputStream os;
    private List<D> dataList;
    private Map<ExcelEnum, Object> exportMap;
    private final POIHelper poi;

    static Export getInstance(POIHelper poi) {

        return new Export(poi);
    }

    static <D> Export getInstance(POIHelper poi, List<D> dataList) {

        return new Export(poi, dataList);
    }

    public Export targetOS(OutputStream os) {

        this.os = os;
        return this;
    }

    public Export clearSheet() {

        exportMap.put(ExcelEnum.CLEAR_SHEET, true);
        return this;
    }

    private Export(POIHelper poi) {
        this.poi = poi;
    }

    private Export(POIHelper poi, List<D> dataList) {

        this.poi = poi;
        this.dataList = dataList;
    }

    public Parse parse() {

        return Parse.getInstance(poi);
    }

    /**
     * 构建索引
     *
     * @param sheetIndex
     * @param rowIndex
     * @return
     */
    public Export dataIndex(Integer sheetIndex,
                            Integer rowIndex) {
        return dataIndex(sheetIndex, rowIndex, 0, 0);
    }

    /**
     * 构建索引
     *
     * @param sheetIndex
     * @param rowIndex
     * @param dataIndexArr
     * @param cellIndexes
     * @return
     */
    public Export dataIndex(Integer sheetIndex,
                            Integer rowIndex,
                            int[] dataIndexArr,
                            int... cellIndexes) {
        return dataIndex(dataList, sheetIndex, rowIndex, dataIndexArr, cellIndexes);
    }

    /**
     * 构建索引
     *
     * @param sheetIndex
     * @param rowIndex
     * @param dataIndex
     * @param cellIndex
     * @return
     */
    public Export dataIndex(Integer sheetIndex,
                            Integer rowIndex,
                            int dataIndex,
                            int cellIndex) {
        return dataIndex(sheetIndex, rowIndex, new int[]{dataIndex}, cellIndex);
    }

    /**
     * 构建索引
     *
     * @param sheetIndex
     * @param rowIndex
     * @param dataIndex
     * @param cellIndex
     * @return
     */
    public Export dataIndex(List<D> dataList,
                            Integer sheetIndex,
                            Integer rowIndex,
                            int dataIndex,
                            int cellIndex) {
        return dataIndex(dataList, sheetIndex, rowIndex, new int[]{dataIndex}, cellIndex);
    }

    /**
     * 覆盖数据 构建索引
     *
     * @param dataList
     * @param sheetIndex
     * @param rowIndex
     * @param dataIndexArr
     * @param cellIndexes
     * @return
     */
    public Export dataIndex(List<D> dataList,
                            Integer sheetIndex,
                            Integer rowIndex,
                            int[] dataIndexArr,
                            int... cellIndexes) {

        if (!CollectionUtils.isEmpty(exportMap)) {

            exportMapList.add(exportMap);
        }

        if (!BUILD_DATA_FLAG.equals(sheetIndex) && !BUILD_DATA_FLAG.equals(rowIndex)) {
            UtilAssert.isEmpty(dataList, "[dataIndex] data list is empty!");
            exportMap = new HashMap<>(8);
            exportMap.put(ExcelEnum.DATA_LIST, dataList);
            this.dataList = dataList;
            checkExcelEnumNull(ExcelEnum.SHEET_INDEX, sheetIndex);
            checkExcelEnumNull(ExcelEnum.ROW_INDEX, rowIndex);
            checkExcelEnumNull(ExcelEnum.DATA_INDEX_ARR, dataIndexArr);
            checkExcelEnumNull(ExcelEnum.CELL_INDEXES, cellIndexes);
        }

        return this;
    }

    private void checkExcelEnumNull(ExcelEnum excelEnum, Object obj) {
        if (Objects.nonNull(obj)) {

            exportMap.put(excelEnum, obj);
        }
    }

    /**
     * offset
     *
     * @return
     */
    public Export offset(Integer offset) {

        UtilAssert.isNull(exportMap, "please use dataIndex method first!");
        exportMap.put(ExcelEnum.OFFSET, offset);
        return this;
    }


    /**
     * offset
     *
     * @return
     */
    public Export style(CellStyle cellStyle) {

        UtilAssert.isNull(exportMap, "please use dataIndex method first!");
        exportMap.put(ExcelEnum.STYLE, cellStyle);
        return this;
    }

    /**
     * tableHeader
     *
     * @return
     */
    public TableHeader tableHeader() {

        UtilAssert.isNull(exportMap, "please use dataIndex method first!");
        TableHeader tableHeader = TableHeader.getInstance(poi, this);
        exportMap.put(ExcelEnum.TABLE_HEADER, tableHeader);
        return tableHeader;
    }

    /**
     * 取出map数据进行导出
     */
    public POIHelper build() {

        dataIndex(dataList, null, null, BUILD_DATA_FLAG, BUILD_DATA_FLAG);
        UtilAssert.isEmpty(exportMapList, "[build] exportMapList is empty!");
        try {

            UtilAssert.isNull(Objects.isNull(os), poi.file, "[build] file is null!");
            UtilAssert.isNull(Objects.isNull(poi.file), os, "[build] OutputStream is null!");
            os = null == os ? new FileOutputStream(poi.file) : os;
            for (int i = 0; i < exportMapList.size(); i++) {

                Map<ExcelEnum, Object> map = exportMapList.get(i);
                List<D> dataList = (List<D>) map.get(ExcelEnum.DATA_LIST);
                Integer offset = (Integer) map.get(ExcelEnum.OFFSET);
                int[] dataIndexArr = (int[]) map.get(ExcelEnum.DATA_INDEX_ARR);
                dataIndexArr = ArrayUtils.isEmpty(dataIndexArr) ? new int[]{0} : dataIndexArr;
                int[] cellIndexArr = (int[]) map.get(ExcelEnum.CELL_INDEXES);
                cellIndexArr = ArrayUtils.isEmpty(cellIndexArr) ? new int[]{0} : cellIndexArr;
                TableHeader tableHeader = (TableHeader) map.get(ExcelEnum.TABLE_HEADER);
                Boolean clearSheet = (Boolean) map.get(ExcelEnum.CLEAR_SHEET);
                CellStyle cellStyle = (CellStyle) map.get(ExcelEnum.STYLE);
                clearSheet = !Objects.isNull(clearSheet);
                // 确定sheet索引
                Integer sheetIndex = (Integer) map.get(ExcelEnum.SHEET_INDEX);
                sheetIndex = Objects.isNull(sheetIndex) ? i : sheetIndex;
                Sheet sheet = poi.workbook.getSheetAt(sheetIndex);
                Integer rowIndex = (Integer) map.get(ExcelEnum.ROW_INDEX);
                if (Objects.isNull(rowIndex)) {
                    // 推断row
                    int lastRowNum = sheet.getLastRowNum();
                    Row row = sheet.getRow(lastRowNum);
                    rowIndex = null == row ? 0 : lastRowNum + 1;
                }
                // 初始化sheet
                sheet = initSheet(sheetIndex, rowIndex, clearSheet);
                // 构建数据
                buildData(dataList, sheet, rowIndex, dataIndexArr, cellIndexArr, offset, tableHeader, cellStyle);
                LOG.info("[build] success! sheetName: {}", sheet.getSheetName());
            }
            // 写出流
            poi.workbook.write(os);
        } catch (IOException e) {
            LOG.error("[exportFile] fail!", e);
        } finally {
            StreamUtils.close(os);
        }
        return poi;
    }

    /**
     * 初始化sheet
     *
     * @param sheetIndex
     * @param rowIndex
     * @param clearSheet
     */
    private Sheet initSheet(int sheetIndex, int rowIndex, boolean clearSheet) {

        Sheet sheet = poi.workbook.getSheetAt(sheetIndex);
        if (!clearSheet) {
            return sheet;
        }
        int lastRowNum = sheet.getLastRowNum();
        for (int sI = rowIndex; sI <= lastRowNum; sI++) {

            Row row = sheet.getRow(sI);
            if (null == row) {
                continue;
            }
            sheet.removeRow(row);
        }
        return sheet;
    }

    /**
     * 构建数据
     *
     * @param dataList
     * @param sheet
     * @param rowIndex
     * @param dataIndexArr
     * @param cellIndexArr
     * @param offset
     * @param tableHeader
     */
    private void buildData(List<D> dataList,
                           Sheet sheet,
                           int rowIndex,
                           int[] dataIndexArr,
                           int[] cellIndexArr,
                           Integer offset,
                           TableHeader tableHeader,
                           CellStyle cellStyle) {
        // 遍历数据列表
        for (int i = 0; i < dataList.size(); i++) {
            // 取出map
            D data = dataList.get(i);
            if (null != tableHeader) {
                // 创建表头row
                rowIndex = tableHeader.rowIndex;
                i--;
            }
            // 创建row
            Row row = Objects.isNull(sheet.getRow(rowIndex)) ? sheet.createRow(rowIndex) : sheet.getRow(rowIndex);
            // 确定开始索引
            int dataBeginIndex = dataIndexArr[0];
            int cellBeginIndex = cellIndexArr[0];
            // 封装RowBuild
            RowBuild rowBuild =
                    new RowBuild(tableHeader, dataBeginIndex, cellBeginIndex, offset, dataIndexArr, cellIndexArr, cellStyle);
            POIProcessFactory.getInstance((Class<D>) data.getClass()).exportProcess(data, row, rowBuild);
            rowIndex++;
            tableHeader = null;
        }
    }

    /**
     * 根据名称创建sheet
     *
     * @param sheetName
     * @return
     */
    public Export create(String sheetName) {

        if (sheetName.length() > SHEET_NAME_LENGTH) {
            LOG.warn("[create] sheetName length is greatThan 30! sheetName: {}", sheetName);
        }
        if (Objects.nonNull(poi.workbook.getSheet(sheetName))) {
            LOG.warn("[create] sheet: {} is already exist!", sheetName);
            return this;
        }
        poi.workbook.createSheet(sheetName);
        return this;
    }
}