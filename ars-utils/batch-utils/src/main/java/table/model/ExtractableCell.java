package table.model;

import java.util.*;

import org.apache.commons.lang3.StringUtils;

public class ExtractableCell {
    public Integer id;
    public Integer firstRow;
    public Integer lastRow;
    public Integer firstColumn;
    public Integer lastColumn;
    public Integer cellPosition;
    private StringBuilder fullCellString = new StringBuilder();

    public ExtractableCell(Integer id, Integer firstRow, Integer lastRow, Integer firstColumn, Integer lastColumn) {
        this.id = id;
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.firstColumn = firstColumn;
        this.lastColumn = lastColumn;
    }

    public ExtractableCell(Integer id, String cellValue) {
        this.id = id;
        this.fullCellString.append(cellValue);
    }

    public void appendCellValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            this.fullCellString.append(value).append(ExtractableTable.DEFAULT_CELL_DELIMITER);
        }
    }

    public String getCellValue() {
        return fullCellString.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExtractableCell cell = (ExtractableCell) o;
        return Objects.equals(id, cell.id)
               && Objects.equals(cellPosition, cell.cellPosition)
               && Objects.equals(fullCellString.toString(), cell.fullCellString.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cellPosition, fullCellString.toString());
    }

    @Override
    public String toString() {
        return "id=" + id
               + " cellValue=" + fullCellString
               + " firstRow=" + firstRow
               + " lastRow=" + lastRow
               + " firstColumn=" + firstColumn
               + " lastColumn=" + lastColumn
               + " cellPosition=" + cellPosition;
    }
}
