package com.example.seata.provider;

import com.example.seata.server.Server;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(group = Server.XA)
public class ServerXaImpl implements Server {

    private static final Logger log = LoggerFactory.getLogger(ServerXaImpl.class);

    @Autowired
    private TestMapper testMapper;

    @Override
    public Long create(int init) {
        log.info("XID: {}", RootContext.getXID());
        Entry entry = new Entry();
        entry.setVal(init);
        testMapper.save(entry);
        return entry.getId();
    }

    @Override
    public void add(Long id, int i, boolean err) {
        log.info("add: ID: {}, XID: {}-{}", id, RootContext.getXID(), RootContext.getBranchType());
        if (err) {
            throw new RuntimeException("err");
        }
        testMapper.add(id, i);
    }

    @Override
    public Integer get(Long id) {
        log.info("get: ID: {}, XID: {}", id, RootContext.getXID());
        return testMapper.get(id);
    }

    @Override
    public void delete(Long id) {
        log.info("delete: ID: {}, XID: {}", id, RootContext.getXID());
        testMapper.delete(id);
    }

}
