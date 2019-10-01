package table.mapper;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import table.ExcelUtils;
import table.model.ExtractableCell;
import table.model.ExtractableRow;
import table.model.ExtractableTable;

public class ExcelMapper implements ExtractableTableMapper {
    private static final String DEFAULT_CELL_POSITION_DELIMITER = ":";
    private static final Integer NOT_MERGED_CELLS_ID = -1;
    private final Logger logger = LogManager.getLogger(ExcelMapper.class);

    @Override
    public List<ExtractableTable> createExtractableTables(File excelFile) {
        List<ExtractableTable> extractableTables = new ArrayList<>();
        try {
            List<Sheet> sheets = ExcelUtils.getAllSheets(excelFile);

            for (Sheet sheet : sheets) {
                if (sheet.getPhysicalNumberOfRows() == 0) {
                    continue;
                }

                Map<String, ExtractableCell> positions2MergedCells = getMergedCells(sheet);
                ExtractableTable excelTable = createExtractableTable(sheet, positions2MergedCells);
                extractableTables.add(excelTable);
            }
        } catch (IOException e) {
            logger.error(e);
        }

        return extractableTables;
    }

    private ExtractableTable createExtractableTable(Sheet sheet, Map<String, ExtractableCell> positions2MergedCells) {
        ExtractableTable excelTable = new ExtractableTable();
        for (Row row : ExcelUtils.getRows(sheet)) {
            List<Cell> cells = ExcelUtils.getCells(row);
            ExtractableRow excelRow = compressMergedCellColumnsIntoOne(row.getRowNum(), cells, positions2MergedCells);
            excelTable.putRow(excelRow);
        }
        return excelTable;
    }

    private static Map<String, ExtractableCell> getMergedCells(Sheet sheet) {
        Map<String, ExtractableCell> cellPosition2Cells = new HashMap<>();
        Integer idCounter = 0;
        for (CellRangeAddress rangeAddress : sheet.getMergedRegions()) {
            ExtractableCell cell = new ExtractableCell(idCounter,
                rangeAddress.getFirstRow(),
                rangeAddress.getLastRow(),
                rangeAddress.getFirstColumn(),
                rangeAddress.getLastColumn());

            for (int i = rangeAddress.getFirstRow(); i <= rangeAddress.getLastRow(); i++) {
                for (int j = rangeAddress.getFirstColumn(); j <= rangeAddress.getLastColumn(); j++) {
                    String cellPosition = buildCellPosition(i, j);
                    cellPosition2Cells.put(cellPosition, cell);
                }
            }
            idCounter++;
        }
        return cellPosition2Cells;
    }

    private static ExtractableRow compressMergedCellColumnsIntoOne(Integer rowNum,
                                                                   List<Cell> rowCells,
                                                                   Map<String, ExtractableCell> positions2MergedCells) {
        ExtractableRow resultRow = new ExtractableRow(rowNum);
        ExtractableCell lastCell = null;

        for (Cell cell : rowCells) {
            String position = buildCellPosition(cell.getAddress().getRow(), cell.getAddress().getColumn());
            if (positions2MergedCells.containsKey(position)) {
                ExtractableCell currentCell = positions2MergedCells.get(position);
                if (lastCell == null || !lastCell.id.equals(currentCell.id)) {
                    resultRow.addCell(currentCell);
                }
                currentCell.appendCellValue(ExcelUtils.asText(cell));
                lastCell = currentCell;
            } else {
                resultRow.addCell(new ExtractableCell(NOT_MERGED_CELLS_ID, ExcelUtils.asText(cell)));
            }
        }
        return resultRow;
    }

    private static String buildCellPosition(Integer rowNumber, Integer cellNumber) {
        return rowNumber + DEFAULT_CELL_POSITION_DELIMITER + cellNumber;
    }
}
