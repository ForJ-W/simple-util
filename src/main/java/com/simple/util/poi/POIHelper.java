package com.simple.util.poi;

import com.simple.util.StreamUtils;
import com.simple.util.asserts.UtilAssert;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/3/25 13:53
 */
@SuppressWarnings("unchecked")
public final class POIHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(POIHelper.class);
    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";
    InputStream is;
    Workbook workbook;
    File file;

    public static POIHelper getInstance() {

        return new POIHelper().initWorkbook(null, XLSX);
    }

    public static POIHelper getInstance(File file) {

        return new POIHelper().initFile(file, XLSX);
    }

    public static POIHelper getInstance(String filename) {

        UtilAssert.isBlank(filename, "[<init>] filename is blank!");
        // 创建临时文件
        int pointIndex = filename.lastIndexOf(".");
        String prefix = filename.substring(0, pointIndex);
        String suffix = filename.substring(pointIndex);
        POIHelper poi = new POIHelper();
        poi.initFile(poi.createFile(prefix, suffix), XLSX);
        poi.workbook.createSheet(prefix);
        return poi;
    }

    /**
     * create file
     *
     * @param prefix
     * @param suffix
     * @return
     */
    private File createFile(String prefix, String suffix) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(prefix, suffix);
            System.out.println(tempFile);
        } catch (IOException e) {
            LOGGER.warn("tempFile create fail!", e);
        }
        return tempFile;
    }

    public static POIHelper getInstance(InputStream is, String fileType) {

        return new POIHelper().initWorkbook(is, fileType);
    }

    public static POIHelper getInstance(File file, String fileType) {

        return new POIHelper().initFile(file, fileType);
    }

    /**
     * init file
     *
     * @param file
     * @param fileType
     */
    private POIHelper initFile(File file, String fileType) {

        UtilAssert.isNull(file, "[init] file is null!");
        LOGGER.info("[init] file path: {}", file.getAbsolutePath());
        buildFileInputStream(file);
        initWorkbook(is, fileType);
        return this;
    }

    /**
     * 构造文件
     *
     * @param file
     * @return
     */
    private void buildFileInputStream(File file) {

        if (modify(this.file, file)) {
            this.file = file;
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * modify
     *
     * @param obj
     * @param other
     * @return
     */
    private boolean modify(Object obj, Object other) {

        return Objects.isNull(obj) || !obj.equals(other);
    }

    /**
     * init workbook
     *
     * @param inputStream
     * @param fileType
     */
    private POIHelper initWorkbook(InputStream inputStream, String fileType) {

        try {
            workbook = getWorkbook(inputStream, fileType);
        } catch (IOException e) {
            LOGGER.warn("[init] workbook init fail!", e);
        }
        return this;
    }

    /**
     * 获取Excel工作簿
     *
     * @param inputStream 文件输入流
     * @param fileType    文件类型(后缀名)
     * @return
     * @throws IOException
     */
    private Workbook getWorkbook(InputStream inputStream, String fileType) throws IOException {

        Workbook workbook = null;
        try {
            // 根据文件类型获取对应工作簿
            if (fileType.equalsIgnoreCase(XLS)) {
                if (Objects.isNull(inputStream) || 0 == inputStream.available()) {

                    return new HSSFWorkbook();
                }
                workbook = new HSSFWorkbook(inputStream);
            } else if (fileType.equalsIgnoreCase(XLSX)) {
                if (Objects.isNull(inputStream) || 0 == inputStream.available()) {

                    return new XSSFWorkbook();
                }
                workbook = new XSSFWorkbook(inputStream);
            }
        } finally {
            StreamUtils.close(inputStream);
        }
        return workbook;
    }

    /**
     * export
     *
     * @return
     */
    public Export export() {

        return Export.getInstance(this);
    }

    /**
     * export
     *
     * @param dataList
     * @return
     */
    public <D> Export export(List<D> dataList) {

        return Export.getInstance(this, dataList);
    }

    /**
     * parse
     *
     * @return
     */
    public Parse parse() {

        return Parse.getInstance(this);
    }

    /**
     * get file
     *
     * @return
     */
    public File getFile() {

        UtilAssert.isNull(file, "[getFile] file is null!");
        return file;
    }

    /**
     * 关闭workBook
     */
    public void close() {
        try {

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
