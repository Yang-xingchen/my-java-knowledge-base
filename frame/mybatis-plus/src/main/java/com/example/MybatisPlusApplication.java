package com.example;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.example.service.*;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MybatisPlusApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MybatisPlusApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    // -------------------- 基础查询 -------------------- //

    @Bean
    public CommandLineRunner base() {
        return new BaseMain();
    }

    @Bean
    public CommandLineRunner wrapper() {
        return new WrapperMain();
    }

    @Bean
    public CommandLineRunner wrapperLambda() {
        return new WrapperLambdaMain();
    }

    @Bean
    public CommandLineRunner lambdaWrapper() {
        return new LambdaWrapperMain();
    }

    @Bean
    public CommandLineRunner activeRecord() {
        return new ActiveRecordMain();
    }

    // -------------------- 插件 -------------------- //

    public static final ThreadLocal<Long> TENANT = new ThreadLocal<>();

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // TenantLineInnerInterceptor: 多租户
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new LongValue(TENANT.get());
            }

            @Override
            public boolean ignoreTable(String tableName) {
                return !tableName.startsWith("t_tenant");
            }
        }));
        // PaginationInnerInterceptor: 分页
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // DynamicTableNameInnerInterceptor: 动态表名
        interceptor.addInnerInterceptor(new DynamicTableNameInnerInterceptor((sql, tableName) -> {
            if (!"t_dt".equals(tableName)) {
                return tableName;
            }
            return "t_dt_" + TENANT.get();
        }));
        // OptimisticLockerInnerInterceptor: 乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor(true));
        // BlockAttackInnerInterceptor: 防止全表更新或删除
        // DataPermissionInterceptor: 数据权限
        // ReplacePlaceholderInnerInterceptor
        return interceptor;
    }

    @Bean
    public CommandLineRunner page() {
        return new PageMain();
    }

    @Bean
    public CommandLineRunner tenant() {
        return new TenantMain();
    }

    @Bean
    public CommandLineRunner dynamicTable() {
        return new DynamicTableMain();
    }

    @Bean
    public CommandLineRunner lock() {
        return new LockMain();
    }

}
