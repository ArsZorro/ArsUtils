import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.time.StopWatch;
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

    @Test
    public void testMatching() {
        String searchString = "32114132131f fdsdsae213 edsa21";
        String regexp = ".*";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(searchString);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    private static final Long DEFAULT_REPORT_BUILDING_MAX_TIME = 10_000L;

    @Test
    public void test7() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        while (stopWatch.getTime() < DEFAULT_REPORT_BUILDING_MAX_TIME) {
            if (stopWatch.getTime() % 1000 == 0) {
                System.out.println(stopWatch.getTime());
            }
        }
    }

    @Test
    public void iterator() {
        Set<String> list = Sets.newHashSet("hi", "ad", "bs");
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
            iterator.remove();
        }
    }

    @Test
    public void localizationNamesMaker() throws IOException {
        // String text = "    ULAUTORF,\n"
        //               + "    AFF,\n"
        //               + "    BALANCE,\n"
        //               + "    BENEF,\n"
        //               + "    EXT_SOURCE,\n"
        //               + "    EMPLOYER,\n"
        //               + "    EGRUL,\n"
        //               + "    SVI,\n"
        //               + "    IP_R,\n"
        //               + "    IP_T,\n"
        //               + "    IP_A,\n"
        //               + "    FLAUTORF,\n"
        //               + "    BSFL,\n"
        //               + "    CASB,\n"
        //               + "    CKKI,\n"
        //               + "    CASB11,\n"
        //               + "    RAITING_R,\n"
        //               + "    RAITING_212,\n"
        //               + "    RAITING,\n"
        //               + "    RAITING_2,\n"
        //               + "    EXP,\n"
        //               + "    UPASSPORT,\n"
        //               + "    PPFMS,\n"
        //               + "    BS,\n"
        //               + "    BSPD,\n"
        //               + "    TSAUTORF,\n"
        //               + "    AUTOCHECK,\n"
        //               + "    BIP,\n"
        //               + "    IDFL,\n"
        //               + "    IDADDRESS";
        // Set<String> lines = Sets.newHashSet(text.split("\n"));

        List<String> lines = FilesUtilsHelper.readLinesFromResource("testLines.txt");
        for (String line :lines)
        {
            line = line.trim().replaceAll(",", "");
            StringBuilder nameBuilder = new StringBuilder();
            boolean isFirst = true;
            for (String word : line.split("_")) {
                if (isFirst) {
                    nameBuilder.append(word.toLowerCase());
                    isFirst = false;
                } else {
                    String first = word.substring(0, 1).toUpperCase();
                    String second = word.substring(1, word.length()).toLowerCase();
                    nameBuilder.append(first).append(second);
                }
            }
            System.out.println(nameBuilder.toString());
        }
    }
}
