package com.example;

import com.alibaba.fastjson2.JSONWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

public class JsonWrite {


    @Test
    public void main() throws Exception {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             JSONWriter jsonWriter = JSONWriter.of()) {
            jsonWriter.startObject();
            jsonWriter.writeString("str");
            jsonWriter.writeColon();
            jsonWriter.writeString("s1");
            jsonWriter.writeComma();
            jsonWriter.writeString("i");
            jsonWriter.writeColon();
            jsonWriter.writeInt32(1);
            jsonWriter.endObject();
            jsonWriter.flushTo(os);
            Assertions.assertEquals("{\"str\":\"s1\",\"i\":1}", os.toString());
        }
    }

}
