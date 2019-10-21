package utils;

import java.io.*;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

public class EncodingUtils {
    public static String detectEncoding(String file) throws IOException {

        UniversalDetector detector = new UniversalDetector(null);

        byte[] buf = new byte[4096];
        FileInputStream fis = new FileInputStream(file);

        int nread, alreadyRead = 0;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            alreadyRead += nread;
            detector.handleData(buf, 0, nread);

            // Max read 5MB
            if (alreadyRead > 5 * 1024 * 1024) {
                break;
            }
        }
        detector.dataEnd();
        fis.close();

        String charset = detector.getDetectedCharset();

        return StringUtils.defaultIfEmpty(charset, Charset.defaultCharset().name());
    }
}
