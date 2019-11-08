import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang3.time.StopWatch;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;

import utils.FileUtilsHelper;
import utils.TikaConverter;

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
        String sss = FileUtilsHelper.getStringFromClassPathFile("inputFile");
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

        List<String> lines = FileUtilsHelper.readLinesFromResource("testLines.txt");
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

    @Test
    public void concatFilesIntoOne() throws Exception {
        String directory = "C:\\Users\\User\\Desktop\\Архив по работе\\Сущности Из Текста Извлечение сущностей\\Книги";
        List<File> files = new ArrayList<>(FileUtils.listFiles(new File(directory), FileFileFilter.FILE, FileFileFilter.FILE));
        StrBuilder strBuilder = new StrBuilder();
        for (File file : files) {
            strBuilder.append(FileUtilsHelper.readFileByExtension(file));
        }

        FileUtils.write(
            new File("C:\\Users\\User\\Desktop\\TESTS\\" + System.currentTimeMillis() + "_test.txt"),
            strBuilder.toString(),
            Charsets.UTF_8);
    }

    @Test
    public void neFileToMany() throws Exception {
        String directory = "C:\\Users\\User\\Desktop\\Архив по работе\\Сущности Из Текста\\INPUT\\Все книги";
        List<File> files = new ArrayList<>(FileUtils.listFiles(new File(directory), FileFileFilter.FILE, FileFileFilter.FILE));
        StrBuilder strBuilder = new StrBuilder();
        for (File file : files) {
            strBuilder.append(FileUtilsHelper.readFileByExtension(file));
        }

        List<String> messages = new ArrayList<>();
        // for (String str : strBuilder.toString().split("\n")) {
        //     messages.add(str);
        // }
        messages = splitByLength(strBuilder.toString(), 1000);

        int couter = 0;
        for (String message : messages) {
            FileUtils.write(
                new File("C:\\Users\\User\\Desktop\\TESTS\\OUTPUT\\" + couter + "_" + System.currentTimeMillis() + "_message.txt"),
                message,
                Charsets.UTF_8);
            couter++;
        }
    }

    private static List<String> splitByLength(String text, int neededLength) {
        List<String> parts = new ArrayList<>();

        int length = text.length();
        for (int i = 0; i < length; i += neededLength) {
            parts.add(text.substring(i, Math.min(length, i + neededLength)));
        }
        return parts;
    }

    @Test
    public void getTextPart() {
        String s = "Директор по IT\n"
                   + "\n"
                   + "Образование\n"
                   + "Основное\n"
                   + "1998  — Военно-Морской Институт Радиоэлектроники им. А. С. Попова, Средства связи, инженер, дииплом\n"
                   + "\n"
                   + "Повышение квалификации, курсы\n"
                   + "2011  — Охрана труда и техника безопасности на предприятии, ЦНТИ\n"
                   + "2010  — Строительство пассивных оптических сетей, Учебный центр, Лентелефонстрой\n"
                   + "2009  — Проектирование и строительство ВОЛС, Учебный центр, Лентелефонстрой\n"
                   + "2008  — Сметное дело в строительстве, Башкирский Государственный Университет (БГУ)\n"
                   + "2008  — Управление проектами в строительстве, Учебный центр, Голден Телеком\n"
                   + "2006  — Проектирование и монтаж СКС, Краснодарский Государственный Университет (КГУ)\n"
                   + "2005  — Техническая защита инф";
        System.out.println(s.substring(534, 548));
        System.out.println(s.substring(425, 453));
    }
}
