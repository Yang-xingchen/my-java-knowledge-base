# canal
[github](https://github.com/alibaba/canal)

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
 
