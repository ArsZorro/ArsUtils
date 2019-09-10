package table.model;

import java.util.*;

public class ExtractableRow {
    public Integer rowPosition;
    public Map<Integer, ExtractableCell> pos2CellValue = new HashMap<>();
    public List<ExtractableCell> saveOrderCells = new ArrayList<>();
    private Integer cellPositionCounter = 0;

    public ExtractableRow(Integer rowPosition) {
        this.rowPosition = rowPosition;
    }

    public void addCell(ExtractableCell cell) {
        pos2CellValue.put(cellPositionCounter, cell);
        saveOrderCells.add(cell);
        cell.cellPosition = cellPositionCounter;
        cellPositionCounter++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExtractableRow that = (ExtractableRow) o;
        return Objects.equals(saveOrderCells, that.saveOrderCells);
    }

    @Override
    public int hashCode() {

        return Objects.hash(saveOrderCells);
    }
}
