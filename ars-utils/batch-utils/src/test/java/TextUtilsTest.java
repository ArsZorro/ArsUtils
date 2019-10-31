import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import utils.FileUtilsHelper;
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
            File file = FileUtilsHelper.getClassPathResource("text/utils/" + fileName).getFile();
            String text = FileUtilsHelper.readFileByExtension(file);
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

    @Test
    public void getAddresses() throws Exception {
        File file = new File("C:\\Users\\User\\Desktop\\TESTS\\Каналы РТКомм за март 2019.csv");
        List<String> lines = FileUtils.readLines(file, "MACCYRILLIC");
        StringBuilder addresses = new StringBuilder();
        for (String line : lines) {
            String[] cells = line.split(";");
            int i = 0;
            for (String cell : cells) {
                if (i == 14) {
                    addresses.append(cell).append("\n");
                }
                i++;
            }
        }
        FileWriter fileWriter = new FileWriter(new File("C:\\Users\\User\\Desktop\\TESTS\\" + System.currentTimeMillis() + ".csv"));
        fileWriter.append(addresses);
        fileWriter.flush();
        System.out.println(addresses);
    }

    @Test
    public void test2() {
        EEEEEE eeeeee = EEEEEE.E_1;
        EEEEEE eeeeee1 = EEEEEE.E_1;
        System.out.println(eeeeee == eeeeee1);
    }

    public enum EEEEEE {
        E_1,
        E_2
    }

    @Test
    public void splitFiles() throws Exception {
        String fullText = getFilesText("C:\\Users\\User\\Desktop\\Архив по работе\\Резюме\\Все резюме\\Новая папка");

        fullText = duplicate(fullText, 2001);

        List<String> strings = splitEqually(fullText, 6042);
        System.out.println(new HashSet<>(strings).size() + " : " + strings.size());

        Long bytesLength = calcBytesLength(strings);
        System.out.println("Middle bytes:" + divide(bytesLength, strings.size()));

        saveToFiles(strings);
    }

    private String duplicate(String fullText, int i) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j< i; j++) {
            stringBuilder.append(fullText).append("\n");
        }
        return stringBuilder.toString();
    }

    private String getFilesText(String folder) throws Exception {
        StringBuilder fullText = new StringBuilder();

        for (File file : getFiles(folder)) {
            String line = getFileText(file);
            fullText.append(line).append("\n");
        }

        return fullText.toString();
    }

    private String getFileText(File file) throws Exception {
        return FileUtilsHelper.readFileByExtension(file);
    }

    private List<File> getFiles(String folder) {
        File file1 = new File(folder);
        if (!file1.exists() && !file1.mkdirs()) {
            throw new RuntimeException();
        }
        return FileUtilsHelper.listFilesForFolder(file1);
    }

    private void saveToFiles(List<String> strings) throws IOException {
        File file = new File("C:\\Users\\User\\Desktop\\TESTS\\OUTPUT\\");
        if (!file.exists() && !file.mkdirs()) {
            throw new RuntimeException();
        }

        int i = 1;
        for (String string : strings) {
            FileWriter fileWriter = new FileWriter(new File(file, i + "_" + System.currentTimeMillis() + ".txt"));
            fileWriter.append(string);
            fileWriter.flush();
            fileWriter.close();
            i++;
        }
    }

    public static List<String> splitEqually(String text, int size) {
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    private static Long calcBytesLength(List<String> requests) {
        Long summaryBytesLength = 0L;
        for (String request : requests) {
            byte[] bytes = request.getBytes();
            summaryBytesLength += bytes.length;
        }
        return summaryBytesLength;
    }

    private static double divide(long first, long second) {
        return (double) first / (double) second;
    }

    @Test
    public void fileSearcher() throws Exception {
        String query = "".toUpperCase();
        String filedPath = "";
        List<File> files = getFiles(filedPath);

        List<File> suitableFiles = new ArrayList<>();
        for (File file : files) {
            String text = getFileText(file);
            if (text.toUpperCase().contains(query)) {
                suitableFiles.add(file);
            }
        }

        System.out.println("Suitable files size:" + suitableFiles.size());
        for (File file : suitableFiles) {
            System.out.println("Name " + file.getAbsolutePath());
        }
    }
}
