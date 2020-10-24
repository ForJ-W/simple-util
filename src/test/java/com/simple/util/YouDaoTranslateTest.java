package com.simple.util;

import com.simple.util.constant.StringConstant;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static com.simple.util.constant.TranslateConstant.CHINESE;
import static com.simple.util.constant.TranslateConstant.ENGLISH;


/**
 * @author wujing
 * @date 2020/7/7 16:38
 */
public class YouDaoTranslateTest {

    private YouDaoTranslate translate;

    @Before
    public void initTranslate() {
//        translate = YouDaoTranslate.getInstance();
    }

    @Test
    public void textTranslate() throws IOException {

        String filepath = "R:/download/chrome/redisNew.conf";
        Map map = translate.translateFile(filepath, ENGLISH, CHINESE);
        System.out.println(map);
    }

    @Test
    public void translateTest() {

//        Map map = translate.translate("red", "auto", CHINESE);

        System.out.println(translate.translateToEN("绿色"));
    }

}