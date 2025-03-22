# top
实时显示系统进程和资源显示情况。如
```
top - 15:01:21 up  1:39,  1 user,  load average: 0.11, 0.05, 0.03
Tasks: 159 total,   1 running,  71 sleeping,   0 stopped,   0 zombie
%Cpu(s):  0.0 us,  0.1 sy,  0.0 ni, 99.8 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
KiB Mem :  8149176 total,  7252372 free,   661696 used,   235108 buff/cache
KiB Swap:  1679356 total,  1679356 free,        0 used.  7243204 avail Mem

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND
```
## 显示说明
- 第一行
  - `top - `固定内容
  - `15:01:21`当前时间
  - `up  1:39`开机时间
  - `1 user`用户人数
  - `load average: 0.11, 0.05, 0.03`, 1m、5m、15m平均任务负载
- 第二行: 状态统计
- 第三行: CPU整体负载，按`1`切换为每个CPU负载
- 第四、五行: 物理内存/虚拟内存使用情况
- 第六行(示例为空行): 输入命令时状态显示
- 余下行: 进程使用资源情况
  - PID: 进程号
  - USER: 所属用户
  - PR: 优先级，数字越小优先级越高
  - NI: 同优先级
  - %CPU: CPU使用率
  - %MEM: 物理内存使用率
  - TIME+: 实际使用CPU运行时间
  - COMMAND: 实际命令

## 命令
- `h``?`显示帮助
- `q`退出
- `f`调整显示信息及排序，`P`按CPU使用率排序。`M`按内存使用率排序
- `k`杀死进程
- `r`调整优先级(NI)
- `1`切换CPU整体负载/每个CPU负载
- `z`切换颜色，`Z`配置颜色主题

# ps aux
查看当前进程信息。
- USER: 所属用户
- PID: 进程号
- %CPU: CPU使用率
- %MEM: 物理内存使用率
- VSZ: 虚拟内存量(KB)
- RSS: 占用的固定内存量(KB)
- TTY: 终端
- STAT: 当前状态
  - R: 运行中
  - S: 休眠，可被唤醒
  - D: 不可被唤醒(等待IO等)
  - T: 停止
  - Z: 僵尸状态
- START: 启动时间
- TIME: 实际使用CPU运行时间
- COMMAND: 实际命令

# systemctl
- `systemctl`显示所有服务
- `systemctl start {name}`启动`{name}`
- `systemctl stop {name}`关闭`{name}`
- `systemctl restart {name}`重启`{name}`
- `systemctl status {name}`显示`{name}`状态、配置信息
- `systemctl enable {name}`开机启动启用`{name}`
- `systemctl disable {name}`开机启动不启用`{name}`

# 其他命令
- `df -h`显示磁盘使用情况
- `kill 进程号`杀死进程
  - `-15`(默认)发送停止信号
  - `-9`强制杀死进程
- `uname -a`输出系统信息，按顺序为: 内核名称, 网络节点上的主机名, 内核发行号, 内核版本, 主机的硬件架构名称, 处理器类型或"unknown, 硬件平台或"unknown, 操作系统名称
- `shutdown [time]`关机, `time`为时间(`now`为立即, 输入数字时单位分钟, 输入时间时为对应时间)
- `reboot`重启