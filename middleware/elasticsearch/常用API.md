# _cat
查询信息，以紧凑对齐(compact and aligned text)方式显示

通用url参数:
- `v`: 显示详细信息
- `help`: 显示返回的每列信息
- `h`: 控制显示的列, 多个列用`,`隔开
- `bytes`: 格式化数字
- `s`: 排序, 如`s=store.size:desc,index:asc`

常用接口:

| cat命令 | 完整命令 | 说明 |
|---|---|---|
| `GET _cat`| | 显示可用列表|
| `GET _cat/health?v`| `GET _cluster/health` | 查看集群健康情况 |
| `GET _cat/recovery?v`| `GET _recovery` | 查看集群恢复情况 |
| `GET _cat/master?v`| `GET _nodes` | 查看主节点信息 |
| `GET _cat/nodes?v`| `GET _nodes` | 查看集群节点信息 |
| `GET _cat/thread_pool?v`| `GET _nodes` | 查看节点线程池信息 |
| `GET _cat/fielddata?v`| `GET _nodes` | 查看字段内存使用情况 |
| `GET _cat/plugins?v`| `GET _nodes` | 查看插件信息 |
| `GET _cat/shards?v`| | 查看分片信息 |
| `GET _cat/alolocation?v`| | 查看分片存储信息 |
| `GET _cat/indices?v`| `GET _all` | 查看集群索引情况 |
| `GET _cat/count?v`| `GET _all/count` | 查看集群文档总数 |
| `GET _cat/segments?v`| `GET _segments` | 查看分段信息 |
| `GET _cat/aliases?v`| `GET _aliases` | 查看别名 |
| `GET _cat/tasks?v`| `GET _task` | 查看任务信息 |

# 集群 & 节点

- `GET _cluster/health`: 集群健康状态
- `GET _cluster/state`: 集群详细状态
- `GET _cluster/stats`: 集群统计
- `GET _cluster/settings`: 集群设置
- `GET _nodes`: 节点信息
- `GET _nodes/{nodeName}`: `{nodeName}`节点信息
- `GET _nodes/stats`: 节点状态
- `GET _nodes/{nodeName}/stats`: `{nodeName}`节点状态

# 索引
见[索引结构.md](索引结构.md)

# 操作数据
- `POST {index}/_doc`: 添加文档, 自动生成id，请求体为文档内容
- `PUT {index}/_create/{id}`: 添加id为`{id}`的文档, 文档存在则报错，请求体为文档内容
- `PUT {index}/_doc/{id}`: 添加或更新id为`{id}`的文档，请求体为文档内容
- `GET {index}/_doc/{id}`: 获取id为`{id}`的文档, 包含详细信息
- `GET {index}/_source/{id}`: 获取id为`{id}`的文档, 仅包含文档内容
- `DELETE {index}/_doc/{id}`: 删除id为`{id}`的文档
- `POST {index}/_delete_by_query`: 删除查询的文档，请求体为查询内容`{"query":{}}`，见[Query DSL.md](Query%20DSL.md)
- `POST {index}/_update/{id}`: 更新id为`{id}`的文档
  - `{"script":{"source":""}}`: 请求体为`script`, 执行`script.source`更新文档
  - `{"doc":{}}`: 请求体为`doc`, 使用`doc`内容更新文档(存在`script`则忽略)
- `POST {index}/_update_by_query`: 查询并更新文档
  - `{"query":{}}`: 查询内容，见[Query DSL.md](Query%20DSL.md)
  - `{"script":{"source":""}}`: 请求体为`script`, 执行`script.source`更新文档
  - `{"doc":{}}`: 请求体为`doc`, 使用`doc`内容更新文档(存在`script`则忽略)
- `GET _mget`: 获取多个文档，请求体为`{"docs":[{"_index":"","_id":""}]}`
- `GET {index}/_mget`: 获取`{index}`下多个文档，请求体为`{"docs":[{"_id":""}]}`或`{"ids:[]"}`
- `POST _bulk`: 多个索引或删除操作，可减少开销提高速度，**单个操作失败不会影响后续操作**。请求体为`json`序列`{}\n{}\n{}\n`
  - `{"index":{"_index":"","_id":""}}\n{}`: 添加或更新文档，第二行为文档内容
  - `{"create":{"_index":"","_id":""}}\n{}`: 添加文档，第二行为文档内容，文档存在则失败
  - `{"update":{"_index":"","_id":""}}\n{}`: 更新文档，第二行为同`POST {index}/_update/{id}`请求体
  - `{"delete":{"_index":"","_id":""}}`: 删除文档
- `POST {index}/_bulk`: 同`POST _bulk`，提供默认操作索引，在未显式提供索引时使用
- `POST _sql`: 使用`SQL`查询数据，请求体为`{"query":""}`

# 查询
见[Query DSL.md](Query%20DSL.md)
