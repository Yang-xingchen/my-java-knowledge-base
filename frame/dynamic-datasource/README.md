# dynamic-datasource
[文档](https://www.kancloud.cn/tracy5546/dynamic-datasource/2264611)
[github](https://github.com/baomidou/dynamic-datasource)

# 数据源配置
[application.yaml](src%2Fmain%2Fresources%2Fapplication.yaml)
```yaml
spring:
  datasource:
    dynamic:
      datasource:
        master_1:
          url: jdbc:mysql://192.168.31.201:3306/ds1?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
          username: root
          password: 123456
        master_2:
          url: jdbc:mysql://192.168.31.201:3306/ds2?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
          username: root
          password: 123456
```

# 分组
组名为: `ds.split("_")[0]`, 同组默认采用轮询(`LoadBalanceDynamicDataSourceStrategy`)

# 切换数据源
可填写组名或具体名称。
1. `@DS`注解
2. DynamicDataSourceContextHolder.push

优先级: DynamicDataSourceContextHolder.push > 方法上的`@DS`注解 > 类上的`@DS`注解

# 懒加载
`DynamicRoutingDataSource.addDataSource()`

