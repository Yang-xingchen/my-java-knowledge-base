package com.example.seata.consumer.tcc;

import com.example.seata.consumer.TestException;
import com.example.seata.consumer.tcc.action.AddAction;
import com.example.seata.consumer.tcc.action.CreateAction;
import com.example.seata.server.Server;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TccTestServiceImpl implements TccTestService{

    private static final Logger log = LoggerFactory.getLogger(TccTestServiceImpl.class);

    @DubboReference(group = "dubbo")
    private Server server;
    @Autowired
    public AddAction addAction;
    @Autowired
    public CreateAction createAction;

    @Override
    @GlobalTransactional
    public Long commit() {
        Long id = createAction.prepare(null, 0);
        addAction.prepare(null, id, 1, false);
        addAction.prepare(null, id, 1, false);
        return id;
    }

    @Override
    @GlobalTransactional
    public Long rollback() {
        Long id = createAction.prepare(null, 0);
        try {
            addAction.prepare(null, id, 1, false);
            addAction.prepare(null, id, 1, true);
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
