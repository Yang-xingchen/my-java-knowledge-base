package com.example;

import com.alibaba.fastjson.JSONWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class JsonWrite {


    @Test
    public void main() throws Exception {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             Writer writer = new OutputStreamWriter(os);
             JSONWriter jsonWriter = new JSONWriter(writer)) {
            jsonWriter.startObject();
            jsonWriter.writeKey("str");
            jsonWriter.writeValue("s1");
            jsonWriter.writeKey("i");
            jsonWriter.writeValue(1);
            jsonWriter.endObject();
            jsonWriter.flush();
            Assertions.assertEquals("{\"str\":\"s1\",\"i\":1}", os.toString());
        }
    }

}
