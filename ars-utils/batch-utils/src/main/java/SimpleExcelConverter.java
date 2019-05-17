
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import files.FilesUtilsHelper;

public class SimpleExcelConverter {
    public enum TableHeaderPosition {
        TOP,
        BOTTOM
    }

    private static final String DEFAULT_CELL_DELIMITER = " ";
    private static final String DEFAULT_ROW_DELIMITER = "\n";
    private static final Integer DEFAULT_SHEET_PROCESSED_COUNTER = 1;

    public String process(File file, boolean useProfileExcelTableConverters) throws IOException {
        try {
            StringBuilder textResult = new StringBuilder();
            Resource resource = new FileSystemResource(file);
            Workbook workbook = openWorkbook(resource);
            int sheetProcessedCounter = workbook.getNumberOfSheets() == 0 ? 0 : DEFAULT_SHEET_PROCESSED_COUNTER;
            List<ExcelTableConverter> tableConverters = new ArrayList<>();
            if (useProfileExcelTableConverters) {
                tableConverters.addAll(buildDefaultTableConverters());
            }
            for (int i = 0; i < sheetProcessedCounter; i++) {
                textResult.append(parseSheet(workbook.getSheetAt(i), tableConverters));
            }
            return textResult.toString();
        } catch (InvalidFormatException e) {
            return DEFAULT_CELL_DELIMITER;
        }
    }

    private Workbook openWorkbook(Resource resource) throws IOException, InvalidFormatException {
        Workbook workbook = null;
        InputStream workbookStream = resource.getInputStream();

        if (!workbookStream.markSupported()) {
            workbookStream = new PushbackInputStream(new ByteArrayInputStream(FileUtils.readFileToByteArray(resource.getFile())), 8);
        }

        if (POIFSFileSystem.hasPOIFSHeader(workbookStream)) {
            try {
                workbook = new HSSFWorkbook(workbookStream);
            } catch (EncryptedDocumentException e) {
                InputStream secondStream = resource.getInputStream();
                workbook = new XSSFWorkbook(secondStream);
            }
            workbook.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);
            return workbook;
        }

        if (DocumentFactoryHelper.hasOOXMLHeader(workbookStream)) {
            workbook = new XSSFWorkbook(OPCPackage.open(workbookStream));
            workbook.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);
            return workbook;
        }
        throw new IllegalArgumentException("Файл не соотвествует формату OLE2/OOXML, проверте версию Excel");
    }

    private Collection<ExcelTableConverter> buildDefaultTableConverters() {
        List<ExcelTableConverter> tableConverters = new ArrayList<>();
        try {
            tableConverters.add(new ExcelTableConverter(FilesUtilsHelper.readLinesFromResource("converters/addressesDetectionStrings.txt"),
                new ExcelCellsConcatinator("###", null, true, true),
                TableHeaderPosition.BOTTOM,
                2,
                2,
                DEFAULT_CELL_DELIMITER));
            tableConverters.add(new ExcelTableConverter(FilesUtilsHelper.readLinesFromResource("converters/innDetectionStrings.txt"),
                new ExcelCellsConcatinator(", ", ": ", true, true),
                TableHeaderPosition.TOP,
                1,
                2,
                DEFAULT_ROW_DELIMITER));
            return tableConverters;
        } catch (IOException e) {
            return tableConverters;
        }
    }

    private String parseSheet(Sheet sheet, List<ExcelTableConverter> tableConverters) {
        StringBuilder stringBuilder = new StringBuilder();

        if (sheet.getPhysicalNumberOfRows() == 0) {
            return DEFAULT_CELL_DELIMITER;
        }

        ExcelTable excelTable = buildTable(sheet);
        for (int i = 0; i < excelTable.table.size(); i++) {
            ExcelTableConverter currentConverter = getDetectedConverter(excelTable, tableConverters, i);
            if (currentConverter == null || tableConverters.size() == 0) {
                String rowString = excelTable.getFullRowString(i);
                stringBuilder.append(rowString);
                stringBuilder.append(StringUtils.isBlank(rowString) ? "" : DEFAULT_ROW_DELIMITER);
            } else {
                i = currentConverter.convert(stringBuilder, excelTable, i);
                stringBuilder.append(DEFAULT_ROW_DELIMITER);
            }
        }

        return stringBuilder.toString();
    }

    private ExcelTable buildTable(Sheet sheet) {
        ExcelTable excelTable = new ExcelTable();
        int tableRowCounter = 0;
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                ExcelRow excelRow = buildExcelRow(row);
                if (excelRow.pos2CellValue.size() != 0) {
                    excelTable.table.put(tableRowCounter, excelRow);
                    tableRowCounter++;
                }
            }
        }
        return excelTable;
    }

    private ExcelRow buildExcelRow(Row row) {
        ExcelRow excelRow = new ExcelRow();
        excelRow.pos2CellValue = getNotEmptyCellsPositions2CellValues(row);
        excelRow.fullRowString = getRowString(excelRow.pos2CellValue.values());
        excelRow.realExcelFileRowPosition = row.getRowNum();
        return excelRow;
    }

    private Map<Integer, String> getNotEmptyCellsPositions2CellValues(Row row) {
        Map<Integer, String> notEmptyCellsPositions = new HashMap<>();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            String cellValue = getCellValue(cell);
            if (StringUtils.isNotBlank(cellValue)) {
                notEmptyCellsPositions.put(cell.getColumnIndex(), cellValue);
            }
        }
        return notEmptyCellsPositions;
    }

    private static String getCellValue(Cell cell) {
        String resultString;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                resultString = getValueIfNotBlank(cell.getRichStringCellValue().getString());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    resultString = getValueIfNotBlank(new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue()));
                } else {
                    resultString = getValueIfNotBlank(cell.getNumericCellValue() + "");
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                resultString = "";
                break;
            case Cell.CELL_TYPE_ERROR:
                resultString = "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                resultString = "";
                break;
            case Cell.CELL_TYPE_BLANK:
                resultString = "";
                break;
            default:
                resultString = getValueIfNotBlank(cell.getStringCellValue());
                break;
        }
        return resultString;
    }

    private static String getValueIfNotBlank(String value) {
        if (StringUtils.isNotBlank(value)) {
            return value + DEFAULT_CELL_DELIMITER;
        }
        return "";
    }

    private static String getRowString(Collection<String> cells) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String cell : cells) {
            stringBuilder.append(cell).append(DEFAULT_CELL_DELIMITER);
        }
        return stringBuilder.toString();
    }

    private ExcelTableConverter getDetectedConverter(ExcelTable excelTable,
                                                     List<ExcelTableConverter> tableConverters,
                                                     Integer rowNumber) {
        for (ExcelTableConverter converter : tableConverters) {
            if (!converter.convert(new StringBuilder(), excelTable, rowNumber).equals(rowNumber)) {
                return converter;
            }
        }
        return null;
    }

    public class ExcelTable {
        Map<Integer, ExcelRow> table = new HashMap<>();

        public Map<Integer, String> getPos2CellValue(Integer positon) {
            return table.get(positon).pos2CellValue;
        }

        public String getFullRowString(Integer positon) {
            return table.get(positon).fullRowString;
        }
    }

    public class ExcelRow {
        Map<Integer, String> pos2CellValue = new HashMap<>();
        String fullRowString;
        Integer realExcelFileRowPosition;
    }

    public class ExcelTableConverter {
        public List<String> startConvertDetectors;
        public ExcelCellsConcatinator excelCellsConcatinator;
        public TableHeaderPosition tableHeaderPosition;
        public Integer repeatConvertationCounter;
        public Integer tableRowsCounter;
        public String rowsDelimiter;

        public ExcelTableConverter(List<String> startConvertDetectors,
                                   ExcelCellsConcatinator excelCellsConcatinator,
                                   TableHeaderPosition tableHeaderPosition,
                                   Integer repeatConvertationCounter,
                                   Integer tableRowsCounter,
                                   String rowsDelimiter) {
            this.startConvertDetectors = startConvertDetectors;
            this.excelCellsConcatinator = excelCellsConcatinator;
            this.tableHeaderPosition = tableHeaderPosition;
            this.repeatConvertationCounter = repeatConvertationCounter;
            this.tableRowsCounter = tableRowsCounter;
            this.rowsDelimiter = rowsDelimiter;
        }

        public Integer convert(StringBuilder stringBuilder, ExcelTable excelTable, Integer outComeStartRowPosition) {
            Integer startRowPosition = outComeStartRowPosition;
            String rowPreview = excelTable.getFullRowString(startRowPosition);
            if (needToStartConvert(rowPreview)) {
                stringBuilder.append(rowPreview).append(DEFAULT_ROW_DELIMITER);
                startRowPosition += 1;
                for (int i = 0; i < repeatConvertationCounter; i++) {
                    Integer endRowPosition = startRowPosition + tableRowsCounter - 1;
                    stringBuilder.append(convertTableToString(excelTable, startRowPosition, endRowPosition));
                    startRowPosition = endRowPosition + 1;
                }
                return startRowPosition;
            } else {
                stringBuilder.append(rowPreview);
                return startRowPosition;
            }
        }

        private String convertTableToString(ExcelTable excelTable, int startRowPosition, int endRowPosition) {
            if (tableHeaderPosition == TableHeaderPosition.TOP) {
                return convertTableBodyToString(excelTable, startRowPosition + 1, endRowPosition, excelTable.getPos2CellValue(startRowPosition));
            } else {
                return convertTableBodyToString(excelTable, startRowPosition, endRowPosition - 1, excelTable.getPos2CellValue(endRowPosition));
            }
        }

        private String convertTableBodyToString(ExcelTable excelTable,
                                                int statRowPosition,
                                                int endRowPosition,
                                                Map<Integer, String> headers) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = statRowPosition; i <= endRowPosition; i++) {
                stringBuilder.append(excelCellsConcatinator.concat(excelTable.getPos2CellValue(i), headers)).append(rowsDelimiter);
            }
            return stringBuilder.toString();
        }

        private boolean needToStartConvert(String value) {
            for (String detector : startConvertDetectors) {
                if (value.toUpperCase().contains(detector.toUpperCase())) {
                    return true;
                }
            }
            return false;
        }
    }

    public class ExcelCellsConcatinator {
        private static final String DEFAULT_CELL_DELIMITER = ", ";
        public String betweenConnectedGroupDelimiter;
        public String inGroupCellsDelimiter;
        public boolean addHeadersFirst;
        public boolean useEmptyCellsValues;

        public ExcelCellsConcatinator(String betweenConnectedGroupDelimiter, String inGroupCellsDelimiter, boolean addHeadersFirst, boolean
            useEmptyCellsValues) {
            this.betweenConnectedGroupDelimiter = betweenConnectedGroupDelimiter
                                                  == null ? DEFAULT_CELL_DELIMITER : betweenConnectedGroupDelimiter;
            this.inGroupCellsDelimiter = inGroupCellsDelimiter == null ? SimpleExcelConverter.DEFAULT_CELL_DELIMITER : inGroupCellsDelimiter;
            this.addHeadersFirst = addHeadersFirst;
            this.useEmptyCellsValues = useEmptyCellsValues;
        }

        public String concat(Map<Integer, String> row, Map<Integer, String> headers) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<Integer, String> entry : headers.entrySet()) {
                String headerValue = entry.getValue();
                String cellValue = row.getOrDefault(entry.getKey(), "");
                if (addHeadersFirst) {
                    stringBuilder.append(concat(headerValue, cellValue));
                } else {
                    stringBuilder.append(concat(cellValue, headerValue));
                }
                stringBuilder.append(betweenConnectedGroupDelimiter);
            }
            return stringBuilder.toString();
        }

        private String concat(String header, String cell) {
            boolean concat = (!useEmptyCellsValues && (StringUtils.isBlank(header) || StringUtils.isBlank(cell)));
            if (!concat) {
                return header + inGroupCellsDelimiter + cell;
            }
            return "";
        }
    }

    // private Map<Integer, Map<Integer, String>> restructureRowNumber2RowToColumnNumber2Column(Map<Integer, Map<Integer, String>> rowNumber2RowTable) {
    //     Map<Integer, Map<Integer, String>> columnNumber2ColumnTable = new HashMap<>();
    //     for (Map.Entry<Integer, Map<Integer, String>> rowNumber2Row : rowNumber2RowTable.entrySet()) {
    //         Map<Integer, String> row = rowNumber2Row.getValue();
    //         for (Map.Entry<Integer, String> rowEntry : row.entrySet()) {
    //             Map<Integer, String> column = columnNumber2ColumnTable.computeIfAbsent(rowEntry.getKey(), (k) -> new HashMap<>());
    //             column.put(rowNumber2Row.getKey(), rowEntry.getValue());
    //         }
    //     }
    //     return columnNumber2ColumnTable;
    // }
}
