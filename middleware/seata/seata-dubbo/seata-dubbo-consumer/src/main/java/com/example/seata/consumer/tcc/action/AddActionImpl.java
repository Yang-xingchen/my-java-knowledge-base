package com.example.seata.consumer.tcc.action;

import com.example.seata.server.Server;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.seata.core.context.RootContext;
import org.apache.seata.core.model.BranchType;
import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.apache.seata.rm.tcc.api.BusinessActionContextParameter;
import org.apache.seata.rm.tcc.api.LocalTCC;
import org.apache.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@LocalTCC
@Service
public class AddActionImpl implements AddAction {

    @DubboReference(group = Server.NONE)
    private Server server;

    private static final Logger log = LoggerFactory.getLogger(AddActionImpl.class);

    @Override
    @TwoPhaseBusinessAction(name = "add",
            commitArgsClasses = {BusinessActionContext.class, Long.class},
            rollbackArgsClasses = {BusinessActionContext.class, Long.class, Integer.class})
    public void prepare(BusinessActionContext context, @BusinessActionContextParameter("id") Long id, @BusinessActionContextParameter("i") int i, @BusinessActionContextParameter("err") boolean err) {
        log.info("tcc add prepare[{}] XID: {}", id, context.getXid());
        server.add(id, i, err);
    }

    @Override
    public boolean commit(BusinessActionContext context, @BusinessActionContextParameter("id") Long id) {
        log.info("tcc add commit[{}] XID: {}", id, context.getXid());
        return true;
    }

    @Override
    public boolean rollback(BusinessActionContext context, @BusinessActionContextParameter("id") Long id, @BusinessActionContextParameter("i") Integer i) {
        RootContext.bind(context.getXid());
        RootContext.bindBranchType(BranchType.TCC);
        try {
            log.info("tcc add rollback[{}] XID: {}", context.getActionContext("id", Long.class), context.getXid());
            server.add(id, -i, false);
            return true;
        } finally {
            RootContext.unbindBranchType();
            RootContext.unbind();
        }
    }

}
