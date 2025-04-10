package com.example;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@Document(indexName = "agg_module")
public class AggModule implements Serializable {

    @Id
    public String id;

    @Field(type = FieldType.Keyword)
    public String key;
    public Integer value;

}
