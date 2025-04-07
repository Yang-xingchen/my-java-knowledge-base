# ElasticSearch
[官网](https://www.elastic.co/)
[github](https://github.com/elastic/elasticsearch)

# 安装
## java
[JDK安装.md](../../base/JDK安装.md)

> 建议使用jdk8

## Linux配置
### 修改最大文件数和锁内存限制
`vim /etc/security/limits.conf`
```
*               soft    core            0
*               hard    rss             10000
*               -       nofile          262144
*               -       memlock         unlimited
*               -       fsize           unlimited
*               -       as              unlimited
```
### 修改进程能拥有的最大内存区限制，禁用交换区
1. `vim /etc/sysctl.conf`
    ```
    vm.max_map_count = 262144
    vm.swappiness = 1
    ```
2. `sysctl -p`

## 创建用户
> 不允许以`root`用户启动，故需创建新用户。建议所有安装运行操作均以新用户操作。

1. `useradd es`
2. `su es`

## 解压
1. 解压
2. 到根目录

## SSL配置
> 只需要生成一个`cross-cluster.p12`文件，所有节点共用该文件

1. 创建证书: `bin/elasticsearch-certutil ca --pem --out=cross-cluster-ca.zip --pass 123456`
2. 解压: `unzip cross-cluster-ca.zip`
3. 创建p12文件: `bin/elasticsearch-certutil cert --out=cross-cluster.p12 --pass=123456 --ca-cert=ca/ca.crt --ca-key=ca/ca.key --ca-pass=123456 --dns=zk1 --ip=192.168.31.201`
4. 复制到配置目录(该文件需复制到所有节点的`config`目录): `cp cross-cluster.p12 config/`
5. 配置: `vim config/elasticsearch.yml`
   ```
   xpack.security.enabled: true
   xpack.security.remote_cluster_server.ssl.enabled: true
   xpack.security.remote_cluster_server.ssl.keystore.path: cross-cluster.p12
   xpack.security.transport.ssl.enabled: true
   xpack.security.transport.ssl.verification_mode: certificate
   xpack.security.transport.ssl.client_authentication: required
   xpack.security.transport.ssl.keystore.path: cross-cluster.p12
   xpack.security.transport.ssl.truststore.path: cross-cluster.p12
   ```
6. 保存密码:
   ```shell
   bin/elasticsearch-keystore add xpack.security.remote_cluster_server.ssl.keystore.secure_password
   bin/elasticsearch-keystore add xpack.security.transport.ssl.keystore.secure_password
   bin/elasticsearch-keystore add xpack.security.transport.ssl.truststore.secure_password
   ```

## 集群配置
`vim config/elasticsearch.yml`
```
cluster.name: elasticsearch
node.name: {name}
node.roles: ["master", "data"]
network.host: 0.0.0.0
http.port: 9200
http.cors.enabled: true
http.cors.allow-origin: "*"
discovery.seed_hosts: ["zk1:9300", "zk2:9300", "zk3:9300"]
cluster.initial_master_nodes: ["node-1", "node-2", "node-3"]
```
- `cluster.name`: 集群名称, 集群所有节点相同
- `node.name`: 节点名称, 集群中唯一
- `node.roles`: 节点角色, 集群中至少两个`master`
- `network.host`: 允许访问的主机, `0.0.0.0`为所有
- `http`: http配置
- `discovery.seed_hosts`: 服务发现种子主机，集群中的一条即可
- `cluster.initial_master_nodes`: 主节点列表，使用节点名称

## 设置密码
> 需要先启动集群

`bin/elasticsearch-setup-passwords interactive`

## Kibana
1. 解压
2. 到根目录
3. 配置 `vim config/kibana.yml`
   ```
   server.host: 0.0.0.0
   server.publicBaseUrl: "http://192.168.31.201:5601"
   elasticsearch.hosts: ["http://192.168.31.201:9200","http://192.168.31.202:9200","http://192.168.31.203:9200"]
   elasticsearch.username: "kibana_system"
   elasticsearch.password: "123456"
   i18n.locale: "zh-CN"
   ```

# 命令
> 
> 需要切换到非root用户执行 `su es`

- 启动elasticsearch: `bin/elasticsearch -d`
- 启动kibana: `bin/kibana serve`

# 报错说明
## fatal exception while booting Elasticsearchjava.lang.RuntimeException: can not run elasticsearch as root
不允许以`root`用户启动。切换到非`root`用户启动即可。

## fatal exception while booting Elasticsearchjava.nio.file.NoSuchFileException: xxx/jdk/lib/dt.jar
JDK版本过高。降级为JDK8即可。

## max file descriptors [4096] for elasticsearch process is too low, increase to at least [65535];
修改最大文件数和锁内存限制
`vim /etc/security/limits.conf`
```
*               soft    core            0
*               hard    rss             10000
*               -       nofile          262144
*               -       memlock         unlimited
*               -       fsize           unlimited
*               -       as              unlimited
```

## max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144];
修改进程能拥有的最大内存区限制，禁用交换区
`vim /etc/sysctl.conf`
```
vm.max_map_count = 262144
vm.swappiness = 1
```
`sysctl -p`