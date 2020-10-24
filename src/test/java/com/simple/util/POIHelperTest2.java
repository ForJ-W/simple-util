package com.simple.util;


import com.simple.util.poi.POIHelper;
import com.simple.util.pojo.RowData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wujing
 * @date 2020/5/6 21:58
 */
@SuppressWarnings("unchecked")
public class POIHelperTest2 {

    /**
     * entity list
     */
    private ArrayList<RowData> rowDataList;

    /**
     * map list
     */
    private List<Map> mapList;

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(10, 50, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>());


    /**
     * template file (have three sheet)
     */
    private File file;

    public static void main(String[] args) {

        POIHelperTest2 excelHelperTest = new POIHelperTest2();
        excelHelperTest.dataInit();
        for (int i = 0; i < 10; i++) {

            EXECUTOR.execute(() -> POIHelper.getInstance("excelTest.xlsx")
                    .export(excelHelperTest.rowDataList)
                    .dataIndex(0, 0, 0, 0)
                    .build()
                    .getFile()
                    .deleteOnExit());
//            EXECUTOR.execute(excelHelperTest::manyDataTest);
        }

    }

    @Test
    public void manyDataTest() {


        ArrayList<Map> data1 = new ArrayList<>();
        ArrayList<Map> data2 = new ArrayList<>();
        ArrayList<Map> data3 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            HashMap<String, String> map = new LinkedHashMap<>();
            map.put("VIN码", "VIN码的值" + i);
            map.put("车牌号", "车牌号的值" + i);
            map.put("外观颜色", "外观颜色的值" + i);
            map.put("内饰颜色", "内饰颜色的值" + i);
            map.put("车辆绑定时间", "车辆绑定时间的值" + i);
            map.put("车辆解绑时间", "车辆解绑时间的值" + i);
            data1.add(map);
            data2.add(map);
            data3.add(map);
        }
        String[] tableHeaderArr = new String[]{"VIN码", "车牌号",
                "外观颜色1", "内饰颜色1", "车辆绑定时间1", "车辆解绑时间1",
                "外观颜色2", "内饰颜色2", "车辆绑定时间2", "车辆解绑时间2",
                "外观颜色3", "内饰颜色3", "车辆绑定时间3", "车辆解绑时间3"};
        POIHelper build = POIHelper.getInstance("excelTest.xlsx")
                .export()
                .dataIndex(data1, 0, 1, 0, 0)
                .tableHeader().value(tableHeaderArr).index()
                .dataIndex(data2, 0, 1, 2, 6).offset(4)
                .tableHeader().value(tableHeaderArr).index()
                .dataIndex(data3, 0, 1, 2, 10).offset(4)
                .tableHeader().value(tableHeaderArr).index()
                .build();

        build.getFile().deleteOnExit();
    }

    @Before
    public void dataInit() {

        try {
            file = new ClassPathResource("template/testDemo.xlsx").getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        rowDataList = new ArrayList<>();
        rowDataList.add(new RowData("cell1", "cell2", "cell3", "cell4", "cell5"));
        rowDataList.add(new RowData("cell11", "cell22", "cell33", "cell44", "cell55"));
        rowDataList.add(new RowData("cell111", "cell222", "cell333", "cell444", "cell555"));
        rowDataList.add(new RowData("cell1111", "cell2222", "cell3333", "cell4444", "cell5555"));
        rowDataList.add(new RowData("cell11111", "cell22222", "cell33333", "cell44444", "cell55555"));

        mapList = rowDataList.stream().map(rowData -> {

            Map map = new LinkedHashMap<>();
            map.put("column1", rowData.getColumn1());
            map.put("column2", rowData.getColumn2());
            map.put("column3", rowData.getColumn3());
            map.put("column4", rowData.getColumn4());
            map.put("column5", rowData.getColumn5());
            return map;
        }).collect(Collectors.toList());

    }

    /**
     * 在已有excel模板基础上导出数据
     */
    @Test
    public void alreadyExistTemplateExportTest() throws IOException {

        ArrayList<Map> sheet1 = new ArrayList<>();
        ArrayList<Map> sheet2 = new ArrayList<>();
        ArrayList<Map> sheet3 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            HashMap<String, String> map = new LinkedHashMap<>();
            map.put("用户账号", "用户账号的值" + i);
            map.put("报文时间", "报文时间的值" + i);
            map.put("绑定的车架号", "绑定的车架号的值" + i);
            map.put("纬度标识", "纬度标识的值" + i);
            map.put("经度", "经度的值" + i);
            map.put("经度标识", "经度标识的值" + i);
            map.put("定位状态", "定位状态的值" + i);
            sheet1.add(map);
        }
        for (int i = 0; i < 10; i++) {

            HashMap<String, String> map = new LinkedHashMap<>();
            map.put("邮箱", "邮箱的值" + i);
            map.put("手机区号", "手机区号的值" + i);
            map.put("手机号", "手机号的值" + i);
            map.put("昵称", "昵称的值" + i);
            map.put("性别", "性别的值" + i);
            map.put("国家地区", "国家地区的值" + i);
            map.put("紧急联系人姓名", "紧急联系人姓名的值" + i);
            map.put("关系", "关系的值" + i);
            map.put("紧急联系人电话", "紧急联系人电话的值" + i);
            map.put("下载日期", "下载日期的值" + i);
            sheet2.add(map);
        }
        for (int i = 0; i < 10; i++) {

            HashMap<String, String> map = new LinkedHashMap<>();
            map.put("VIN码", "VIN码的值" + i);
            map.put("车牌号", "车牌号的值" + i);
            map.put("外观颜色", "外观颜色的值" + i);
            map.put("内饰颜色", "内饰颜色的值" + i);
            map.put("车辆绑定时间", "车辆绑定时间的值" + i);
            map.put("车辆解绑时间", "车辆解绑时间的值" + i);
            sheet3.add(map);
        }
        POIHelper.getInstance("excelTest.xlsx")
                .export()
                .dataIndex(sheet1, 0, 1, null, null)
                .dataIndex(sheet2, 0, 1, null, null)
                .dataIndex(sheet3, 0, 1, null, null)
                .build();
//
//        new ExcelHelper(file)
//                .export(mapList)
//                // 设置索引 数据集合,表索引,行索引,数据索引,单元格索引
//                // 不去覆盖mapList,直接配置索引
//                // 第一张表第一行,第2个数据,第三个单元格
//                .dataIndex(0, 0, 1,2)
//                // 覆盖数据集合,顺便测试entityList
//                // 第2张表,第3行,第3,4,5个数据,第1,2,3个单元格
//                .dataIndex(rowDataList,1,2,new int[]{2,3,4},new int[]{0,1,2})
//                // 第3张表,第1行,第1个数据开始与第1个单元格开始,偏移4个 (1,2,3,4),并设置表头
//                .dataIndex().offset(4)
//                // 设置表头 当设置表头后,后续的数据自动在表头索引后的行填充
//                .tableHeader().center().value("表头1", "表头2", "表头3", "表头4").rowIndex()
//                // build后才开始导出
//                .build();
    }

    /**
     * 在无文件的情况下导出数据
     */
    @Test
    public void exportTest() {

        // 默认通过文件名创建一个单sheet 文件
        POIHelper.getInstance("tempFile.xlsx")
                .export(mapList)
                // 设置索引 数据集合,表索引,行索引,数据索引,单元格索引
                // 不去覆盖mapList,直接配置索引
                // 第一张表第一行,第2个数据,第三个单元格
                .dataIndex(0, 0, 1, 2)
                // 覆盖数据集合,顺便测试entityList
                // 第2张表,第3行,第3,4,5个数据,第1,2,3个单元格
                // 若是多张表需要 手动创建
                .create("sheet2")
                .dataIndex(rowDataList, 1, 2, new int[]{2, 3, 4}, new int[]{0, 1, 2})
                // 第3张表,第1行,第1个数据开始与第1个单元格开始,偏移4个 (1,2,3,4)
                .create("sheet3")
                .dataIndex(null, null, 0, 0).offset(4)
                // build后才开始导出
                .build();
    }


    @Test
    public void exportMoreTest() throws IOException {

        ArrayList<Map> sheet1 = new ArrayList<>();
        ArrayList<Map> sheet2 = new ArrayList<>();
        ArrayList<Map> sheet3 = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            HashMap<String, String> map = new LinkedHashMap<>();
            map.put("sheet1-key1-" + i, "sheet1-value1-" + i);
            map.put("sheet1-key2-" + i, "sheet1-value2-" + i);
            sheet1.add(map);
        }
        for (int i = 0; i < 10; i++) {

            HashMap<String, String> map = new LinkedHashMap<>();
            map.put("sheet2-key1-" + i, "sheet2-value1-" + i);
            map.put("sheet2-key2-" + i, "sheet2-value2-" + i);
            sheet2.add(map);
        }
        for (int i = 0; i < 10; i++) {

            HashMap<String, String> map = new LinkedHashMap<>();
            map.put("sheet3-key1-" + i, "sheet3-value1-" + i);
            map.put("sheet3-key2-" + i, "sheet3-value2-" + i);
            sheet3.add(map);
        }

        POIHelper.getInstance("sheet1.xlsx").export()
                .dataIndex(sheet1, null, null, 0, 0).offset(1)
                .create("sheet2")
                .dataIndex(sheet2, null, null, 0, 0)
                .create("sheet3")
                .dataIndex(sheet3, null, null, 0, 0)
                .build();

//        outPutFile(file, "C:/Users/WU309/Desktop/例子.xlsx");
//        for (int i = 0; i < 3; i++) {
//
//            exportTest();
//            alreadyExistTemplateExportTest();
//        }
    }

    /**
     * outPut file
     *
     * @param file
     * @param path
     * @throws IOException
     */
    private void outPutFile(File file, String path) throws IOException {

        FileInputStream fis = new FileInputStream(file);
        OutputStream os = new FileOutputStream(path);
        int readCount = 0;
        byte[] buffer = new byte[8192];
        while ((readCount = fis.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, readCount);
        }
        os.close();
        fis.close();
    }

    @Test
    public void parseTest() {

        ArrayList dataList = new ArrayList<>();
        ArrayList dataMapList = new ArrayList<>();

        Map<String, Map<Object, Object>> parseMap = POIHelper.getInstance(file)
                .parse()
                // 需要sheet row function
                .parseSheet(dataList, RowData.class, 0, 0)
                .parseSheet(dataMapList, Map.class, 1, 0)
                .getParseMap();

        System.out.println(parseMap);

        System.out.println(dataList);
        System.out.println(dataMapList);
    }
}