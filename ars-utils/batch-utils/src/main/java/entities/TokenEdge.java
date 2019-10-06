package entities;

import java.util.*;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.jgrapht.graph.DefaultEdge;

public class TokenEdge extends DefaultEdge {
    public Map<String, Object> properties = new HashMap<>();
    public String relationType;
    public Direction direction;

    public TokenEdge() {}

    public TokenEdge(Direction direction) {
        this.direction = direction;
    }

    public TokenEdge(String key, String value) {
        this.properties.put(key, value);
    }

    public TokenEdge(String key, String value, Direction direction) {
        this.properties.put(key, value);
        this.direction = direction;
    }

    public TokenEdge(String relationType) {
        this.relationType = relationType;
    }

    public TokenEdge(String relationType, Direction direction) {
        this.relationType = relationType;
        this.direction = direction;
    }

    public TokenEdge(String key, Object value) {
        this.properties.put(key, value);
    }

    public TokenEdge(Map<String, Object> properties) {
        this.properties.putAll(properties);
    }

    public TokenEdge(TokenEdge edge) {
        this.properties.putAll(edge.properties);
        this.relationType = edge.relationType;
        this.direction = edge.direction;
    }

    public void addProperty(String key, String value) {
        this.properties.put(key, value);
    }

    public void addProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    public void addProperties(Map<String, Object> additionalProperties) {
        this.properties.putAll(additionalProperties);
    }

    public void addToPropertyValue(String key, String value) {
        if (properties.containsKey(key)) {
            String v = properties.get(key).toString();
            properties.put(key, v + ",\n" + value);
        } else {
            this.properties.put(key, value);
        }
    }

    public Object getSourceToken() {
        return super.getSource();
    }

    public Object getTargetToken() {
        return super.getTarget();
    }
}
