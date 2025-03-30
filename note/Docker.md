# docker
[官网](https://www.docker.com/)
[仓库](https://hub.docker.com/)

# 安装
https://developer.aliyun.com/mirror/docker-ce

# 镜像配置
`vim /etc/docker/daemon.json`
```
{
    "registry-mirrors": [
        "https://dockerproxy.com",
        "https://hub-mirror.c.163.com",
        "https://mirror.baidubce.com",
        "https://ccr.ccs.tencentyun.com"
    ]
}
```

# 命令
- 搜索镜像 `docker search {name}`
- 拉取镜像 `docker pull {name}`
- 查看镜像 `docker image ls` `docker images`
- 删除镜像 `docker image rm {anme}`
- 创建镜像(Dockerfile) `docker build .`
  - `-t {name}`: 指定名称, 格式: `{name:tag}`
  - `-f {name}`: 指定文件, 默认`Dockerfile`
- 新建容器(若无镜像则自动拉取) `docker run {name}`
  - `{name}`: 镜像名称，可用`{name}:{tag}`指定版本
  - `-d`: 后台运行
  - `-p {target}:{source}`: 绑定端口, `{target}`目标端口，即主机上的端口, `{source}`源端口，即容器内的端口
  - _`-v {target}:{source}`: 挂载，`{target}`目标目录，即主机上的目录或者存储卷, `{source}`源目录，即容器内的目录_
  - `-mount type={type},src={src},targe={targe}`: 挂载, 可更好控制，推荐使用
    - `{type}`: 类型，可选`volume`(存储卷)`bind`(本机)
    - `{src}`: 存储卷名称(`volume`)或主机目录(`bind`)
    - `{targe}`: 容器内目录
  - `-e {name}={value}`: 配置环境变量, `{name}`参数名称，`{value}`参数值
  - `--name {name}`: 名称，设定容器名称。可使用名称管理容器
  - `-l {key}={value}`: 标签，可使用标签管理容器
  - `--network={name}`: 链接到docker`{name}`网络
    - `-ip`: 设定ip
    - `--network-alias`: 设定域名
  - `--restart={value}`: 自动重启, `{value}`值，可选: 
    - `no`关闭自动重启
    - `always`除手动停止外停止都重启(手动重启要等docker守护程序启动时启动)
    - `unless-stopped`除手动停止外停止都重启
    - `on-failure[:max-retries]`异常时重启(可设定重启次数)
  - `--storage-opt size={size}`: 设置容器磁盘大小
  - `-m`: 设置最大可用内存
  - `--cpus`: 设置cpu限额
- 启动容器 `docker start {name/id}`
- 停止容器(优雅停止) `docker stop {name/id}`
- 停止容器(强行停止) `docker kill {name/id}`
- 查看容器 `docker ps`
  - `-a`: 所有容器(包含未运行的容器)
- 删除容器 `docker rm {name/id}`: (需要先停止容器)
- 进入容器 `docker -it {name/id} {cmd}`
  - `{cmd}`: 执行命令，一般使用`bash`
- 复制文件 `docker cp {source} {target}`
  - `{source}`: 源文件
  - `{target}`: 目标文件
  - 格式: 可用相对路径或绝对路径，加上`{name}:`前缀表示为`{name}`容器
- 查看容器日志 `docker logs {name}`
- 重命名容器 `docker rename {name} {newName}`
- 提交容器 `docker container commit {containerName/containerId} {imageName}`: 将当前容器状态封装成镜像
  - `-c`: 更改`Dockerfile`配置
- 创建docker网络 `docker network create {name}`
- 查看docker网络 `docker network ls`
- 删除docker网络 `docker network rm`
- 创建存储卷 `docker volume create {name}`
- 查看存储卷 `docker volume ls`
- 查看存储卷信息 `docker volume inspect {name}`
- 删除存储卷 `docker volume rm {name}`
- 构建、(重新)创建、启动compose: `docker compose up`
  - `-d`: 后台运行
  - `--build`: 创建对应镜像
