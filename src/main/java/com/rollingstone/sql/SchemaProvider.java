package com.rollingstone.sql;



import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.io.InputStream;

@Component
public class SchemaProvider {
    @Value("classpath:db/schema_new.sql") private Resource schemaFile;
    private final AtomicReference<String> cache = new AtomicReference<>();

    public String schemaText() {
        String memo = cache.get();
        if (memo != null) return memo;
        try (InputStream in = schemaFile.getInputStream()) {
            String txt = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            cache.set(txt);
            return txt;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load schema_new.sql", e);
        }
    }
}

