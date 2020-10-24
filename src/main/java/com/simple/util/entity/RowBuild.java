package com.simple.util.entity;


import com.simple.util.poi.POIHelper;
import com.simple.util.poi.TableHeader;
import org.apache.poi.ss.usermodel.CellStyle;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author wujing
 * @date 2020/5/18 15:20
 */
public final class RowBuild {

    public RowBuild() {

    }

    public RowBuild(TableHeader tableHeader, Integer dataBeginIndex, Integer cellBeginIndex, Integer offsetValue, int[] dataIndexArr, int[] cellIndexArr, CellStyle cellStyle) {
        this.tableHeader = tableHeader;
        this.dataBeginIndex = dataBeginIndex;
        this.cellBeginIndex = cellBeginIndex;
        this.offsetValue = offsetValue;
        this.dataIndexArr = dataIndexArr;
        this.cellIndexArr = cellIndexArr;
        this.cellStyle = cellStyle;
    }

    /**
     * 表头
     */
    private TableHeader tableHeader;

    /**
     * 数据开始索引
     */
    private Integer dataBeginIndex;

    /**
     * 单元格开始索引
     */
    private Integer cellBeginIndex;

    /**
     * 偏移量
     */
    private Integer offsetValue;

    /**
     * 数据索引数组
     */
    private int[] dataIndexArr;

    /**
     * 单元格索引数组
     */
    private int[] cellIndexArr;

    /**
     * 单元格样式
     */
    private CellStyle cellStyle;

    public CellStyle getCellStyle() {
        return cellStyle;
    }

    public void setCellStyle(CellStyle cellStyle) {
        this.cellStyle = cellStyle;
    }

    public TableHeader getTableHeader() {
        return tableHeader;
    }

    public void setTableHeader(TableHeader tableHeader) {
        this.tableHeader = tableHeader;
    }

    public Integer getDataBeginIndex() {
        return dataBeginIndex;
    }

    public void setDataBeginIndex(Integer dataBeginIndex) {
        this.dataBeginIndex = dataBeginIndex;
    }

    public Integer getCellBeginIndex() {
        return cellBeginIndex;
    }

    public void setCellBeginIndex(Integer cellBeginIndex) {
        this.cellBeginIndex = cellBeginIndex;
    }

    public Integer getOffsetValue() {
        return offsetValue;
    }

    public void setOffsetValue(Integer offsetValue) {
        this.offsetValue = offsetValue;
    }

    public int[] getDataIndexArr() {
        return dataIndexArr;
    }

    public void setDataIndexArr(int[] dataIndexArr) {
        this.dataIndexArr = dataIndexArr;
    }

    public int[] getCellIndexArr() {
        return cellIndexArr;
    }

    public void setCellIndexArr(int[] cellIndexArr) {
        this.cellIndexArr = cellIndexArr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RowBuild rowBuild = (RowBuild) o;
        return Objects.equals(tableHeader, rowBuild.tableHeader) &&
                Objects.equals(dataBeginIndex, rowBuild.dataBeginIndex) &&
                Objects.equals(cellBeginIndex, rowBuild.cellBeginIndex) &&
                Objects.equals(offsetValue, rowBuild.offsetValue) &&
                Arrays.equals(dataIndexArr, rowBuild.dataIndexArr) &&
                Arrays.equals(cellIndexArr, rowBuild.cellIndexArr) &&
                Objects.equals(cellStyle, rowBuild.cellStyle);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(tableHeader, dataBeginIndex, cellBeginIndex, offsetValue, cellStyle);
        result = 31 * result + Arrays.hashCode(dataIndexArr);
        result = 31 * result + Arrays.hashCode(cellIndexArr);
        return result;
    }

    @Override
    public String toString() {
        return "RowBuild{" +
                "tableHeader=" + tableHeader +
                ", dataBeginIndex=" + dataBeginIndex +
                ", cellBeginIndex=" + cellBeginIndex +
                ", offsetValue=" + offsetValue +
                ", dataIndexArr=" + Arrays.toString(dataIndexArr) +
                ", cellIndexes=" + Arrays.toString(cellIndexArr) +
                ", cellStyle=" + cellStyle +
                '}';
    }
}
