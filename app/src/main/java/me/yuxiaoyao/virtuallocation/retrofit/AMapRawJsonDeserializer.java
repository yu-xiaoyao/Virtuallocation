package me.yuxiaoyao.virtuallocation.retrofit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AMapRawJsonDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        if (node.isArray()) {
            Iterator<JsonNode> iterator = node.iterator();
            List<String> list = new ArrayList<>();
            while (iterator.hasNext()) {
                String s = iterator.next().textValue();
                list.add(s);
            }
            return String.join(",", list);
        } else if (node.isObject()) {
            return mapper.writeValueAsString(node);
        }
        // just test value
        return node.textValue();
    }
}
