package table.searcher;

import java.util.*;

import table.searcher.model.InnerTableHeader;

public class InnerTableSearchSettings {
    public List<List<InnerTableHeader>> headers;
    public Integer minSuitableHeadersCount;
    public Integer maxSuitableHeadersCount;

    public InnerTableSearchSettings(List<List<InnerTableHeader>> headers) {
        this.headers = headers;
    }


}
