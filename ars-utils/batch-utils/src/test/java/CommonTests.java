import java.text.SimpleDateFormat;
import java.util.*;

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
    public void testDeleteEndOfNumber() {
        String s = "  dsa 9.0 ";
        System.out.println(s.replaceAll("\\.0", ""));
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
}
