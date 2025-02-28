package com.example;

import com.example.server.UserServer;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DynamicDatasourceApplication {

    private static final Logger log = LoggerFactory.getLogger(DynamicDatasourceApplication.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(DynamicDatasourceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    public CommandLineRunner w1r1(UserServer userServer) {
        return args -> {
            Long id = userServer.save1("w1r1");
            Assertions.assertNotNull(userServer.find1(id));
            log.info("w1r1: {}", id);
        };
    }

    @Bean
    public CommandLineRunner w2r2(UserServer userServer) {
        return args -> {
            Long id = userServer.save2("w2r2");
            Assertions.assertNotNull(userServer.find2(id));
            log.info("w2r2: {}", id);
        };
    }

    @Bean
    public CommandLineRunner w1r2(UserServer userServer) {
        return args -> {
            Long id = userServer.save1("w1r2");
            Assertions.assertNotEquals("w1r2", userServer.find2(id));
            log.info("w1r2: {}", id);
        };
    }

//    @Bean
//    public DataSource dataSource(DefaultDataSourceCreator creator) {
//        DynamicDataSourceProvider provider = () -> Stream.of("1", "2").collect(Collectors.toMap(s -> "master_" + s, s -> {
//            DataSourceProperty property = new DataSourceProperty();
//            property.setUrl("jdbc:mysql://192.168.31.201:3306/ds" + s + "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8");
//            property.setUsername("root");
//            property.setPassword("123456");
//            return creator.createDataSource(property);
//        }));
//        return new DynamicRoutingDataSource(List.of(provider));
//        每次执行SQL都会创建数据源，需手动关闭
//        return new DynamicRoutingDataSource(List.of()) {
//            @Override
//            public DataSource getDataSource(String ds) {
//                log.info("create dataSource: {}", ds);
//                String s = ds.split("_")[1];
//                DataSourceProperty property = new DataSourceProperty();
//                property.setUrl("jdbc:mysql://192.168.31.201:3306/ds" + s + "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8");
//                property.setUsername("root");
//                property.setPassword("123456");
//                return creator.createDataSource(property);
//            }
//        };
//    }

}
