package com.example.seata.consumer.tcc.action;

import org.apache.seata.rm.tcc.api.BusinessActionContext;

public interface CreateAction {

    Long prepare(BusinessActionContext context, int init);

    boolean commit(BusinessActionContext context, Long id);

    boolean rollback(BusinessActionContext context, Long id);

}
