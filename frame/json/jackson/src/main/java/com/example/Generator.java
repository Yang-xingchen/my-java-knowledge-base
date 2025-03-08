package com.example;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class Generator {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void main() throws Exception {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            JsonGenerator generator = MAPPER.createGenerator(os, JsonEncoding.UTF8);
            generator.writeStartObject();
            generator.writeFieldName("str");
            generator.writeString("s1");
            generator.writeNumberField("i", 1);
            generator.writeEndObject();
            generator.flush();
            Assertions.assertEquals("{\"str\":\"s1\",\"i\":1}", os.toString());
        }
    }

}
