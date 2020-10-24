package com.simple.util.strategy;

import com.simple.util.poi.POIHelper;
import com.simple.util.entity.RowBuild;
import com.simple.util.poi.TableHeader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @author wujing
 * @date 2020/5/10 13:30
 */
public interface POIProcess<D> {

    Object LOCK = new Object();

    /**
     * 解析
     * @param instance
     * @param firstCellIndex
     * @param lastCellIndex
     * @param tableHeaderRow
     * @param row
     * @return
     */
    D parseProcess(D instance, int firstCellIndex, int lastCellIndex,
                   Row tableHeaderRow, Row row);

    /**
     * 导出
     *
     * @param data
     * @param row
     * @param rowBuild
     */
    void exportProcess(D data, Row row, RowBuild rowBuild);


    /**
     * 装换属性值为对应单元格类型
     *
     * @param cell
     * @param value
     */
    default void convertValueToCellType(Cell cell, Object value) {
        if (null == cell || null == value) {

            return;
        }

        if (value instanceof Integer
                || value instanceof Long
                || value instanceof Double) {

            cell.setCellValue(Double.parseDouble(String.valueOf(value)));
        } else if (value instanceof Date) {

            cell.setCellValue((Date) value);
        } else if (value instanceof Boolean) {

            cell.setCellValue((Boolean) value);
        } else if (value instanceof Calendar) {

            cell.setCellValue((Calendar) value);
        } else {

            cell.setCellValue(String.valueOf(value));
        }
    }

    /**
     * 将单元格内容转换为字符串
     *
     * @param cell
     * @return
     */
    default String convertCellValueToString(Cell cell) {
        if (null == cell) {
            return null;
        }

        String returnValue = null;
        switch (cell.getCellType()) {
            // 数字
            case NUMERIC:
                Double doubleValue = cell.getNumericCellValue();
                // 格式化科学计数法
                DecimalFormat df = new DecimalFormat("0");
                returnValue = df.format(doubleValue);
                break;
            // 字符串
            case STRING:
                returnValue = cell.getStringCellValue();
                break;
            // 布尔
            case BOOLEAN:
                Boolean booleanValue = cell.getBooleanCellValue();
                returnValue = booleanValue.toString();
                break;
            // 空值
            case BLANK:
                break;
            // 公式
            case FORMULA:
                returnValue = cell.getCellFormula();
                break;
            // 故障
            case ERROR:
                break;
            default:
                break;
        }

        return returnValue;
    }

    /**
     * 生成指定长度数组,并以索引作为元素
     *
     * @param indexArr
     * @param dataLength
     * @param greaterThanOne
     * @param beginIndex
     * @return
     */
    default int[] indexArrProcess(int[] indexArr, int dataLength, boolean greaterThanOne, int beginIndex) {

        if (!greaterThanOne) {

            final int[] arr = new int[dataLength];
            for (int i = 0; i < dataLength; i++) {
                arr[i] = beginIndex++;
            }
            indexArr = arr;
        }
        return indexArr;
    }

    /**
     * 索引数组长度判断
     *
     * @param dataIndexArr
     * @param cellIndexArr
     * @return
     */
    default boolean lengthJudge(int[] dataIndexArr, int[] cellIndexArr) {

        return dataIndexArr.length == cellIndexArr.length && cellIndexArr.length == 1;
    }


    /**
     * 表头处理
     *
     * @param iTableHeader
     * @param tableHeader
     * @param headerName
     * @param cell
     * @return
     */
    default int tableHeaderProcess(int iTableHeader, TableHeader tableHeader, String headerName, Cell cell) {
        String[] valueArr = tableHeader.getValueArr();
        String cellValue = Objects.isNull(valueArr) ? headerName : valueArr[iTableHeader++];
        cell.setCellValue(cellValue);
        cell.setCellStyle(tableHeader.getCellStyle());
        return iTableHeader;
    }
}
