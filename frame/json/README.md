# 特性比较
| | jackson | fastjson | fastjson2 |
|---|---|---|---|
| 配置作用域 | 实例 | 全局 / 实例类 / 调用 | 全局 / 实例类 / 调用 |
| transient | 序列化 / 反序列化 | 忽略该字段 | 忽略该字段 |
| boolean序列化方法 | getXxx > isXxx | getXxx > isXxx | isXxx > getXxx |
| 枚举序列化 | name(默认) / toString(配置) / ordinal(配置) | name(默认) / toString(配置) / ordinal(配置) | name(默认) / toString(配置) / ordinal(配置) |
| 枚举反序列化 | name(默认) / toString(配置) / ordinal(配置) | name | name |
| 未知枚举反序列化 | 异常(默认) / 默认值(@JsonEnumDefaultValue且配置) / null(配置) | null | null |
| 反序列化数字基本类型 | 支持 | 支持 | 支持 |
| 反序列化浮点基本类型 | 支持 | 支持 | 支持 |
| 反序列化布尔基本类型 | 支持 | 支持 | 支持 |
| `null` | 支持 | 支持 | 支持 |
| `null`参数 | 异常 | 支持 | 支持 |
| record | 支持 | 不支持序列化 | 支持 |
| java.time | 依赖+module | 支持 | 支持 |
| time格式 | DateTimeFormatter.ISO_DATE_TIME | DateTimeFormatter.ISO_DATE_TIME | yyyy-MM-dd HH:mm:ss |
| 静态函数 | 注解 | 不支持 | 不支持 |
| 建造者 | 注解 | 注解 | 注解 |
| 启动继承 | 注解 | 注解+配置 / 配置 | 注解+配置 / 配置(不安全) |
| 继承标识字段 | `@type` / `@class` / 自定义 | `@type` / 自定义 | `@type` / 自定义 |
| 构造函数匹配顺序 | 查找所有参数带`@JsonProperty`注解或无参的构造函数 > _多个可能异常可能正常，原因未知_ | `@JSONCreator` > 无参 > 随机 | `@JSONCreator` > 无参 > 随机 |
| 无字段getter方法 | 调用 | 调用 | 调用 |
| 无字段setter方法 | 调用 | 调用 | 调用 |
| 无getter方法 | 不序列化 | 不序列化 | 不序列化 |
| 无setter方法 | 异常(默认) | 不反序列化 | 不反序列化 |
| 循环引用 | 异常(默认) / null(配置) | 支持 | 异常(默认) |
| Mixin | 支持 | 支持 | 支持 |
| Stream读 | 支持 | 支持 | 支持 |
| Stream写 | 支持 | 支持 | 支持 |
| Object类型 | LinkedHashMap | JSONObject(默认) / LinkedHashMap(配置) | JSONObject(默认) / LinkedHashMap(配置) |
| Array类型 | ArrayList | JSONArray(默认) / ArrayList(配置) | JSONArray(默认) / ArrayList(配置) |
| 小数类型 | Double(默认) / BigDecimal(配置) | BigDecimal(默认) / Float(配置) / Double(配置) | BigDecimal(默认) / Float(配置) / Double(配置) |

# 性能比较
[link](https://github.com/alibaba/fastjson2/wiki/fastjson_benchmark)