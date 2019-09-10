package table.searcher;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import table.model.ExtractableCell;
import table.model.ExtractableRow;
import table.model.ExtractableTable;
import table.searcher.model.InnerTableHeader;

public class InnerTableSearcher {
    public ExtractableTable getInnerTable(ExtractableTable originalTable, InnerTableSearchSettings searchSettings) {
        for (ExtractableRow extractableRow : originalTable.getTable()) {
            if (tableIsFound(originalTable, extractableRow, searchSettings)) {

            }
        }
        return null;
    }

    private boolean tableIsFound(ExtractableTable origin,
                                 ExtractableRow extractableRow,
                                 InnerTableSearchSettings searchSettings) {
        List<ExtractableCell> notEmptyCells = notEmptyCells(extractableRow);
        if (isOutOfRange(notEmptyCells.size(), searchSettings.minSuitableHeadersCount, searchSettings.maxSuitableHeadersCount)) {
            return false;
        }

        List<Set<String>> notEmptyCellsValues = getNotEmptyCellsValues(origin, extractableRow, searchSettings.headers.size() - 1);
        if (CollectionUtils.isEmpty(notEmptyCellsValues) || allHeadersIsMatch(notEmptyCellsValues, searchSettings.headers)) {
            return false;
        }

        return true;
    }

    private static List<ExtractableCell> notEmptyCells(ExtractableRow extractableRow) {
        List<ExtractableCell> notEmptyCells = new ArrayList<>();
        for (ExtractableCell cell : extractableRow.saveOrderCells) {
            if (StringUtils.isNotBlank(cell.getCellValue())) {
                notEmptyCells.add(cell);
            }
        }
        return notEmptyCells;
    }

    private static boolean isOutOfRange(Integer value, Integer minValue, Integer maxValue) {
        return minValue != null
               && maxValue != null
               && value != null
               && (value < minValue
                   || value > maxValue);
    }

    private List<Set<String>> getNotEmptyCellsValues(ExtractableTable origin,
                                                     ExtractableRow extractableRow,
                                                     Integer neededRowsSize) {
        List<Set<String>> rowsValues = new ArrayList<>();
        rowsValues.add(getNotEmptyCellsValues(extractableRow));
        getRowsAfterStartRow(origin, extractableRow, neededRowsSize).forEach(e -> rowsValues.add(getNotEmptyCellsValues(e)));
        return rowsValues;
    }

    private Set<String> getNotEmptyCellsValues(ExtractableRow extractableRow) {
        return notEmptyCells(extractableRow).stream().map(c -> c.getCellValue().toLowerCase()).collect(Collectors.toSet());
    }

    private List<ExtractableRow> getRowsAfterStartRow(ExtractableTable origin, ExtractableRow startRow, Integer neededRowsSize) {
        Map<Integer, ExtractableRow> tableMap = origin.getTableMap();
        if (origin.maxRowPosition == null || !tableMap.containsKey(startRow.rowPosition)) {
            return Collections.emptyList();
        }

        List<ExtractableRow> resultRows = new ArrayList<>();
        ExtractableRow lastRow = null;
        for (int i = startRow.rowPosition; i < origin.maxRowPosition; i++) {
            if (resultRows.size() >= neededRowsSize) {
                break;
            }

            if (tableMap.containsKey(i)) {
                ExtractableRow currentRow = tableMap.get(i);
                if (lastRow != null && !lastRow.equals(currentRow)) {
                    resultRows.add(currentRow);
                }
                lastRow = currentRow;
            }
        }
        return resultRows;
    }

    private static boolean allHeadersIsMatch(List<Set<String>> notEmptyCellsValues, List<List<InnerTableHeader>> headers) {
        if (notEmptyCellsValues.size() != headers.size()) {
            return false;
        }

        for (int i = 0; i < notEmptyCellsValues.size(); i++) {
            Set<String> requiredValues = headers.get(i).stream().map(h -> h.value).collect(Collectors.toSet());
            if (!notEmptyCellsValues.get(i).containsAll(requiredValues)) {
                return false;
            }
        }

        return true;
    }
}
