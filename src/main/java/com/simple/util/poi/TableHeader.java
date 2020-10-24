package com.simple.util.poi;

import org.apache.poi.ss.usermodel.CellStyle;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/22 14:55
 */
public class TableHeader {

    Integer rowIndex;
    private String[] valueArr = {};
    private CellStyle cellStyle;
    private final Export export;
    private final POIHelper poi;

    static TableHeader getInstance(POIHelper poi, Export export) {

        return new TableHeader(poi, export);
    }

    private TableHeader(POIHelper poi, Export export) {

        this.poi = poi;
        this.cellStyle = poi.workbook.createCellStyle();
        this.export = export;
    }

    /**
     * 表头索引
     *
     * @param rowIndex
     * @return
     */
    public Export index(int rowIndex) {

        this.rowIndex = rowIndex;
        return Export.getInstance(poi);
    }

    /**
     * 默认表头索引 0
     *
     * @return
     */
    public Export index() {

        return index(0);
    }

    public CellStyle getCellStyle() {
        return cellStyle;
    }

    public String[] getValueArr() {
        return valueArr;
    }

    /**
     * 构造表头
     *
     * @param value
     * @return
     */
    public TableHeader value(String... value) {

        valueArr = value;
        return this;
    }

    public TableHeader style(CellStyle cellStyle) {
        this.cellStyle = cellStyle;
        return this;
    }
}
