package table.model;

import java.util.*;
import java.util.stream.Collectors;

public class ExtractableTable {
    public static final String DEFAULT_CELL_DELIMITER = " ";
    public Integer maxRowPosition;
    private List<ExtractableRow> table = new ArrayList<>();
    private Map<Integer, ExtractableRow> tableMap = new HashMap<>();

    public String getFullRowString(Integer position) {
        return table.get(position).pos2CellValue.entrySet()
                                                .stream()
                                                .map(e -> e.getValue().getCellValue())
                                                .collect(Collectors.joining(DEFAULT_CELL_DELIMITER));
    }

    public void putRow(ExtractableRow excelRow) {
        this.table.add(excelRow);
        this.tableMap.put(excelRow.rowPosition, excelRow);
        if (maxRowPosition == null || maxRowPosition < excelRow.rowPosition) {
            maxRowPosition = excelRow.rowPosition;
        }
    }

    public List<ExtractableRow> getTable() {
        return table;
    }

    public Map<Integer, ExtractableRow> getTableMap() {
        return tableMap;
    }
}
