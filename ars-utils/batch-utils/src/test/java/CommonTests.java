import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;

import files.FilesUtilsHelper;

public class CommonTests {
    @Test
    public void testCount() {
        Integer columnWidth = 1000 / 3;
        System.out.println(columnWidth);
    }

    @Test
    public void test2() throws Exception {
        String s = new SimpleDateFormat("dd.MM.yyyy hh:mm").format(new Date(1556571600000L));
        System.out.println(s);
    }

    @Test
    public void testFiles() {
        File file = new File("hello\\e.txt");
        System.out.println(file.getAbsolutePath());
    }

    @Test
    public void testDeleteEndOfNumber() {
        String s = "  dsa 9.0 ";
        System.out.println(s.replaceAll("\\.0", ""));
    }

    @Test
    public void tryParseAllNumbersTypes() {
        try {
            new BigDecimal("1.1".replaceAll(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Broken");
        }
    }

    @Test
    public void optionalTest() {
        Assert.assertEquals(Optional.empty().orElse("EmptyValue"), "EmptyValue");
    }

    @Test
    public void testDate() {
        org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTime dateTime = dtf.parseDateTime("11-11-2011");
        System.out.println(dateTime.getYear());
    }

    @Test
    public void te() throws Exception {
        String sss = FilesUtilsHelper.getStringFromClassPathFile("inputFile");
        String s = sss.split("=")[0];
        String[] ss = s.split("[.]");
        StringBuilder velert = new StringBuilder();
        int max = 2;
        for (int i = 0; i < max; i++) {
            velert.append(ss[i]).append(".");
        }
        for (int i = max; i < ss.length; i++) {
            String v = ss[i];
            if (i == max) {
                velert.append(v);
            } else {
                velert.append(Character.toUpperCase(v.charAt(0)));
                velert.append(v.substring(1, v.length()));
            }
        }
        System.out.println(velert.toString());
    }

    @Test
    public void testStringPosition() {
        String s = "http://192.168.0.76:8080/Prairie/get/internet/all/msisdn:' 89210000112'/-/sms/json/-";
        System.out.println(s.substring(58, s.length() - 1));
    }
}
