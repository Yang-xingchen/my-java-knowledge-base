# 网络连接测试
- `ping ip`
- `ping host`

# 配置静态ip
1. 查看网卡名称 `ip addr` `ifconfig -a`
2. 配置网卡 `vim /etc/sysconfig/network-scripts/{网卡名称}`
    ```
   BOOTPROTO="static"
   IPADDR=192.168.31.201
   GATEWAY=192.168.31.1
   NETMASK=255.255.255.0
   DNS1=8.8.8.8
   ```
3. 重启网络服务 `service network restart`

# 关闭防火墙
```shell
systemctl disable firewalld
```

# 编辑host
`vim /etc/hosts`
```
ip name
```