package com.example.seata.consumer.tcc.action;

import com.example.seata.server.Server;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.seata.core.context.RootContext;
import org.apache.seata.core.model.BranchType;
import org.apache.seata.rm.tcc.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@LocalTCC
@Service
public class CreateActionImpl implements CreateAction {

    @DubboReference(group = Server.NONE)
    private Server server;
    private static final Logger log = LoggerFactory.getLogger(CreateAction.class);

    @Override
    @TwoPhaseBusinessAction(name = "create",
            commitArgsClasses = {BusinessActionContext.class, Long.class},
            rollbackArgsClasses = {BusinessActionContext.class, Long.class})
    public Long prepare(BusinessActionContext context, @BusinessActionContextParameter("init") int init) {
        log.info("tcc create prepare XID: {}", context.getXid());
        Long id = server.create(init);
        BusinessActionContextUtil.addContext("id", id);
        return id;
    }

    @Override
    public boolean commit(BusinessActionContext context, @BusinessActionContextParameter("id") Long id) {
        log.info("tcc create commit[{}] XID: {}", id, context.getXid());
        return true;
    }

    @Override
    public boolean rollback(BusinessActionContext context, @BusinessActionContextParameter("id") Long id) {
        RootContext.bind(context.getXid());
        RootContext.bindBranchType(BranchType.TCC);
        try {
            log.info("tcc create rollback[{}] XID: {}", id, context.getXid());
            server.delete(id);
            return true;
        } finally {
            RootContext.unbindBranchType();
            RootContext.unbind();
        }
    }

}
