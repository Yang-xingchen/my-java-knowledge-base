# 特性比较
| | jackson | fastjson2 |
|---|---|---|
| 配置作用域 | 实例 | 全局/实例类/调用 |
| transient | 正常处理 | 忽略 |
| boolean | getXxx > isXxx | isXxx > getXxx |
| 枚举序列化 | name(默认)/toString(配置)/ordinal(配置) | name(默认)/toString(配置)/ordinal(配置) |
| 枚举反序列化 | name(默认)/toString(配置)/ordinal(配置) | name |
| 未知枚举反序列化 | 异常(默认)/默认值(@JsonEnumDefaultValue且配置)/null(配置) | null |
| java.time | 依赖+module | 支持 |
| time格式 | DateTimeFormatter.ISO_DATE_TIME | yyyy-MM-dd HH:mm:ss |
| 静态函数 | 注解 |  |
| 建造者 | 注解 | 注解_(未知如何使用)_ |
| 构造函数匹配顺序 | 查找所有参数带`@JsonProperty`注解或无参的构造函数 > _多个可能异常可能正常，原因未知_ | `@JSONCreator` > 无参 > 随机 |
| 无字段getter方法 | 调用 | 调用 |
| 无字段setter方法 | 调用 | 不调用 |
| 无getter方法 | 不序列化 | 不序列化 |
| 无setter方法 | 异常(默认) | 不反序列化 |
| 循环引用 | 异常(默认)/null(配置) | 异常(默认) |
| Object类型 | LinkedHashMap | JSONObject(默认)/LinkedHashMap(配置) |
| Array类型 | ArrayList | JSONArray(默认)/ArrayList(配置) |
| 小数类型 | Double(默认)/BigDecimal(配置) | BigDecimal(默认)/Float(配置)/Double(配置) |

# 性能比较
[link](https://github.com/alibaba/fastjson2/wiki/fastjson_benchmark)