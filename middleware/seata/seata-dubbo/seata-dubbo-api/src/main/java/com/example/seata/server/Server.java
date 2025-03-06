package com.example.seata.server;

public interface Server {

    String NONE = "master";
    String AT = "at";
    String XA = "xa";

    Long create(int init);

    void add(Long id, int i, boolean err);

    Integer get(Long id);

    void delete(Long id);

}
