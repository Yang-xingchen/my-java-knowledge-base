# my-java-knowledge-base
个人知识库，主要以Java技术为主，与Java关联较大技术也有。

> [!TIP]
> 主要以代码+注释整理，总结内容见各个`*.md`文件内容。

# 目录
标签说明:
- `spring`: 项目使用spring系列(官方版本)框架
- `spring ali`: 项目使用spring系列(阿里版本)框架
- `h2`: 项目数据库采用`h2`，可直接运行
- `mysql`: 项目数据库采用`mysql`, 需提前准备环境(建表语句在对应`src/main/resources/schema.sql`)

目录说明: 
- [base](./base): JDK
- [frame](./frame): 框架
- [middleware](./middleware): 中间件
- [note](./note): 笔记
- _[sso](./sso): 单点登录_
- _[vue](./vue): vue_

## JDK
- [加密/解密](./base/src/main/java/codeAndDecode): Base64 / AES / DES
- [编译](./base/src/main/java/compiler): 运行期编译 / 编译期注解
- [特性](./base/src/main/java/feature): JDK8 / JDK17 / JDK21 新版本特性
- [生成器](./base/src/main/java/generator): 简易类Stream框架
- [句柄](./base/src/main/java/invoke): 方法句柄 / 变量句柄 / 调用点 / Lambda实现
- [IO](./base/src/main/java/io): BIO / NIO / AIO(NIO2)
- [JUC](./base/src/main/java/juc): 多线程工具
- [动态代理](./base/src/main/java/proxy): JDK动态代理
- [时间](./base/src/main/java/time): Java8时间相关工具
- [spi](./base/src/main/java/spi): spi
- 奇技淫巧
  - [创建对象](./base/src/main/java/other/CreateEntry.java): 创建对象的`6种`方式
  - [改变字段](./base/src/main/java/other/ChangeField.java): 改变字段值的`5种`方式
  - [方法调用](./base/src/main/java/other/InvokeMethod.java): 调用方法的`4种`方式
  - [单例](./base/src/main/java/other/BreakSingleton.java): 创建及破坏单例
- ...

## 框架
一些框架的使用
- spring
  - [transaction](frame/spring-transaction): `spring` `mysql` spring事务
  - [format](frame/spring-format): `spring` spring序列化/反序列化
  - [listener](frame/spring-listener): `spring` 事件监听器
  - [ai](frame/spring-ai): `spring` Ollama+deepseek
  - [mvc](frame/spring-mvc): `spring` 阻塞式web服务器/客户端
  - [webflux](frame/spring-webflux): `spring` 非阻塞式web服务器/客户端
  - [mvc-websocket](frame/spring-mvc-websocket): `spring` 阻塞式websocket服务器/客户端
  - [webflux-websocket](frame/spring-webflux-websocket): `spring` 非阻塞式websocket服务器/客户端
  - [cache](frame/spring-cache): `spring` 缓存适配
- 数据库
  - [sharding](frame/sharding): `spring` `mysql` 数据库分库分表框架
  - [mybatis](frame/mybatis): `spring` `h2` ORM框架
  - [dynamic-datasource](frame/dynamic-datasource): `spring` `mysql` 动态数据源 
  - [mybatis-plus](frame/mybatis-plus): `spring` `h2` mybatis扩展
- 微服务
  - [dubbo](frame/dubbo): `spring ali` RPC框架
  - [sentinel](frame/sentinel): `spring ali` 限流框架
- 其他
  - [easyexcel](frame/easyexcel): excel读写框架
  - [reactor](frame/reactor): 响应式框架
  - [jmh](frame/jmh): 压测
  - [parquet](frame/parquet): 列式文件格式
  - [netty](frame/netty): 网络框架
  - [json](frame/json): json框架
      - [fastjson](frame/json/fastjson)
      - [fastjson2](frame/json/fastjson2)
      - [jackson](frame/json/jackson)
  - [caffeine](frame/caffeine): 本地缓存框架

## 中间件
第三方组件，独立进程
- 微服务
  - [消息队列](middleware/消息中间件.md)
    - [activeMQ](middleware/jms): _JMS方式使用_
    - [kafka](middleware/kafka): `spring`
    - [pulsar](middleware/pulsar): `spring`
  - [数据库](middleware/数据库.md)
    - [mysql](middleware/mysql): CTE/窗口函数的使用
    - [redis](middleware/redis): `spring` redis
    - [Elasticsearch](middleware/elasticsearch): `spring` Elasticsearch
  - [nacos](middleware/nacos): `spring ali` 服务发现/注册中心
  - [powerJob](middleware/powerJob): `spring` 定时任务
  - [prometheus](middleware/prometheus): `spring` 监控
  - [seata](middleware/seata): `spring ali` `mysql` 分布式事务
- 大数据
  - [flink](middleware/flink)
  - [hadoop](middleware/hadoop)
  - [zookeeper](middleware/zookeeper)
  - [spark](middleware/spark)
- 其他
  - [canal](middleware/canal): `spring` 数据库变更监听

## 笔记
与具体代码无关知识
- Linux
  - [文件命令](note/Linux/文件命令.md)
  - [系统命令](note/Linux/系统命令.md)
  - [网络配置](note/Linux/网络配置.md)
  - [包管理器](note/Linux/包管理器.md)
- [git](note/Git.md)
- [正则](note/正则表达式.md)
- [nginx](note/Nginx.md)
- [docker](note/Docker.md)
- [缓存](note/缓存.md)
- JVM
  - [运行时内存区域.md](note/jvm/运行时内存区域.md)
  - [GC.md](note/jvm/GC.md)

# 微服务导航
| [服务发现](./middleware/nacos/discovery.md) | 服务框架 | 分布式事务 | 备注 | 项目 |
|---|---|---|---|---|
| nacos | spring | / | RestTemplate / RestClient / WebClient / OpenFeign / HttpExchange | [provider](./middleware/nacos/nacos-spring-provider) / [consumer](./middleware/nacos/nacos-spring-consumer) |
| nacos | dubbo | / | dubbo协议 / rest | [api](./frame/dubbo/nacos-dubbo-api) / [provider](./frame/dubbo/nacos-dubbo-provider) / [consumer](./frame/dubbo/nacos-dubbo-consumer) |
| nacos | spring | seata | RestTemplate / RestClient / WebClient / OpenFeign / HttpExchange | [provider](./middleware/seata/seata-spring/seata-spring-provider) / [consumer](./middleware/seata/seata-spring/seata-spring-consumer) |
| nacos | dubbo | seata | triple协议 | [api](./middleware/seata/seata-dubbo/seata-dubbo-api) / [provider](./middleware/seata/seata-dubbo/seata-dubbo-provider) / [consumer](./middleware/seata/seata-dubbo/seata-dubbo-consumer) |
