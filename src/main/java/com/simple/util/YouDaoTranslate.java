package com.simple.util;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.simple.util.StringUtil.merge;
import static com.simple.util.constant.StringConstant.UTF_8;
import static com.simple.util.constant.TranslateConstant.*;

/**
 * @author wujing
 * @date 2020/7/7 16:23
 */
public final class YouDaoTranslate {

    private static Logger LOGGER = LoggerFactory.getLogger(YouDaoTranslate.class);
    private static final String DEFAULT_SIGN_TYPE = "v3";
    private static final int DEFAULT_PARAM_MAP_CAPACITY = 10;
    private static final Map<String, String> PARAM_MAP = new HashMap<>(DEFAULT_PARAM_MAP_CAPACITY);
    private static String TEXT_URL = "https://openapi.youdao.com/api";
    private static String FILE_UPLOAD_URL = "https://openapi.youdao.com/file_trans/upload";
    private static volatile YouDaoTranslate youDaoTranslate;
    private static final Object LOCK=new Object();

    private YouDaoTranslate() {
    }

    public static YouDaoTranslate getInstance(String appKey, String secret) {

        PARAM_MAP.put(APP_KEY, appKey);
        PARAM_MAP.put(SECRET, secret);
        if (null == youDaoTranslate) {
            synchronized (LOCK) {
                if (null == youDaoTranslate) {

                    youDaoTranslate = new YouDaoTranslate();
                }
            }
        }
        return youDaoTranslate;
    }

    /**
     * 通用强制往目标语言进行翻译
     *
     * @param str
     * @param sourceLanguage
     * @param targetLanguage
     * @return
     */
    public Map<String, Object> translate(String str, String sourceLanguage, String targetLanguage) {

        final StringBuilder SB = new StringBuilder();
        PARAM_MAP.put(SOURCE_LANGUAGE, sourceLanguage);
        PARAM_MAP.put(TARGET_LANGUAGE, targetLanguage);
        final int maxSize = 5000;
        int beginIndex = 0;
        int endIndex = maxSize;
        Map<String, Object> map = new HashMap<>(1);
        Object obj = null;
        while (str.length() > beginIndex) {

            String beTranslatedStr = (str.length() - beginIndex) > maxSize
                    ? str.substring(beginIndex, endIndex)
                    : str.substring(beginIndex);
            map = genericTranslate(beTranslatedStr, TEXT_URL);
            obj = map.get(TRANSLATION);
            if (Objects.isNull(obj)) {
                continue;
            }
            String content = obj.toString();
            content = content.substring(1, content.length() - 1);
            SB.append(content);
            beginIndex = endIndex;
            endIndex += maxSize;
        }
        map.put(TRANSLATION, obj);
        return map;
    }

    public Map<String, Object> translateFile(String filePath, String sourceLanguage, String targetLanguage) throws IOException {

        String fileStr = FileUtils.readFileToString(new File(filePath), UTF_8);
        return translate(fileStr, sourceLanguage, targetLanguage);
    }

    /**
     * 通用强制往目标语言进行翻译
     *
     * @param beTranslatedStr
     * @param url
     * @return
     */
    private Map<String, Object> genericTranslate(String beTranslatedStr, String url) {

        PARAM_MAP.put(SIGN_TYPE, DEFAULT_SIGN_TYPE);
        PARAM_MAP.put(SALT, String.valueOf(System.currentTimeMillis()));
        PARAM_MAP.put(BE_TRANSLATED_STR, beTranslatedStr);
        PARAM_MAP.put(STRICT, "true");
        String currentTimeSecond = String.valueOf(System.currentTimeMillis() / 1000);
        PARAM_MAP.put(CURRENT_TIME, currentTimeSecond);
        String signStr = merge(PARAM_MAP.get(APP_KEY),
                truncate(beTranslatedStr),
                System.currentTimeMillis(),
                currentTimeSecond,
                PARAM_MAP.get(SECRET));
        String sign = getDigest(signStr);
        PARAM_MAP.put(SIGN, sign);
        Map<String, Object> resultMap = null;
        try {

            String json = requestForHttp(url, PARAM_MAP);
            resultMap = JsonUtils.toMap(json, String.class, Object.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    /**
     * 翻译成中文
     *
     * @param beTranslatedStr
     * @return
     */
    public List translateToCN(String beTranslatedStr) {

        return (List) translate(beTranslatedStr, AUTO, CHINESE).get(TRANSLATION);
    }

    /**
     * 翻译成英文
     *
     * @param beTranslatedStr
     * @return
     */
    public List translateToEN(String beTranslatedStr) {

        return (List) translate(beTranslatedStr, AUTO, ENGLISH).get(TRANSLATION);
    }

    /**
     * 请求翻译http
     *
     * @param url
     * @param paramMap
     * @return
     * @throws IOException
     */
    private String requestForHttp(String url, Map<String, String> paramMap) throws IOException {

        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> paramsList = new ArrayList<>();

        Iterator<Map.Entry<String, String>> it = paramMap.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry<String, String> en = it.next();
            String key = en.getKey();
            String value = en.getValue();
            paramsList.add(new BasicNameValuePair(key, value));
        }

        httpPost.setEntity(new UrlEncodedFormEntity(paramsList, UTF_8));
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try {

            /*
                不考虑音频
            Header[] contentType = httpResponse.getHeaders(CONTENT_TYPE);
            if ("audio/mp3".equals(contentType[0].getValue())) {
                //如果响应是wav
                HttpEntity httpEntity = httpResponse.getEntity();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(baos);
                byte[] result = baos.toByteArray();
                EntityUtils.consume(httpEntity);
                if (result != null) {//合成成功
                    String file = "C:/Users/WU309/Desktop" + System.currentTimeMillis() + ".mp3";
                    byteToFile(result, file);
                }
            } else {
                */

            HttpEntity httpEntity = httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity, UTF_8);
            EntityUtils.consume(httpEntity);
//            }
        } finally {

            StreamUtils.close(httpResponse);
        }

        return result;
    }

    /**
     * 截取部分str
     *
     * @param q
     * @return
     */
    private String truncate(String q) {

        if (q == null) {
            return null;
        }
        int len = q.length();
        String result;
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }

    /**
     * 加密
     *
     * @param string
     * @return
     */
    private String getDigest(String string) {

        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = new byte[0];
        try {
            btInput = string.getBytes(UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
