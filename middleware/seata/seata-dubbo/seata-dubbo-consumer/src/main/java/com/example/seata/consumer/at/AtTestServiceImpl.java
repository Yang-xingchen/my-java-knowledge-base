package com.example.seata.consumer.at;

import com.example.seata.consumer.AbstractTestServiceImpl;
import com.example.seata.server.Server;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.seata.core.context.RootContext;
import org.apache.seata.core.model.BranchType;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.apache.seata.tm.api.GlobalTransaction;
import org.apache.seata.tm.api.GlobalTransactionContext;
import org.apache.seata.tm.api.TransactionalTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AtTestServiceImpl extends AbstractTestServiceImpl implements AtTestService {

    private static final Logger log = LoggerFactory.getLogger(AtTestServiceImpl.class);

    @DubboReference(group = Server.AT)
    private Server server;

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    @GlobalTransactional
    public void commit(Long aId, Long bId) {
        RootContext.bindBranchType(BranchType.AT);
        log.info("at commit XID: {}", RootContext.getXID());
        server.add(aId, 1, false);
        server.add(bId, 1, false);
    }

    @Override
    @GlobalTransactional
    public void rollback(Long aId, Long bId) {
        RootContext.bindBranchType(BranchType.AT);
        log.info("at rollback XID: {}", RootContext.getXID());
        server.add(aId, 1, false);
        server.add(bId, 1, true);
    }

}
