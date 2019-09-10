package table.searcher.model;

import java.util.*;


public class InnerTableHeader {
    public List<String> nearMarkers;
    public String value;
    public Integer firstColumn;
    public Integer lastColumn;
    public String nodeMapping;

    public InnerTableHeader(String value, String nodeMapping) {
        this.value = value;
        this.nodeMapping = nodeMapping;
    }
}
