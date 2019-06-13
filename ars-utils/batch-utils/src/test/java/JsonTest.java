import java.io.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import graph.StarVertex;

public class JsonTest {
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testArray() throws Exception {
        String value = "[{\"label\":\"Закупка\",\"labels\":[\"Объект\"],\"lookupKey\":{\"Идентификатор закупки\":\"355399007295860\"},"
         + "\"properties\":{\"Идентификатор закупки\":\"355399007295860\",\"Максимальная цена\":\"79217836996\"},\"edges\":[],\"propertyWriteMode\":\"WRITE_ONCE\"},{\"label\":\"Телефон\",\"labels\":[\"Объект\"],\"lookupKey\":{\"Номер\":\"79217836996\"},\"properties\":{\"Номер\":\"79217836996\"},\"edges\":[],\"propertyWriteMode\":\"WRITE_ONCE\"}]";
        StarVertex[] starVertexes = mapper.readValue(value, StarVertex[].class);
        System.out.println();
    }

    private JsonNode getJsonNode(String string) {
        try {
            return mapper.readTree(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
