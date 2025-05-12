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

# 安全
## 基础密码
> 旧版本方法，仅使用密码控制。

`vim redis.conf`
```
requirepass 123456
```

## ACL
> 新版本方法，可配置用户名+密码组合，可配置规则。旧方法密码在新版本中配置为用户名为`default`，拥有所有权限的用户。

`vim user.acl`
```
user default +@all ~* >123456
```
- `user`: 命令
- `default`: 用户名, `default`表示默认用户
- `+@all`: `+`允许、`@all`所有命令
- `~*`: 所有`key`
- `>123456`: 密码设置为`123456`

`vim redis.conf`
```
aclfile /etc/user.acl
```

# 持久化
## RDB
> 快照模式: 按一定规则将redis快照保存到文件，记录速度较慢，存储占用较低，重启后恢复较快

### 配置
`vim redis.conf`
```
# 执行规则，每两个参数为一条规则(第一个参数时间，第二个参数变更次数)，满足一条则进行保存
# 3600 1: 3600s中变更1次
# 300 100: 300s中变更100次
# 60 10000: 600s中变更10000次
save 3600 1 300 100 60 10000
# 压缩
rdbcompression yes
# 文件名
dbfilename dump.rdb
```

### 命令
- `save`: 保存数据
- `bgsave`: 后台保存数据

## AOF
> 追加模式: 按一定规则将执行的命令按顺序保存到文件，记录速度较快，存储占用较高，重启后恢复慢

### 重写
重建`aof`文件，优化重复key及已删除key以减少文件大小

### 配置
`vim redis.conf`
```
# 开启
appendonly no
# 文件名
appendfilename "appendonly.aof"
# 写入规则，可选项(按性能降序，丢失可能性升序): always: 每次执行写入命令时; everysec: 每秒; no: 在操作系统写出时
appendfsync everysec
# 重写配置
## 重写最小大小，低于该值时不重写
auto-aof-rewrite-min-size 64mb
## 重写百分比，当与上次重写后的大小比较大于该百分比时，则触发重写。设置为0则禁用自动重写
auto-aof-rewrite-percentage 100
```

### 命令
- `BGREWRITEAOF`: 后台重写`aof`文件 

## 混合
> 上述两种混合

# 集群
自动将数据分片到多个实例上，每个实例处理一部分数据。
**使用lua脚本需要保证所有key都在一个同一个槽**

支持主从模型，从节点拥有与主节点相同的数据，当主节点宕机时可自动将从节点提升为主节点。

## 实现方式
将key按hash取余分成16384个槽，每个节点处理一部分范围。
当客户端访问不是该实例处理的数据时，将会重定向到对应实例处理。
当需要添加或删除实例时，需手动调整处理的范围。

## 配置
1. 配置文件
    ```
   cluster-enabled yes
   cluster-config-file nodes.conf
   cluster-node-timeout 5000
   appendonly yes
   ```
2. 启动每个实例
3. 配置集群 `redis-cli --cluster create ip1:prot1 ip2:prot2 --cluster-replicas 1`
    - `--cluster create`: 集群实例配置，多个以空格分割
    - `--cluster-replicas`: 副本数，分配每个主节点的副本数。注意，副本也从`--cluster create`提供的实例中选取。

# 类型
| 类型名称 | 说明 | type | 命令 | 测试代码 | 需求版本 | 功能说明 |
|---|---|---|---|---|---|---|
| String | 字符串 / 数字 | `string` | xx | [StringTest](src/main/java/com/example/StringTest.java) | | 基础kv存储，也可作为计数器 |
| Hash | 键值对 | `hash` | `H`xx | [HashTest](src/main/java/com/example/HashTest.java) | | |
| List | 有序可重复列表 | `list` | `L`xx | [ListTest](src/main/java/com/example/ListTest.java) | | |
| Set | 无序不重复集合 | `set` | `S`xx | [SetTest](src/main/java/com/example/SetTest.java) | | |
| Sorted set | 有序集合 | `zset` | `Z`xx | [ZsetTest](src/main/java/com/example/ZsetTest.java) | | 常用于排行榜 |
| Stream | 数据流 | `stream` | `X`xx | [StreamTest](src/main/java/com/example/StreamTest.java) | 5.0 | 类似消息队列 |
| Bitmap | 位图 | `string` | xx`BIT`/`BITCOUNT`| [StringTest#bitMap](src/main/java/com/example/StringTest.java) | | 低占用存储boolean数组 |
| Bitfield | 位域 | `string` | `BITFIELD` | [StringTest#bitField](src/main/java/com/example/StringTest.java) | | 低占用存储int数组 |
| Geospatial | 地理位置 | `zset` | `GEO`xx | [GeoTest](src/main/java/com/example/GeoTest.java) | 3.2 | 计算坐标位置的距离/查找范围内坐标 |
| JSON | json | `ReJSON-RL` | `JSON.`xx | [OtherTest#json](src/main/java/com/example/OtherTest.java) | Redis Stack / 8.0 | 类似文档数据库 |
| Probabilistic data types | 统计 | | | | Redis Stack / 8.0 | 大数据统计，使用近似值以提高效率降低存储 |
| - HyperLogLog | 基数统计 | `string` | `PFADD`/`PFCOUNT` | [HyperLogLogTest](src/main/java/com/example/HyperLogLogTest.java) | Redis Stack / 8.0 | 统计大量元素的去重数量，结果与实际值有一定的误差。占用<12k, 误差<0.81% |
| - Bloom filters | 布尔过滤器 | `MBbloom--` | `BF.`xx | [OtherTest#bf](src/main/java/com/example/OtherTest.java) | Redis Stack / 8.0 | 判断一个数是否存在集合，可能将不存在判断为存在，集合不可以删除元素 |
| - Cuckoo filters | 布谷鸟过滤器 | `MBbloomCF` | `CF.`xx | [OtherTest#cf](src/main/java/com/example/OtherTest.java) | Redis Stack / 8.0 | 判断一个数是否存在集合，可能将不存在判断为存在，集合可以删除元素(可能误删)，集合添加元素可能失败 |
| - t-digest | 近似分位数 | `TDIS-TYPE` | `TDIGEST.`xx | [OtherTest#tdigest](src/main/java/com/example/OtherTest.java) | Redis Stack / 8.0 | 计算近似的百分位或元素的百分位位置，结果与实际值有一定的误差 |
| - Top-K | 前k位 | `TopK-TYPE` | `TOPK.`xx | [OtherTest#topk](src/main/java/com/example/OtherTest.java) | Redis Stack / 8.0 | 获取最高的k元素值，结果与实际值有一定的差异 |
| - Count-min sketch | 频率统计 | `CMSk-TYPE` | `CMS.`xx | [OtherTest#cms](src/main/java/com/example/OtherTest.java) | Redis Stack / 8.0 | 统计元素的频率，结果与实际值有一定的误差 |
| Time series | 时间序列 | `TSDB-TYPE` | `TS.`xx | [OtherTest#ts](src/main/java/com/example/OtherTest.java) | Redis Stack / 8.0 | 类似时序数据库 |
| Vector set | 向量 | `vectorset` | `V`xx | | 8.0(preview) | 向量 |