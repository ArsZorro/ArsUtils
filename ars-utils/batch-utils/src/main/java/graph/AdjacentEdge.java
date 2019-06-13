package graph;

import java.util.*;

public class AdjacentEdge {
    public String adjacentLabel;
    public Map<String, Object> adjacentLookupKey = new HashMap<>();
    public String edgeLabel;
    public Map<String, Object> edgeLookupKey = new HashMap<>();
    public Map<String, Object> edgeProperties = new HashMap<>();
    public PropertyWriteMode propertyWriteMode = PropertyWriteMode.WRITE_ONCE;

    // both direction non available
    public boolean direction;

    public AdjacentEdge() {
    }

    public AdjacentEdge(StarVertex vertex) {
        this.adjacentLabel = vertex.label;
        this.adjacentLookupKey = vertex.lookupKey;
    }
}
