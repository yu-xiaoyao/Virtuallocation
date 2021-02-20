package me.yuxiaoyao.virtuallocation.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;

public class JacksonCacheConverter implements CacheConverter<String> {

    private final ObjectMapper objectMapper;

    public JacksonCacheConverter() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KotlinModule kotlinModule = new KotlinModule.Builder().build();
        objectMapper.registerModule(kotlinModule);
    }

    @Override
    public <S> String serialize(S source) {
        try {
            return objectMapper.writeValueAsString(source);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public <C> C deserialize(String target, Class<C> cls) {
        try {
            return objectMapper.readValue(target, cls);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }


}
