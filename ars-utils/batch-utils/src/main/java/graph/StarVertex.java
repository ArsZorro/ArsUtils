package graph;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StarVertex {
    private static ObjectMapper mapper = new ObjectMapper();
    public String label;
    public List<String> labels;
    public Map<String, Object> lookupKey = new HashMap<>();
    public Map<String, Object> properties = new HashMap<>();
    public List<AdjacentEdge> edges = new ArrayList<>();
    public PropertyWriteMode propertyWriteMode = PropertyWriteMode.WRITE_ONCE;

    public static List<StarVertex> toFlat(List<StarVertex> starVertices) {
        List<StarVertex> flatVertices = new ArrayList<>();
        for (StarVertex vertex : starVertices) {
            if (vertex.edges.isEmpty()) {
                flatVertices.add(vertex);
                continue;
            }
            boolean isPropertiesNotWritten = true;
            for (AdjacentEdge edge : vertex.edges) {
                StarVertex adjacentVertex = new StarVertex();
                adjacentVertex.label = vertex.label;
                adjacentVertex.labels = vertex.labels;
                adjacentVertex.lookupKey.putAll(vertex.lookupKey);
                if (isPropertiesNotWritten) {
                    adjacentVertex.properties.putAll(vertex.properties);
                    isPropertiesNotWritten = false;
                }
                adjacentVertex.edges.add(edge);
                flatVertices.add(adjacentVertex);
            }
        }
        return flatVertices;
    }

    public String toJsonString() {
        ObjectNode json = mapper.createObjectNode();
        for (Map.Entry<String, Object> entry : lookupKey.entrySet()) {
            json.putPOJO(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            json.put(entry.getKey(), entry.getValue().toString());
        }
        for (AdjacentEdge edge : edges) {
            for (Map.Entry<String, Object> entry : edge.adjacentLookupKey.entrySet()) {
                json.putPOJO(edge.edgeLabel + ":" + entry.getKey(), entry.getValue());
            }
        }
        ObjectNode outerJson = mapper.createObjectNode();
        outerJson.set(label, json);
        try {
            return mapper.writeValueAsString(outerJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
