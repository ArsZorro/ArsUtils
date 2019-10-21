package utils;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class ExcelUtils {
    protected ExcelUtils() {}

    private static final String DEFAULT_SHEET_DELIMITER = "\n\n";
    private static final String DEFAULT_ROW_DELIMITER = "\n";
    private static final String DEFAULT_CELL_DELIMITER = " ";

    private static final String DEFAULT_EMPTY_SHEET_VALUE = "";
    private static final String DEFAULT_EMPTY_CELL_VALUE = "";

    private static final Integer DEFAULT_START_BYTES_SIZE = 8;

    private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String getText(File excelFile) throws IOException {
        Workbook workbook = getWorkbook(excelFile);
        List<Sheet> sheets = getAllSheets(workbook);

        StringBuilder textResult = new StringBuilder();
        for (Sheet sheet : sheets) {
            textResult.append(asText(sheet)).append(DEFAULT_SHEET_DELIMITER);
        }
        return textResult.toString();
    }

    public static Workbook getWorkbook(File excelFile) throws IOException {
        try {
            Resource resource = new FileSystemResource(excelFile);
            return openWorkbook(resource);
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
    }

    public static List<byte[]> getPictures(Workbook workbook) {
        List<? extends PictureData> allPictures = workbook.getAllPictures();
        return allPictures.stream().map(p -> ((PictureData) p).getData()).collect(Collectors.toList());
    }

    public static List<Sheet> getAllSheets(Workbook workbook) throws IOException {
        List<Sheet> sheets = new ArrayList<>();
        try {
            int sheetProcessedCounter = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetProcessedCounter; i++) {
                sheets.add(workbook.getSheetAt(i));
            }
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
        return sheets;
    }

    private static Workbook openWorkbook(Resource resource) throws IOException {
        InputStream workbookStream = resource.getInputStream();

        if (!workbookStream.markSupported()) {
            workbookStream =
                new PushbackInputStream(new ByteArrayInputStream(FileUtils.readFileToByteArray(resource.getFile())), DEFAULT_START_BYTES_SIZE);
        }

        try {
            Workbook workbook = WorkbookFactory.create(workbookStream);
            workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            return workbook;
        } catch (POIXMLException | IOException | EncryptedDocumentException e) {
            throw new IOException("Файл не соотвествует формату OLE2/OOXML, проверте версию Excel\n" + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    private static String asText(Sheet sheet) {
        if (sheet.getPhysicalNumberOfRows() == 0) {
            return DEFAULT_EMPTY_SHEET_VALUE;
        }

        StringBuilder sheetStringBuilder = new StringBuilder();
        for (Row row : getRows(sheet)) {
            sheetStringBuilder.append(asText(row)).append(DEFAULT_ROW_DELIMITER);
        }

        return sheetStringBuilder.toString();
    }

    private static String asText(Row row) {
        StringBuilder rowStringBuilder = new StringBuilder();
        for (Cell cell : getCells(row)) {
            String cellValue = asText(cell);
            if (StringUtils.isNotBlank(cellValue)) {
                rowStringBuilder.append(cellValue).append(DEFAULT_CELL_DELIMITER);
            }
        }
        return rowStringBuilder.toString();
    }

    public static String asText(Cell cell) {
        String resultString = null;
        switch (cell.getCellType()) {
            case STRING:
                resultString = getValueIfNotBlank(cell.getRichStringCellValue().getString());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    resultString = getValueIfNotBlank(DEFAULT_DATE_FORMAT.format(cell.getDateCellValue()));
                } else {
                    resultString = getLongValue(getValueIfNotBlank(cell.getNumericCellValue() + ""));
                }
                break;
            case BOOLEAN:
            case ERROR:
            case FORMULA:
                CellType cachedFormulaResultType = cell.getCachedFormulaResultType();
                if (cachedFormulaResultType == CellType.NUMERIC) {
                    BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
                    resultString = bd.stripTrailingZeros().toPlainString();
                } else {
                    if (cachedFormulaResultType == CellType.STRING) {
                        resultString = cell.getRichStringCellValue().getString();
                    }
                }
                break;
            case BLANK:
                resultString = DEFAULT_EMPTY_CELL_VALUE;
                break;
            default:
                resultString = getValueIfNotBlank(cell.getStringCellValue());
                break;
        }
        return TextUtils.normalizeInnerDelimiter(resultString).trim();
    }

    private static String getValueIfNotBlank(String value) {
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return DEFAULT_EMPTY_CELL_VALUE;
    }

    private static String getLongValue(String value) {
        try {
            if (value.contains(".")) {
                return new Double(Double.parseDouble(value)).longValue() + "";
            }
        } catch (NumberFormatException e) {
            System.out.println();
            //ignore cause
        }
        return value;
    }

    public static List<Row> getRows(Sheet sheet) {
        List<Row> rows = new LinkedList<>();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                rows.add(row);
            }
        }
        return rows;
    }

    public static List<Cell> getCells(Row row) {
        List<Cell> cells = new ArrayList<>();
        for (int j = 0; j < row.getLastCellNum(); j++) {
            Cell cell = row.getCell(j);
            cells.add(cell);
        }
        return cells;
    }
}
