package com.example.seata.consumer.tcc;

import com.example.seata.consumer.AbstractTestServiceImpl;
import com.example.seata.consumer.tcc.action.AddAction;
import com.example.seata.consumer.tcc.action.CreateAction;
import com.example.seata.server.Server;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.seata.core.context.RootContext;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TccTestServiceImpl extends AbstractTestServiceImpl implements TccTestService{

    private static final Logger log = LoggerFactory.getLogger(TccTestServiceImpl.class);

    @DubboReference(group = Server.NONE)
    private Server server;
    @Autowired
    public AddAction addAction;
    @Autowired
    public CreateAction createAction;

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    @GlobalTransactional
    public void commit(Long aId, Long bId) {
        log.info("tcc commit XID: {}", RootContext.getXID());
        addAction.prepare(null, aId, 1, false);
        addAction.prepare(null, bId, 1, false);
    }

    @Override
    @GlobalTransactional
    public void rollback(Long aId, Long bId) {
        log.info("tcc rollback XID: {}", RootContext.getXID());
        addAction.prepare(null, aId, 1, false);
        addAction.prepare(null, bId, 1, true);
    }

}
