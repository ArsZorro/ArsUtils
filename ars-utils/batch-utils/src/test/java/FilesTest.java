import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.junit.Test;

import files.FilesUtilsHelper;

public class FilesTest {
    @Test
    public void test() throws Exception {
        FilesUtilsHelper filesUtilsHelper = new FilesUtilsHelper();
        filesUtilsHelper.createManyBigFiles("", "", "", 0, 3550000);
    }

    @Test
    public void test2() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String dateInString = "00-00-1900 00:00:00";
        Date date = sdf.parse(dateInString);

        System.out.println(dateInString);
        System.out.println("Date - Time in milliseconds : " + date.getTime());

        // Date date = new Date();
        // date.setYear(1920);
        // System.out.println(date.getTime());
        // String dateS = new SimpleDateFormat("YYYY-MM-DD").format(date);
        // System.out.println();
    }
}
