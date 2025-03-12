# my-java-knowledge-base
个人知识库，主要以Java技术为主，与Java关联较大技术也有。
详细内容见各个`*.md`文件内容。

# 目录
> 具体进入对应目录查看

- [base](./base): JDK
- [frame](./frame): 框架
- [middleware](./middleware): 中间件
- [sso](./sso): 单点登录
- [vue](./vue): vue

# JDK
- [加密/解密](./base/src/main/java/codeAndDecode): Base64 / AES / DES
- [编译](./base/src/main/java/compiler): 运行期编译 / 编译期注解
- [特性](./base/src/main/java/feature): JDK8 / JDK17 / JDK21 新版本特性
- [生成器](./base/src/main/java/generator): 简易类Stream框架
- [句柄](./base/src/main/java/invoke): 方法句柄 / 变量句柄 / 调用点 / Lambda实现
- [IO](./base/src/main/java/io): BIO / NIO / AIO(NIO2)
- [JUC](./base/src/main/java/juc): 多线程工具
- [动态代理](./base/src/main/java/proxy): JDK动态代理
- [时间](./base/src/main/java/time): Java8时间相关工具
- _[创建对象](./base/src/main/java/other/CreateEntry.java): 创建对象的6种方式_
- ...

# 生态
> 框架及中间件见[frame](./frame)及[middleware](./middleware)目录

## 微服务导航
| [服务发现](./middleware/nacos/discovery.md) | 服务框架 | 分布式事务 | 备注 | 项目 |
|---|---|---|---|---|
| nacos | spring | / | RestTemplate / RestClient / WebClient / OpenFeign / HttpExchange | [provider](./middleware/nacos/nacos-spring-provider) / [consumer](./middleware/nacos/nacos-spring-consumer) |
| nacos | dubbo | / | dubbo协议 / rest | [api](./frame/dubbo/nacos-dubbo-api) / [provider](./frame/dubbo/nacos-dubbo-provider) / [consumer](./frame/dubbo/nacos-dubbo-consumer) |
| nacos | spring | seata | RestTemplate / RestClient / WebClient / OpenFeign / HttpExchange | [provider](./middleware/seata/seata-spring/seata-spring-provider) / [consumer](./middleware/seata/seata-spring/seata-spring-consumer) |
| nacos | dubbo | seata | triple协议 | [api](./middleware/seata/seata-dubbo/seata-dubbo-api) / [provider](./middleware/seata/seata-dubbo/seata-dubbo-provider) / [consumer](./middleware/seata/seata-dubbo/seata-dubbo-consumer) |
