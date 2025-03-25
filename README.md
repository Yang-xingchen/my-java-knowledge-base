# my-java-knowledge-base
个人知识库，主要以Java技术为主，与Java关联较大技术也有。

> [!TIP]
> 主要以代码+注释整理，总结内容见各个`*.md`文件内容。

# 目录
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
  - [transaction](frame/spring-transaction): spring事务
  - [format](frame/spring-format): spring序列化/反序列化
  - [listener](frame/spring-listener): 事件监听器
  - [ai](frame/spring-ai): Ollama+deepseek
  - [mvc](frame/spring-mvc): 阻塞式web服务器/客户端
  - [webflux](frame/spring-webflux): 非阻塞式web服务器/客户端
- 数据库
  - [sharding](frame/sharding): 数据库分库分表框架
  - [mybatis](frame/mybatis): ORM框架
  - [dynamic-datasource](frame/dynamic-datasource): 动态数据源 
  - [mybatis-plus](frame/mybatis-plus): mybatis扩展
- 微服务
  - [dubbo](frame/dubbo): RPC框架
  - [sentinel](frame/sentinel): 限流框架
- 其他
  - [reactor](frame/reactor): 响应式框架
  - [parquet](frame/parquet): 列式文件格式
  - [jmh](frame/jmh): 压测
  - [easyexcel](frame/easyexcel): excel读写框架
  - [netty](frame/netty): 网络框架
  - [json](frame/json): json框架
      - [fastjson](frame/json/fastjson)
      - [fastjson2](frame/json/fastjson2)
      - [jackson](frame/json/jackson)

## 中间件
第三方组件，独立进程
- 微服务
  - [消息队列](middleware/消息中间件.md)
    - [activeMQ](middleware/jms): _JMS方式使用_
    - [kafka](middleware/kafka)
    - [pulsar](middleware/pulsar)
  - [mysql](middleware/mysql): CTE/窗口函数的使用
  - [nacos](middleware/nacos): 服务发现/注册中心
  - [powerJob](middleware/powerJob): 定时任务
  - [prometheus](middleware/prometheus): 监控
  - [seata](middleware/seata): 分布式事务
- 大数据
  - [flink](middleware/flink)
  - [hadoop](middleware/hadoop)
  - [zookeeper](middleware/zookeeper)
  - [spark](middleware/spark)
- 其他
  - [canal](middleware/canal): 数据库变更监听

## 笔记
与具体代码无关知识
- Linux
  - [文件命令](note/Linux/文件命令.md)
  - [系统命令](note/Linux/系统命令.md)
  - [网络配置](note/Linux/网络配置.md)
  - [包管理器](note/Linux/包管理器.md)
- [git](note/Git.md)
- [nginx](note/Nginx.md)

# 微服务导航
| [服务发现](./middleware/nacos/discovery.md) | 服务框架 | 分布式事务 | 备注 | 项目 |
|---|---|---|---|---|
| nacos | spring | / | RestTemplate / RestClient / WebClient / OpenFeign / HttpExchange | [provider](./middleware/nacos/nacos-spring-provider) / [consumer](./middleware/nacos/nacos-spring-consumer) |
| nacos | dubbo | / | dubbo协议 / rest | [api](./frame/dubbo/nacos-dubbo-api) / [provider](./frame/dubbo/nacos-dubbo-provider) / [consumer](./frame/dubbo/nacos-dubbo-consumer) |
| nacos | spring | seata | RestTemplate / RestClient / WebClient / OpenFeign / HttpExchange | [provider](./middleware/seata/seata-spring/seata-spring-provider) / [consumer](./middleware/seata/seata-spring/seata-spring-consumer) |
| nacos | dubbo | seata | triple协议 | [api](./middleware/seata/seata-dubbo/seata-dubbo-api) / [provider](./middleware/seata/seata-dubbo/seata-dubbo-provider) / [consumer](./middleware/seata/seata-dubbo/seata-dubbo-consumer) |
