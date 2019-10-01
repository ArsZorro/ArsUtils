package table.mapper;

import java.io.*;
import java.util.*;

import table.model.ExtractableTable;

public interface ExtractableTableMapper {
    List<ExtractableTable> createExtractableTables(File file);
}
