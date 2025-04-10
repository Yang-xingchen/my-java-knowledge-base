package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Range;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.geo.Point;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(indexName = "module")
public class BaseModule implements Serializable {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String string;
    @Field(type = FieldType.Text)
    private String text;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime dateTime;
    @Field(type = FieldType.Integer_Range)
    private Range<Integer> range;

    private Point point;
    private List<String> array;
    @Field(type = FieldType.Object)
    private Inner inner;
    @Field(type = FieldType.Nested)
    private List<Inner> inners;

    @CreatedDate
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createTime;
    @CreatedBy
    private String createBy;
    @LastModifiedDate
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime modifiedTime;
    @LastModifiedBy
    private String modifiedBy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inner implements Serializable {
        private String name;
        private String text;
    }

}
