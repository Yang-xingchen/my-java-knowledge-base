# docker
[官网](https://www.docker.com/)
[仓库](https://hub.docker.com/)

# 安装
https://developer.aliyun.com/mirror/docker-ce

# 仓库镜像配置
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

# 镜像
容器模板，定义容器运行环境，只读。
默认文件`Dockerfile`

## 命令
- 搜索镜像 `docker search {name}`
- 拉取镜像 `docker pull {name}`
- 查看镜像 `docker image ls` `docker images`
- 删除镜像 `docker image rm {anme}`
- 创建镜像(Dockerfile) `docker build .`
  - `-t {name}`: 指定名称, 格式: `{name:tag}`
  - `-f {name}`: 指定文件, 默认`Dockerfile`
  - `.`: 构建上下文

## Dockerfile
构建镜像的指令列表文件，docker可通过该文件构建镜像。
每行依次执行。

每行常用内容列表:
- `# {comments}`: 注释
- `FROM {image}`: 基础镜像，在该镜像上进行构筑，可使用`FROM {image} AS {name}`定义名称，供后续使用
- `ARG {name}`: 设定变量，可使用`{name}={value}`设置默认值，参数在`docker run --build-arg {name}={value}`中设置，在后续步骤中可用`${name}`使用该变量
- `RUN {command}`: 执行命令在当前镜像上创建新层，在后续步骤使用该层
- `WORKDIR {directory}`: 设置工作目录，可多次使用(多次使用可以将以前一个路径为当前路径设置相对路径)
- `COPY`: 复制文件，可以复制多个文件，最后一个参数为目标路径(若以`/`结尾，为目录，否则为文件)
  - `{src} {dst}`: 文件名不包含空格
  - `["{src}", "{dst}"]`: 文件名可以包含空格
  - `--from={image|stage|context}`: 定义源文件位置，可使用: `image`镜像、`stage`步骤，即`FROM`定义的名称，`context`上下文
- `CMD {command}`: 运行容器时执行的命令，只有最后一个生效(构建镜像时不执行)
- `ENV {name}={value}`: 设置环境变量
- `EXPOSE {port}`: 定义发布端口(该命令不实际发布端口，实际发布需在`docker run`命令中添加`-P`参数)

# 网络
docker容器间通信网络，处理多个容器间的通信(不包含主机)

## 命令
- 创建docker网络 `docker network create {name}`
- 查看docker网络 `docker network ls`
- 删除docker网络 `docker network rm`

# 存储卷
docker容器数据持久化，可在删除docker容器后保留数据。

## 命令
- 创建存储卷 `docker volume create {name}`
- 查看存储卷 `docker volume ls`
- 查看存储卷信息 `docker volume inspect {name}`
- 删除存储卷 `docker volume rm {name}`

# 容器
实际运行程序进程，docker最小管理单位，由模板生成。

## 命令

### 新建容器
若无镜像则自动拉取

`docker run {name}`
- `{name}`: 镜像名称，可用`{name}:{tag}`指定版本
- `-d`: 后台运行
- `-p {target}:{source}`: 绑定端口, `{target}`目标端口，即主机上的端口, `{source}`源端口，即容器内的端口
- _`-v {target}:{source}`: 挂载，`{target}`目标目录，即主机上的目录或者存储卷, `{source}`源目录，即容器内的目录_
- `--mount {param}`: 挂载, 可更好控制，推荐使用
  - `{param}`: 参数，以`option1=value1,option2=value2`的格式出现，如`type=volume,src=volume,dst=/etc/file`
    - `type`: 类型，可选`volume`(存储卷, 默认)`bind`(本机)`tmpfs`(Linux临时文件，容器停止即销毁)
    - `src`: 存储卷名称(`volume`)或主机目录(`bind`)
    - `dst`: 容器内目录, 必须为绝对路径
    - `volume-subpath`: (仅`volume`类型)，存储卷目录
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

### 管理容器
- 启动容器 `docker start {name/id}`
- 停止容器(优雅停止) `docker stop {name/id}`
- 停止容器(强行停止) `docker kill {name/id}`
- 删除容器 `docker rm {name/id}`: (需要先停止容器)
- 重命名容器 `docker rename {name} {newName}`
- 提交容器 `docker container commit {containerName/containerId} {imageName}`: 将当前容器状态封装成镜像
  - `-c`: 更改`Dockerfile`配置
- 查看容器 `docker ps`
  - `-a`: 所有容器(包含未运行的容器)
- 查看状态 `docker stats`
  - `-a`: 所有(默认为运行中)
- 查看容器日志 `docker logs {name}`

### 操作容器
- 进入容器 `docker exec -it {name/id} {cmd}`
  - `{cmd}`: 执行命令，一般使用`bash`
- 复制文件 `docker cp {source} {target}`
  - `{source}`: 源文件
  - `{target}`: 目标文件
  - 格式: 可用相对路径或绝对路径，加上`{name}:`前缀表示为`{name}`容器

# compose
多个容器编排，定义多个容器组成的系统规则。
默认文件`compose.yaml`

## 命令
- 构建、(重新)创建、启动compose: `docker compose up`
  - `-d`: 后台运行
  - `--build`: 创建对应镜像
- 停止、删除compose: `docker compose down`
- 查看compose日志: `docker compose logs`
- 查看compose: `docker compose ps`

## compose.yaml
用于定义`compose`内容的文件。采用`yaml`格式

参数:
- `name`: 名称
- `include`: 导入其他`compose`文件，yaml数组或yaml对象数组定义多个值
  - `path`: 导入的`compose.yaml`文件路径
  - `env_file`: `.env`文件路径, 
- `x-{xxx}`: 扩展，可在任意地方定义，忽略内容。可用于yaml引用
- `services`: 服务
  - `{name}`: 名称，定义包含的容器，自定义，不重复即可，可通过该名称服务发现
    - `image`: 使用的镜像
    - `volumes`: 存储卷列表, 参数同`-mount`, yaml对象数组定义多个
    - `environment`: 环境变量列表
      - `{env}`: 环境变量名称及对应值
    - `ports`: 使用的端口列表, 同`-p`, `{target}:{source}`格式定义, yaml数组定义多个值
    - `labels`: 标签列表
      - `{lab}`: 标签及对应值
    - `networks`: 网络列表，使用yaml数组定义多个，可使用该文件顶级`networks`配置
    - `restart`: 自动重启, 值同`--restart`
    - `mem_limit`: 内存大小, 值同`-m`
    - `cpus`: 设置cpu限额, 值同`-cpus`
    - `configs`: 配置文件列表, yaml数组或yaml对象数组定义多个值
      - `source`: 配置名称
      - `target`: 挂载文件路径
      - `mode`: 权限，Linux权限格式
    - `secrets`: 敏感文件列表, yaml数组或yaml对象数组定义多个值
      - `source`: 配置名称
      - `target`: 挂载文件路径
      - `mode`: 权限，Linux权限格式
    - `depends_on`: 依赖服务列表，保证启动该服务前启动依赖服务，停止该服务后停止依赖服务
- `networks`: 网络
  - `default`: 默认网络，即所有容器都会加入的网络，参数同`{name}`
  - `{name}`: 自定义网络名称
    - `external`: 是否为外部网络，若为ture，则会使用已有网络而不创建新的
    - `lables`: 标签列表
      - `{lab}`: 标签及对应值
- `volumes`: 存储卷
  - `{name}`: 存储卷名称
    - `external`: 是否为外部存储卷，若为ture，则会使用已有存储卷而不创建新的
    - `lables`: 标签列表
      - `{lab}`: 标签及对应值
- `configs`: 配置
  - `{name}`: 配置名称
    - `file`: 主机配置文件路径
    - `external`: 是否为外部配置，若为ture，则会使用已有配置而不创建新的
    - `content`: 配置文件内容
    - `environment`: 是否写入环境变量
- `secrets`: 敏感文件
  - `{name}`: 敏感文件名称
    - `file`: 主机敏感文件路径