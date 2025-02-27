package com.example.seata.consumer.tcc.action;

import org.apache.seata.rm.tcc.api.BusinessActionContext;

public interface AddAction {

    void prepare(BusinessActionContext context, Long id, int i, boolean err);

    boolean commit(BusinessActionContext context, Long id);

    boolean rollback(BusinessActionContext context, Long id, Integer i);

}
