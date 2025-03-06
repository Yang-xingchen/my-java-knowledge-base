# seata
[官网](https://seata.apache.org/zh-cn/)

# 安装
## JDK
[安装](../../base/JDK安装.md)

## seata
解压即可

## 其他
- 启动 `bin/seata-server.sh`
- 管理平台 http://192.168.31.201:7091/
- 默认用户名密码: `seata` `seata`

# 领域模型
![](https://img.alicdn.com/tfs/TB19qmhOrY1gK0jSZTEXXXDQVXa-1330-924.png)

- TC (Transaction Coordinator) - 事务协调者: 维护全局和分支事务的状态，驱动全局事务提交或回滚。
- TM (Transaction Manager) - 事务管理器: 定义全局事务的范围：开始全局事务、提交或回滚全局事务。
- RM (Resource Manager) - 资源管理器: 管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚。

# 事务模式
> AT模式和XA模式基于数据库代理，暂时未知如何在同一个项目使用多个代理(已尝试[dynamic-datasource](../../frame/dynamic-datasource)框架，在处理提交及回滚时出现问题)。

## AT
[at](seata-dubbo/seata-dubbo-consumer/src/main/java/com/example/seata/consumer/at)

**需要在对应数据库下创建`undo_log`表**
```
-- 注意此处0.3.0+ 增加唯一索引 ux_undo_log
CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

支持数据库: 
- MySQL
- Oracle
- PostgreSQL
- TiDB
- MariaDB

## TCC
> try-confirm-cancel(TCC)

[tcc](seata-dubbo/seata-dubbo-consumer/src/main/java/com/example/seata/consumer/tcc)

**需要`@LocalTCC`和`@TwoPhaseBusinessAction`注解标记**
1. `@LocalTCC`标注调用的操作类，该类实现`prepare`、`commit`、`rollback`方法。
2. `@TwoPhaseBusinessAction`注解标记`prepare`方法，并用`@BusinessActionContextParameter`标记业务参数。
3. `prepare`方法表示`try`阶段，该阶段执行业务检查并锁定资源。
4. `commit`方法表示`confirm`阶段，该阶段提交事务(尽量不失败, 可能重试)。
5. `rollback`方法表示`cancel`阶段，该阶段回滚事务(尽量不失败, 可能重试)。

支持数据库: 不依赖数据库

## Saga

支持数据库: 不依赖数据库

## XA

[ServerXaImpl.java](seata-dubbo/seata-dubbo-provider-xa/src/main/java/com/example/seata/provider/ServerXaImpl.java)

**启动类需要添加`@EnableAutoDataSourceProxy(dataSourceProxyMode = "XA")`注解**

支持数据库:
- MySQL
- Oracle
- PostgreSQL
- MariaDB

## 比较
| | AT | TCC | Saga | XA |
|---|---|---|---|---|
| 一致性 | 弱一致 | 弱一致 | 最终一致 | 强一致 |
| 隔离性 | 全局锁 | 资源预留 | 无 | 完全隔离 |
| 代码侵入性 | 无 | 需手动实现三个方法 | 要编写状态机及补偿代码 | 无 |
| 性能 | 高 | 很高 | 很高 | 低 |

# 微服务适配
## Dubbo
- [api](./seata-dubbo/seata-dubbo-api/src/main/java/com/example/seata/server/Server.java)
- [provider](./seata-dubbo/seata-dubbo-provider/src/main/java/com/example/seata/provider/SeataDubboProviderApplication.java)
- [consumer](./seata-dubbo/seata-dubbo-consumer/src/main/java/com/example/seata/consumer/SeataDubboConsumerApplication.java)

## Spring
- [provider](./seata-spring/seata-spring-provider/src/main/java/com/example/seata/provider/SeataSpringProviderApplication.java)
- [consumer](./seata-spring/seata-spring-consumer/src/main/java/com/example/seata/consumer/SeataSpringConsumerApplication.java)
  - [RestTemplate](./seata-spring/seata-spring-consumer/src/main/java/com/example/seata/consumer/service/RestTemplateServerImpl.java)
    ```java
    @Bean
    public RestTemplate restTemplate(LoadBalancerInterceptor loadBalancer, SeataRestTemplateInterceptor seata) {
        return new RestTemplateBuilder()
				.interceptors(loadBalancer, seata)
				.build();
    }
    ```
  - [RestClient](./seata-spring/seata-spring-consumer/src/main/java/com/example/seata/consumer/service/RestClientServerImpl.java)
    ```java
    @Bean
    public RestClient restClient(LoadBalancerInterceptor loadBalancer, SeataRestTemplateInterceptor seata) {
        return RestClient.builder()
				.requestInterceptors(interceptors -> {
					interceptors.add(loadBalancer);
					interceptors.add(seata);
				})
				.build();
    }
    ```
  - [WebClient](./seata-spring/seata-spring-consumer/src/main/java/com/example/seata/consumer/service/WebClientServerImpl.java)
    ```java
    @Bean
    public WebClient webClient(ReactorLoadBalancerExchangeFilterFunction filter) {
        return WebClient.builder()
				.filter(filter)
				.filter((request, next) -> {
					String xid = RootContext.getXID();
					if (StringUtils.hasLength(xid)) {
						request = ClientRequest.from(request)
								.headers(httpHeaders -> httpHeaders.add(RootContext.KEY_XID, xid))
								.build();
					}
					return next.exchange(request);
				})
				.build();
    }
    ```
  - [Feign](./seata-spring/seata-spring-consumer/src/main/java/com/example/seata/consumer/service/FeignServerImpl.java)
  - [HttpExchange](./seata-spring/seata-spring-consumer/src/main/java/com/example/seata/consumer/service/ExchangeServerImpl.java)

