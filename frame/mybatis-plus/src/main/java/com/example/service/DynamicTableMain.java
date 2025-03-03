package com.example.service;

import com.example.entry.DynamicTable;
import com.example.mapper.DynamicTableMapper;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import static com.example.MybatisPlusApplication.TENANT;

public class DynamicTableMain implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DynamicTableMain.class);

    @Autowired
    private DynamicTableMapper dynamicTableMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("==================== {} ====================", getClass().getSimpleName());
        test(1, 1, true);
        test(1, 2, false);
        test(2, 1, false);
    }

    private void test(long save, long select, boolean exist) {
        DynamicTable dynamicTable = new DynamicTable();
        dynamicTable.setName("dynamicTable-" + save + "-" + select);
        TENANT.set(save);
        try {
            dynamicTableMapper.insert(dynamicTable);
        } finally {
            TENANT.remove();
        }
        DynamicTable selectUser;
        TENANT.set(select);
        try {
            selectUser = dynamicTableMapper.selectById(dynamicTable.getId());
        } finally {
            TENANT.remove();
        }
        log.info("find: save: {}, select: {}, dynamicTable: {}", save, select, selectUser);
        if (exist) {
            Assertions.assertNotNull(selectUser);
        } else {
            if (selectUser != null) {
                Assertions.assertNotEquals("dynamicTable-" + save + "-" + select, selectUser.getName());
            }
        }
    }


}
