package com.simple.util;

import java.io.*;

/**
 * @author wujing
 * @date 2020/3/29 11:39
 */
public final class StreamUtils {

    private StreamUtils() {
    }

    /**
     * fileToByte
     *
     * @param tradeFile
     * @return
     */
    public static byte[] fileToByte(File tradeFile) {

        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        byte[] buffer = null;
        try {

            fis = new FileInputStream(tradeFile);
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[8192];
            int n;
            while ((n = fis.read(b)) != -1) {

                bos.write(b, 0, n);
            }

            buffer = bos.toByteArray();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {

            streamHold(bos, fis);
        }

        return buffer;
    }

    /**
     * 流处理 close and flush
     *
     * @param obj
     */
    public static void streamHold(Object... obj) {

        for (Object o : obj) {

            if (o instanceof Closeable) {

                close((Closeable) o);
            }

            if (o instanceof Flushable) {

                flush((Flushable) o);
            }
        }
    }

    /**
     * close
     *
     * @param cs
     */
    public static void close(Closeable... cs) {

        if (null != cs) {

            for (Closeable stream : cs) {

                if (null != stream) {

                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * flush
     *
     * @param fs
     */
    public static void flush(Flushable... fs) {

        if (null != fs) {

            for (Flushable stream : fs) {

                if (null != stream) {

                    try {
                        stream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
