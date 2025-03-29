# redis
[官网](https://redis.io/)
[文档](https://redis.io/docs/latest/develop/)
[github](https://github.com/redis/redis)

# 代码
- [RedisApplication.java](src/main/java/com/example/RedisApplication.java): 启动类
- [StringTest.java](src/main/java/com/example/StringTest.java): ValueOperations
- [HashTest.java](src/main/java/com/example/HashTest.java): HashOperations
- [ListTest.java](src/main/java/com/example/ListTest.java): ListOperations
- [SetTest.java](src/main/java/com/example/SetTest.java): SetOperations
- [ZsetTest.java](src/main/java/com/example/ZsetTest.java): ZSetOperations
- [StreamTest.java](src/main/java/com/example/StreamTest.java): StreamOperations
- [GeoTest.java](src/main/java/com/example/GeoTest.java): GeoOperations
- [HyperLogLogTest.java](src/main/java/com/example/HyperLogLogTest.java): HyperLogLogOperations
- [OtherTest.java](src/main/java/com/example/OtherTest.java): 其他spring未适配的操作, 采用lua脚本调用

# 类型
| 类型名称 | 说明 | type | 命令 | 测试代码 | 需求版本 | 功能说明 |
|---|---|---|---|---|---|---|
| String | 字符串 / 数字 | `string` | xx | [StringTest](src/main/java/com/example/StringTest.java) | | 基础kv存储，也可作为计数器 |
| Hash | 键值对 | `hash` | `H`xx | [HashTest](src/main/java/com/example/HashTest.java) | | |
| List | 有序可重复列表 | `list` | `L`xx | [ListTest](src/main/java/com/example/ListTest.java) | | |
| Set | 无序不重复集合 | `set` | `S`xx | [SetTest](src/main/java/com/example/SetTest.java) | | |
| Sorted Set | 有序集合 | `zset` | `Z`xx | [ZsetTest](src/main/java/com/example/ZsetTest.java) | | 常用于排行榜 |
| Stream | 数据流 | `stream` | `X`xx | [StreamTest](src/main/java/com/example/StreamTest.java) | 5.0 | 类似消息队列 |
| Bitmap | 位图 | `string` | xx`BIT`/`BITCOUNT`| [StringTest#bitMap](src/main/java/com/example/StringTest.java) | | 低占用存储boolean数组 |
| Bitfield | 位域 | `string` | `BITFIELD` | [StringTest#bitField](src/main/java/com/example/StringTest.java) | | 低占用存储int数组 |
| Geospatial | 地理位置 | `zset` | `GEO`xx | [GeoTest](src/main/java/com/example/GeoTest.java) | 3.2 | 计算坐标位置的距离/查找范围内坐标 |
| JSON | json | `ReJSON-RL` | `JSON.`xx | [OtherTest#json](src/main/java/com/example/OtherTest.java) | Redis Stack | 类似文档数据库 |
| Probabilistic data types | 统计 | | | | Redis Stack | 大数据统计，使用近似值以提高效率降低存储 |
| - HyperLogLog | 基数统计 | `string` | `PFADD`/`PFCOUNT` | [HyperLogLogTest](src/main/java/com/example/HyperLogLogTest.java) | Redis Stack | 统计大量元素的去重数量，结果与实际值有一定的误差。占用<12k, 误差<0.81% |
| - Bloom filters | 布尔过滤器 | `MBbloom--` | `BF.`xx | [OtherTest#bf](src/main/java/com/example/OtherTest.java) | Redis Stack | 判断一个数是否存在集合，可能将不存在判断为存在，集合不可以删除元素 |
| - Cuckoo filters | 布谷鸟过滤器 | `MBbloomCF` | `CF.`xx | [OtherTest#cf](src/main/java/com/example/OtherTest.java) | Redis Stack | 判断一个数是否存在集合，可能将不存在判断为存在，集合可以删除元素(可能误删)，集合添加元素可能失败 |
| - t-digest | 近似分位数 | `TDIS-TYPE` | `TDIGEST.`xx | [OtherTest#tdigest](src/main/java/com/example/OtherTest.java) | Redis Stack | 计算近似的百分位或元素的百分位位置，结果与实际值有一定的误差 |
| - Top-K | 前k位 | `TopK-TYPE` | `TOPK.`xx | [OtherTest#topk](src/main/java/com/example/OtherTest.java) | Redis Stack | 获取最高的k元素值，结果与实际值有一定的差异 |
| - Count-min sketch | 频率统计 | `CMSk-TYPE` | `CMS.`xx | [OtherTest#cms](src/main/java/com/example/OtherTest.java) | Redis Stack | 统计元素的频率，结果与实际值有一定的误差 |
| Time series | 时间序列 | `TSDB-TYPE` | `TS.`xx | [OtherTest#ts](src/main/java/com/example/OtherTest.java) | Redis Stack | 类似时序数据库 |
