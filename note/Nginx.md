# nginx
[官网](https://nginx.org/)

# 基础管理
## 安装
https://nginx.org/en/linux_packages.html
1. 添加源 `vim /etc/yum.repos.d/nginx.repo` 
    ```
   [nginx-stable]
   name=nginx stable repo
   baseurl=http://nginx.org/packages/centos/$releasever/$basearch/
   gpgcheck=1
   enabled=1
   gpgkey=https://nginx.org/keys/nginx_signing.key
   module_hotfixes=true
    
   [nginx-mainline]
   name=nginx mainline repo
   baseurl=http://nginx.org/packages/mainline/centos/$releasever/$basearch/
   gpgcheck=1
   enabled=0
   gpgkey=https://nginx.org/keys/nginx_signing.key
   module_hotfixes=true
   ```
2. 清理缓存 `yum clean all`
3. 建立缓存 `yum makecache`
4. 安装 `yum install nginx`

## 命令
- 启动`nginx`
- 重新加载配置文件 `nginx -s reload`
- 关闭 `nginx -s quit`
- 查看启动配置 `nginx -V`

# 配置文件
## 全局
最外层，主要包含全局配置
- worker_processes: worker进程数量

## events
服务器和客户端间网络链接配置
- worker_connections: 每个worker进程接收的网络链接数量

## http
网络配置，如反向代理、负载均衡等
- include: 导入配置文件

### upstream
负载均衡组，通过`proxy_pass`使用，默认为轮询
- ip_hash: 切换为根据ip哈希
- least_conn: 切换最少链接
- `server ip:prot`: 代理的服务器
  - `width:n`: 配置权重为`n`
  - `down`: 暂时下线，忽略该节点

### limit_req_zone
限流配置，通过`limit_req`使用
```
limit_req_zone {key} zone={name}:{size} {rate}
```
- `{key}`: 限流应用规则，使用nginx变量限定
- `{name}`: 规则名称，不重复即可
- `{size}`: 存储区大小
- `{rate}`: 限流规则，如`10r/s`为每100ms一个请求

### server
端口配置
- listen: 监听端口
- server_name: 服务名称

#### location
url映射
```
location [ = | ~ | ~* | ^~ ] uri { ... }
```
- `[ = | ~ | ~* | ^~ ]`: 修饰符，匹配规则(除`@`按优先级)
  - `@`: 定义命名的location，不用于常规处理
  1. `=`: 精确匹配
  2. `~`: 区分大小写的正则匹配
  3. `~*`: 不区分大小写的正则匹配
  4. `^~`: 非正则匹配
  5. 无修饰符: 前缀字符
- root: 本机文件根目录
- index: 起始文件名
- proxy_pass: 反向代理服务器
- limit_req: 限流
  - `zone`: 配置名称
  - `burst`: 缓冲链接，如`burst=2`允许两个请求同时到来(默认仍然按照速率配置访问)而不返回失败
  - `nodelay`: 缓冲链接不按照速率配置访问，即支持并发

# 变量
配置文件中可用变量，内置变量见 https://nginx.org/en/docs/varindex.html


# 简单示例
## 静态页面
`vim conf.d/static.conf`
```
server {
    listen       8080;
    server_name  static;

    location /static {
        root   /opt/module/nginx/html;
        index  index.html index.htm;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }

}
```
即可通过 `http://localhost:8080/static` 访问 `/opt/module/nginx/html`下的文件。

## 负载均衡
`vim conf.d/proxy.conf`
```
upstream proxy {
    server 192.168.31.218:8080;
    server 192.168.31.218:8081 weight=3;
}

server {
    listen       8081;
    server_name  proxy;

    location / {
        proxy_pass   http://proxy;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }

}
```
即可通过 `http://localhost:8081` 轮询访问`http://192.168.31.218:8080`和`http://192.168.31.218:8081`服务器。

## 限流
`vim conf.d/limit.conf`
```
limit_req_zone $binary_remote_addr zone=mylimit:10m rate=1r/s;

server {
    listen       8082;
    server_name  limit;

    location / {
        limit_req zone=mylimit;
        root   /usr/share/nginx/html;
        index  index.html index.htm;
    }
    
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```
即可通过 `http://localhost:8082` 访问默认页面，若每秒访问大于1次，则返回错误页面(503错误)

## TCP代理/负载均衡
> [!IMPORTANT]
> 确保开启了stream模块，可通过`nginx -V`查看命令中是否包含`--with-stream`判断

`vim nginx.conf`添加如下行，将`stream.conf.d`下配置导入配置文件
```
stream {
    include /etc/nginx/stream.conf.d/*.conf
}
```
创建`stream.conf.d`文件夹 `mkdir stream.conf.d`

`vim stream.conf.d/tcp.conf`
```
upstream tcp {
    server 192.168.31.218:8080;
    server 192.168.31.218:8081 weight=3;
}

server {
    listen 8083;
    proxy_pass tcp;
}
```
即可通过`localhost:8083`通过tcp访问`192.168.31.218:8080`和`192.168.31.218:8081`

## websocket代理
`vim conf.d/websocket.conf`
```
map $http_upgrade $connection_upgrade {
    default upgrade;
    '' close;
}

upstream ws {
    server 192.168.31.218:8080;
    server 192.168.31.218:8081;
    keepalive 1000;
}

server {
    listen       8084;
    server_name  ws;

    location / {
        proxy_http_version 1.1;
        proxy_pass http://ws;
        proxy_redirect off;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded $proxy_add_x_forwarded_for;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection $connection_upgrade;
        proxy_read_timeout 3600s;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```
即可通过 `ws://localhost:8084` 轮询链接 `ws://192.168.31.218:8080`和`ws://192.168.31.218:8081`

## HTTPS
获取证书，自签证书见 [SSL.md](Linux/SSL.md)
`vim conf.d/ssl.conf`
```
upstream sslproxy {
    server 192.168.31.218:8080;
    server 192.168.31.218:8081 weight=3;
}

server {
    listen       443 ssl;
    server_name  ssl;

    ssl_certificate      /etc/nginx/cert/ssl.crt;
    ssl_certificate_key  /etc/nginx/cert/ssl.key;

    ssl_session_cache    shared:SSL:1m;
    ssl_session_timeout  5m;

    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    location / {
        proxy_pass   http://sslproxy;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```
即可通过 `https://localhost` 轮询访问`https://192.168.31.218:8080`和`https://192.168.31.218:8081`服务器。
