package com.example.seata.consumer.xa;

import com.example.seata.consumer.AbstractTestServiceImpl;
import com.example.seata.server.Server;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.seata.core.context.RootContext;
import org.apache.seata.core.model.BranchType;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class XaTestServiceImpl extends AbstractTestServiceImpl implements XaTestService {

    private static final Logger log = LoggerFactory.getLogger(XaTestServiceImpl.class);

    @DubboReference(group = Server.XA)
    private Server server;

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    @GlobalTransactional
    public void commit(Long aId, Long bId) {
        RootContext.bindBranchType(BranchType.XA);
        log.info("xa commit XID: {}", RootContext.getXID());
        server.add(aId, 1, false);
        server.add(bId, 1, false);
        System.out.println();
    }

    @Override
    @GlobalTransactional
    public void rollback(Long aId, Long bId) {
        RootContext.bindBranchType(BranchType.XA);
        log.info("xa rollback XID: {}", RootContext.getXID());
        server.add(aId, 1, false);
        server.add(bId, 1, true);
    }

}
