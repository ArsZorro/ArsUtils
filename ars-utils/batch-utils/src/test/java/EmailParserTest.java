import java.io.*;
import java.util.*;

import org.junit.Test;

import email.Email;
import email.EmailParser;
import utils.FileUtilsHelper;
import utils.TikaConverter;

public class EmailParserTest {
    @Test
    public void base() throws Exception {
        EmailParser emailParser = new EmailParser();
        // String path = "email/example.eml";
        // String path = "email/sample_body_data.eml";
        // String path = "email/sample_bug27257.eml";
        // String path = "email/sample_invalid_date1.eml";
        // String path = "email/sample_large.eml";
        // String path = "email/sample_mime.eml";
        String path = "email/sample_mime2.eml";

        String manyFiles = "C:\\Users\\User\\Desktop\\TESTS\\emails";
        // String manyFiles = "C:\\Users\\User\\Desktop\\TESTS\\emails_my\\from_and_to_null";
        // String manyFiles = "C:\\Users\\User\\Desktop\\TESTS\\emails_my\\cc";
        // String manyFiles = "C:\\Users\\User\\Desktop\\TESTS\\emails_my";

        final List<File> files = FileUtilsHelper.listFilesForFolder(new File(manyFiles));

        List<Email> emails = new ArrayList<>();
        for (File file : files) {
            // File file = FileUtilsHelper.getClassPathFile(path);

            // final String eml = TikaConverter.process(file, "eml");
            Email email = emailParser.parseEmail(file.getAbsolutePath());
            emails.add(email);
        }
        System.out.println();
    }
}
