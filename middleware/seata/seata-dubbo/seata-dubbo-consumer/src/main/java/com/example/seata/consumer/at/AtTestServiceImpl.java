package com.example.seata.consumer.at;

import com.example.seata.consumer.TestException;
import com.example.seata.server.Server;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.seata.core.context.RootContext;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AtTestServiceImpl implements AtTestService {

    private static final Logger log = LoggerFactory.getLogger(AtTestServiceImpl.class);

    @DubboReference(group = "dubbo")
    private Server server;

    @Override
    @GlobalTransactional
    public Long commit() {
        log.info("XID: {}", RootContext.getXID());
        Long id = server.create(0);
        server.add(id, 1, false);
        server.add(id, 1, false);
        return id;
    }

    @Override
    @GlobalTransactional
    public Long rollback() {
        log.info("XID: {}", RootContext.getXID());
        Long id = server.create(0);
        try {
            server.add(id, 1, false);
            server.add(id, 1, true);
            return id;
        } catch (Exception e) {
            throw new TestException(e, id);
        }
    }

    @Override
    public void check(String message, Long id, Integer val) {
        Integer i = server.get(id);
        if (Objects.equals(i, val)) {
            log.info("{}[{}]: {} == {}", message, id, i, val);
        } else {
            log.error("{}[{}]: {} != {}", message, id, i, val);
        }
    }

}
