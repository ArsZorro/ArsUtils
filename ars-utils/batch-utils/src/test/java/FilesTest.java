import org.junit.Test;

import files.FilesUtilsHelper;

public class FilesTest {
    @Test
    public void test() throws Exception {
        FilesUtilsHelper filesUtilsHelper = new FilesUtilsHelper();
        filesUtilsHelper.createManyBigFiles("", "", "", 0, 3550000);
    }
}
