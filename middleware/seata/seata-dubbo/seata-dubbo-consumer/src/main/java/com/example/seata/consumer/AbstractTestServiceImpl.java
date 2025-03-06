package com.example.seata.consumer;

import com.example.seata.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class AbstractTestServiceImpl implements TestService {

    private static final Logger log = LoggerFactory.getLogger(AbstractTestServiceImpl.class);

    protected abstract Server getServer();

    @Override
    public Long create() {
        return getServer().create(0);
    }

    @Override
    public void check(String message, Long aId, Integer aVal, Long bId, Integer bVal) {
        Integer a = getServer().get(aId);
        Integer b = getServer().get(bId);
        if (Objects.equals(a, aVal) && Objects.equals(b, bVal)) {
            log.info("{}: a[{}]: {} == {}, b[{}]: {} == {}", message, aId, a, aVal, bId, b, bVal);
        } else {
            log.error("{}: a[{}]: {} != {}, b[{}]: {} != {}", message, aId, a, aVal, bId, b, bVal);
        }
    }

}
