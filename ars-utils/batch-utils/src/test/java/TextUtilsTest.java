import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import files.FilesUtilsHelper;
import table.model.ExtractableCell;
import table.model.ExtractableRow;

public class TextUtilsTest {
    @Test
    public void testExcelTokenExtractor() {
        Map<Integer, String> map = new HashMap<>();
        System.out.println(map.get(1));
        ExtractableRow row = new ExtractableRow(1);
        ExtractableCell cell = new ExtractableCell(0, "0");
        ExtractableCell cell1 = new ExtractableCell(1, "1");
        ExtractableCell cell2 = new ExtractableCell(2, "2");
        ExtractableCell cell3 = new ExtractableCell(3, "3");
        row.addCell(cell);
        row.addCell(cell1);
        row.addCell(cell2);
        row.addCell(cell3);
        ExtractableRow row1 = new ExtractableRow(2);
        row1.addCell(cell);
        row1.addCell(cell1);
        row1.addCell(cell2);
        row1.addCell(cell3);
        System.out.println(row.equals(row1));
        // try {
        //     File file = FileUtilsHelper.getClassPathResource("text/utils/АНКЕТА ПАО РТК v14 с фото_Бахилин Н..xlsx").getFile();
        //     ExcelTokenExtractor.ExcelTable excelTable = new ExcelTokenExtractor().extractTable(file);
        //     Assert.assertEquals("File texts is not equals, expected: ", "текст 1 \n"
        //                                                                 + "текст 2 \n"
        //                                                                 + "текст 3 \n", excelTable);
        // } catch (IOException e) {
        //     throw new RuntimeException(e);
        // }
    }

    @Test
    public void testExcelSupportedFormatsReading() {
        processExcelBaseTest("test_1.xls");
        processExcelBaseTest("test_1.xlsm");
        processExcelBaseTest("test_1.xlsx");
        processExcelBaseTest("test_1.xlt");
        processExcelBaseTest("test_1.xltm");
    }

    private void processExcelBaseTest(String fileName) {
        try {
            File file = FilesUtilsHelper.getClassPathResource("text/utils/" + fileName).getFile();
            String text = FilesUtilsHelper.getFileTextByExtension(file);
            Assert.assertEquals("File texts is not equals, expected: ", "текст 1 \n"
                                                                        + "текст 2 \n"
                                                                        + "текст 3 \n", text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void duplicateExcelFile() {
        File oldFile = new File("");
        File newFile = new File("");
        try {
            Workbook oldWB = new XSSFWorkbook(new FileInputStream(oldFile));
            for (int i = 0; i < oldWB.getNumberOfSheets(); i++) {
                XSSFSheet sheetFromOldWB = (XSSFSheet) oldWB.getSheetAt(i);
                sheetFromOldWB.disableLocking();
            }
            FileOutputStream fileOut = new FileOutputStream(newFile);
            oldWB.write(fileOut);
            oldWB.close();
            fileOut.close();
        } catch (IOException ignore) {
            //ignore
        }
    }
}
