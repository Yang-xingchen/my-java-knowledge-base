package com.example.seata.consumer;

public interface TestService {

    Long create();

    void commit(Long aId, Long bId);

    void rollback(Long aId, Long bId);

    void check(String message, Long aId, Integer aVal, Long bId, Integer bVal);

}
