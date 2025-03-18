# canal
[github](https://github.com/alibaba/canal)

[客户端模式](src/main/java/com/example/client/Main.java)
[Kafka](src/main/java/com/example/kafka/KafkaApplication.java)

# 安装
1. 配置mysql，开启binlog`vim /etc/my.cnf`
    ```
    [mysqld]
    log-bin=mysql-bin # 开启 binlog
    binlog-format=ROW # 选择 ROW 模式
    server_id=1 # 配置 MySQL replaction 需要定义，不要和 canal 的 slaveId 重复
   ```
2. 解压
3. 到根目录
4. 配置instance `vim conf/example/instance.properties`
    ```
   canal.instance.mysql.slaveId=2
   canal.instance.master.address=127.0.0.1:3306
   canal.instance.master.journal.name=mysql-bin.000001
   canal.instance.master.position=-1
   canal.instance.master.timestamp=1742112044234
   
   canal.instance.dbUsername=root
   canal.instance.dbPassword=123456
   ```

## 转发kafka配置
1. 配置topic `vim conf/example/instance.properties`
   ```
   canal.mq.topic=canal-topic
   ```
2. 配置kafka连接信息 `vim conf/canal.properties`
   ```
   kafka.bootstrap.servers = 192.168.31.201:9092,192.168.31.202:9092,192.168.31.203:9093
   ```

## 命令
- 启动: `bin/startup.sh`
- 关闭: `bin/stop.sh`