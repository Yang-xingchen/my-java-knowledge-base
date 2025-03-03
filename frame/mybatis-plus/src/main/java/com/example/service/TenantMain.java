package com.example.service;

import com.example.entry.TenantRole;
import com.example.entry.TenantUser;
import com.example.mapper.TenantRoleMapper;
import com.example.mapper.TenantUserMapper;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import static com.example.MybatisPlusApplication.TENANT;

public class TenantMain implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(TenantMain.class);

    @Autowired
    private TenantUserMapper userMapper;
    @Autowired
    private TenantRoleMapper roleMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("==================== {} ====================", getClass().getSimpleName());
        test(1, 1, true);
        test(1, 2, false);
        test(2, 1, false);
    }

    private void test(long save, long select, boolean exist) {
        TenantUser user = new TenantUser();
        String name = "tenant-" + save + "-" + select;
        user.setName(name);
        TenantRole role = new TenantRole();
        role.setName(name);
        TENANT.set(save);
        try {
            roleMapper.insert(role);
            user.setRid(role.getId());
            userMapper.insert(user);
        } finally {
            TENANT.remove();
        }
        TenantUser selectUser;
        TenantRole selectRole;
        TENANT.set(select);
        try {
            selectRole = roleMapper.selectById(role.getId());
            selectUser = userMapper.selectById(user.getId());
        } finally {
            TENANT.remove();
        }
        log.info("find: save: {}, select: {}, user: {}", save, select, selectUser);
        log.info("find: save: {}, select: {}, role: {}", save, select, selectRole);
        if (exist) {
            Assertions.assertNotNull(selectRole);
            Assertions.assertNotNull(selectUser);
        } else {
            if (selectUser != null) {
                Assertions.assertNotEquals(name, selectUser.getName());
            }
            if (selectRole != null) {
                Assertions.assertNotEquals(name, selectRole.getName());
            }
        }

        // 对于自定义带join的sql，租户拼接条件写在ON语句里
        TENANT.set(select);
        try {
            selectUser = userMapper.get(user.getId());
        } finally {
            TENANT.remove();
        }
        log.info("get: save: {}, select: {}, user: {}", save, select, selectUser);
        if (exist) {
            Assertions.assertNotNull(selectUser);
            Assertions.assertNotNull(selectUser.getRole());
        } else {
            if (selectUser != null) {
                Assertions.assertNotEquals(name, selectUser.getName());
                Assertions.assertNotEquals(name, selectUser.getRole().getName());
            }
        }

    }


}
