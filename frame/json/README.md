# 特性比较
| | jackson | 
|---|---|
| 配置作用域 | 实例 |
| boolean | getXxx > isXxx |
| 枚举序列化 | name(默认)/toString(配置)/ordinal(配置) |
| 枚举反序列化 | name(默认)/toString(配置)/ordinal(配置) |
| 未知枚举反序列化 | 异常(默认)/默认值(@JsonEnumDefaultValue且配置)/null(配置) |
| java.time | 依赖+module |
| 静态函数 | 注解 |
| 建造者 | 注解 |
| 构造函数匹配顺序 | 查找所有参数带`@JsonProperty`注解或无参的构造函数 > _多个可能异常可能正常，原因未知_ |
| 无字段getter方法 | 调用 |
| 无字段setter方法 | 调用 |
| 无getter方法 | 不序列化 |
| 无setter方法 | 异常(默认) |
| 循环引用 | 异常(默认)/null(配置) |
| Object类型 | LinkedHashMap |
