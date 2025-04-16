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
| `GET _cat/templates?v`| `GET _templates` | 查看索引模板 |
| `GET _cat/component_templates?v`| `GET _component_templates` | 查看组件模板 |

# 集群 & 节点

- `GET /`: 集群信息
- `GET _license`: 获取许可证信息
- `GET _cluster/health`: 集群健康状态
- `GET _cluster/state`: 集群详细状态
- `GET _cluster/stats`: 集群统计
- `GET _cluster/settings`: 集群设置
- `GET _nodes`: 节点信息
- `GET _nodes/{nodeName}`: `{nodeName}`节点信息
- `GET _nodes/stats`: 节点状态
- `GET _nodes/{nodeName}/stats`: `{nodeName}`节点状态

# 索引
创建及修改: 见[索引结构.md](索引结构.md)

- `DELETE {index}`: 删除
- `POST {index}/_open`: 打开
- `POST {index}/_close`: 关闭
- `POST {index}/_flush`: 强制`flush`，调用一次`refresh`并将`Segment`写入磁盘及清空`Transaction Log`
- `POST {index}/_forcemerge`: 强制`merge`，合并`Segment`文件及删除已删除的文档
- `POST {index}/_update_by_query`: 更新文档，主要用于变更映射后应用变更到旧文档
- `POST _reindex`: 将数据写入其他索引，请求体为`{"source":{"index":""},"dest":{"index":""}}`。
- `POST _reindex?wait_for_completion=false`: 异步将数据写入其他索引，请求体为`{"source":{"index":""},"dest":{"index":""}}`。

# 索引模板
创建或更新索引模板
```
PUT _index_template/{name}
{
  "index_patterns": "",
  "composed_of": [],
  "priority": 1,
  "template": {
    "aliases": {},
    "settings": {},
    "mappings": {}
  },
  "version": 0,
  "data_stream": {}
}
```
- `{name}`: 模板名称
- `index_patterns`: 索引格式，使用通配符或数组表示该模板匹配的索引名称
- `composed_of`: 合并的组件模板名称列表，按顺序合并组件模板
- `priority`: 优先级，选择匹配模板中数字最大的模板
- `template`: 模板内容
- `template.aliases`: 别名，见[索引结构.md](索引结构.md)
- `template.settingsa`: 设置，见[索引结构.md](索引结构.md)
- `template.mappings`: 映射，见[索引结构.md](索引结构.md)
- `version`: 管理版本号
- `data_stream`: 使用数据流

模拟创建索引(不实际创建索引，返回为创建索引的内容，请求体同`创建或更新索引模板`)
`POST _index_template/_simulate`

模拟创建索引(不实际创建索引，返回为创建索引的内容，无需请求体)
`POST _index_template/_simulate/{name}`
- `{name}`: 使用的模板

获取模板
`GET _index_template/{name}`
- `{name}`: 模板名称，可使用通配符

获取全部模板
`GET _index_template`

删除模板
`DELETE _index_template/{name}`
- `{name}`: 模板名称，可使用通配符

# 组件模板
创建或更新组件模板
```
PUT _component_template/{name}
{
  "template": {
    "aliases": {},
    "settings": {},
    "mappings": {}
  },
  "version": 0
}
```
- `{name}`: 模板名称
- `template`: 模板内容
- `template.aliases`: 别名，见[索引结构.md](索引结构.md)
- `template.settingsa`: 设置，见[索引结构.md](索引结构.md)
- `template.mappings`: 映射，见[索引结构.md](索引结构.md)
- `version`: 管理版本号

模拟创建模板(不实际创建模板，返回为创建模板的内容，请求体同`创建或更新组件模板`)
`POST _index_template/_simulate`

获取模板
`GET _component_template/{name}`
- `{name}`: 模板名称，可使用通配符

获取全部模板
`GET _component_template`

删除模板
`DELETE _component_template/{name}`
- `{name}`: 模板名称，可使用通配符

# 别名
操作别名
```
POST _aliases
{
  "actions": [
    {
      "add": {
        "index": "",
        "alias": "",
        "aliases": [],
        "filter": {},
        "is_write_index": true
      }
    },
    {
      "remove": {
        "alias": "",
        "aliases": []      
      }
    }
  ]
}
```
- `actions`: 操作列表
- `actions.add`: 添加或更新操作
- `actions.add.index`: 操作的索引，支持通配符
- `actions.add.alias`: 别名名称
- `actions.add.aliases`: 别名名称
- `actions.add.filter`: 过滤的内容，见[Query DSL.md](Query%20DSL.md)
- `actions.add.is_write_index`: 是否为写索引，同一个别名只能有一个写索引，用于写入数据
- `actions.remove`: 删除操作
- `actions.remove.alias`: 别名名称
- `actions.remove.aliases`: 别名名称

获取别名
`GET _alias/{name}`
- `{name}`: 别名名称，可使用通配符

获取全部别名
`GET _alias`

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
```
GET {index}/_search
{
  "explain": true,
  "_source": [],
  "fields": [],
  "script_fields": [],
  "runtime_mappings": {},
  "collapse": {},
  "highlight": {},
  "query": {},
  "filter": {},
  "indices_boost": [],
  "suggest": {
    "{name}": {
      "text": "",
      "term": {
        "field": "{fieldName}"
      }
    }
  },
  "aggs": {},
  "sort": [
    {"{field}": {"order": "asc"}}
    {"{fields}": {"order": "asc", "mode": "min"}}
  ],
  "from": n,
  "size": m,
  "search_after": [],
  "seq_no_primary_term": true
}
```
- `explain`: 显示查询优化信息。类似`EXPLAIN`
- `_source`、`fields`、`script_fields`: 返回的字段列表，默认返回全部字段。类似`SELECT`
  - `_source`: 仅原始字段
  - `fields`: 可对字段进行格式化
  - `script_fields`: 可进行方法计算值
- `runtime_mappings`: 运行期映射，使用已存在字段通过脚本添加新字段。同[`mappings.runtime`](./索引结构)
- `collapse`: 去重。类似`DISTINCT`
- `highlight`: 高亮字段，可添加`html标签`将重要的字词进行标注。
- `{index}`: 查询的索引，可使用通配符。类似`FROM`
- `query`: 查询，判断文档与查询的匹配程度，返回最佳匹配的文档。匹配程度由`_score`字段表示, 内容见[Query DSL.md](Query%20DSL.md)。忽略匹配全部文档。类似`WHERE`
- `filter`: 过滤，判断文档是否满足查询，返回匹配的文档，不进行打分, 内容见[Query DSL.md](Query%20DSL.md)。忽略匹配全部文档。类似`WHERE`
- `suggest`: 建议
- `suggest.{name}`: 建议名称, 可定义多个不同的名称。
- `suggest.{name}.text`: 提供建议的文本
- `suggest.{name}.term`: 对`suggest.{name}.text`分析后建议
- `suggest.{name}.term.field`: 获取建议的字段
- `suggest.{name}.term.suggest_mode`: 获取建议模式: `missing`(默认，提供的文本不存在时提供)，`popular`(提供比提供文档更多存在于索引中的建议)，`always`(提供所有匹配的建议)
- `suggest.{name}.phrase`: 基于`term`建议增加些逻辑处理
- `suggest.{name}.phrase.highlight`: 基于`term`建议增加高亮
- `suggest.{name}.completion`: (仅`completion`类型字段)补全
- `suggest.{name}.completion.contexts`: (仅`completion`类型字段)补全上下文
- `indices_boost`: 对于多索引情况，可为不同索引分配得分权重，默认全部1.0。
- `aggs`: 聚合，内容见[聚合.md](聚合.md)。类似`GROUP BY`。
- `sort`: 排序。类似`ORDER BY`。
- `sort.{field}`: 排序字段, 按顺序依次排列
- `sort.{field}.order`: 排序顺序, 可选`asc`、`desc`
- `sort.{field}.mode`: 多值字段取值方法，可选`min`、`max`、`sum`、`avg`、`median`
- `from`, `size`: 分页，`size`默认10，默认最大值10000。类似`LIMIT`
- `search_after`: 分页，根据上次查询结果取下一页，可优化查询
- `seq_no_primary_term`: 返回乐观锁字段`_seq_no`和`_primary_term`，可于更新时使用

返回:
```
{
  "took": 0,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 0,
      "relation": "eq"
    },
    "max_score": 1,
    "hits": [
      {
        "_index": "",
        "_id": "-UH9HpYBOUCHwSzIzUjT",
        "_score": 1,
        "_source": {
        },
        "fields": {
        }
      }
    ]
  },
  "suggest":{},
  "aggregations":{},
}
```
- `took`: 执行毫秒数
- `timed_out`: 是否超时
- `_shards`: 分片
- `_shards.total`: 总分片数
- `_shards.successful`: 成功的分片数
- `_shards.skipped`: 忽略的分片数
- `_shards.failed`: 失败的分片数
- `hits.total.value`: 匹配查询的文档总数
- `hits.total.relation`: 结果精确(`eq`)、只是部分(`gte`)
- `hits.max_score`: 匹配文档最大得分
- `hits.hits`: 匹配的文档
- `hits.hits._index`: 匹配的文档索引
- `hits.hits._id`: 匹配的文档id
- `hits.hits._score`: 匹配的文档得分
- `hits.hits._source`: 匹配的文档内容
- `hits.hits.fields`: 匹配的文档内容, 由`fields`、`script_fields`、`docvalue_fields`、`stored_fields`指定
- `suggest`: 建议的结果
- `aggregations`: 聚合查询结果

# 查询模板
创建或更新模板
```
PUT _scripts/{name}
{
  "script": {
    "lang": "mustache",
    "source": {
    }
  }
}
```
- `{name}`: 名称
- `script`: 模板内容
- `script.lang`: 脚本语言，查询模板固定为`mustache`
- `script.source`: 内容见[Query DSL.md](Query%20DSL.md)，可使用`{{name}}`使用名称为`name`的变量

模拟查询(不实际查询，请求体同`创建或更新模板`)
```
POST _render/template
{
  "source": {},
  "params": {}
}
```
- `script`: 模板内容
- `param`: 参数

模拟查询(不实际查询)
```
POST _render/template
{
  "id": "{name}",
  "params": {}
}
```
- `id`: 使用的模板名称
- `param`: 参数

查询
```
GET {index}/_search/template
{
  "id": "{name}",
  "params": {}
}
```
- `{index}`: 查询的索引
- `id`: 使用的模板名称
- `param`: 参数

获取查询模板
`GET _scripts/{name}`
- `{name}`: 模板名称

获取全部查询模板
`GET _scripts`

删除查询模板
`DELETE _scripts/{name}`
- `{name}`: 模板名称

# Ingest Pipeline(采集管道)
创建或更新
```
PUT _ingest/pipeline/{name}
{
  "description": "",
  "processors": [],
  "version": 0
}
```
- `{name}`: 名称
- `description`: 描述
- `processors`: 处理器列表，见[官方文档](https://www.elastic.co/docs/reference/enrich-processor)
- `processors.description`: (通用字段)描述
- `processors.ignore_failure`: (通用字段)忽略错误
- `processors.on_failure`: (通用字段)错误处理Pipeline，内容同`processors`
- `version`: 版本号

模拟处理数据
```
POST /_ingest/pipeline/_simulate
{
  "pipeline": {
    "description": "",
    "processors": []
  },
  "docs": []
}
```
- `{name}`: 名称
- `pipeline`: pipeline内容
- `pipeline.description`: 描述
- `pipeline.processors`: 处理器列表
- `docs`: 处理文档列表

模拟处理数据
```
POST /_ingest/pipeline/{name}/_simulate
{
  "docs": []
}
```
- `{name}`: 名称
- `docs`: 处理文档列表

使用: 在操作URL上添加`pipeline={name}`参数

获取
`GET _ingest/pipeline/{name}`
- `{name}`: 名称

获取全部
`GET _ingest/pipeline`

删除
`DELETE _ingest/pipeline/{name}`
- `{name}`: 名称

# Enrich policy(扩充策略)
创建
```
PUT /_enrich/policy/{name}
{
  "{mathType}": {
    "indices": "",
    "match_field": "",
    "enrich_fields": ""
  }
}
```
- `{name}`: 名称
- `{matchType}`: 匹配规则，可选`geo_match`(地理位置)、`match`(精确值)、`range`(数据范围)
- `{matchType}.indices`: 使用的索引
- `{matchType}.match_field`: 匹配的字段
- `{matchType}.enrich_fields`: 添加的字段

执行(更新内容，在源文档变动后执行)
`PUT _enrich/policy/{name}/_execute`
- `{name}`: 名称

_配置Ingest Pipeline(忽略其他，详细见`Ingest Pipeline`)_
```
PUT _ingest/pipeline/{pipelineName}
{
  "description": "",
  "processors": [
    {
      "enrich": {
        "description": "{name}",
        "policy_name": "",
        "field": "",
        "target_field": ""
      }
    }
  ],
  "version": 0
}
```
- `processors.enrich`: Enrich处理器
- `processors.enrich.policy_name`: 名称
- `processors.enrich.field`: 添加的文档中用于匹配的字段
- `processors.enrich.target_field`: 添加到文档中的字段

获取
`GET _enrich/policy/{name}`
- `{name}`: 名称

获取全部
`GET _enrich/policy`

获取正在执行的信息
`GET _enrich/_stats`

删除
`DELETE _enrich/policy/{name}`
- `{name}`: 模板名称

# ILM(索引生命周期管理)
创建
```
PUT _ilm/policy/{name}
{
  "policy": {
    "_mata": {},
    "phases": {
      "{phase}": {
        "min_age": "",
        "actions": {
          "rollover": {
            "max_size": "",
            "max_age": "",
            "max_docs": 10000
          },
          "allocate": {
            "number_of_replicas": 1
          },
          "shrink": {
            "allow_write_after_shrink": false,          
            "number_of_shards": 1
          },
          "forcemerge": {
            "max_num_segments": 1
          }
          "readonly": {},
          "set_priority": {
            "priority": 1
          }
        }
      }
    }
  }
}
```
- `{name}`: 名称
- `policy`: 策略
- `policy._mata`: 元数据，内容自定义
- `policy.phases`: 处理阶段列表
- `policy.phases.{phase}`: 各处理阶段定义，可选值: `hot`、`warm`、`cold`、`frozen`、`delete`
- `policy.phases.{phase}.min_age`: 最小年龄，即数据存在多久进入该阶段。可用单位: `nanos`、`micros`、`ms`、`s`、`m`、`h`、`d`, 如`30d`
- `policy.phases.{phase}.actions`: 操作
- `policy.phases.{phase}.actions.rollover`: 定义下采样，即将固定时间间隔内的文档汇总/打包到单个摘要文档。通过以减小的粒度存储时序数据来减少索引占用。
- `policy.phases.{phase}.actions.allocate.number_of_replicas`: 定义分片信息
- `policy.phases.{phase}.actions.shrink`: 缩小分片
- `policy.phases.{phase}.actions.shrink.allow_write_after_shrink`: 是否允许写入
- `policy.phases.{phase}.actions.shrink.number_of_shards`: 分片数，与`max_primary_shard_size`冲突
- `policy.phases.{phase}.actions.shrink.max_primary_shard_size`: 分片大小，与`number_of_shards`冲突
- `policy.phases.{phase}.actions.forcemerge.max_num_segments`: 强制合并分段数目，清除已删除的文档
- `policy.phases.{phase}.actions.readonly`: 设置为只读
- `policy.phases.{phase}.actions.set_priority.priority`: 重启恢复优先级

获取状态
`GET _ilm/status`

获取索引生命周期状态
`GET /{index}/_ilm/explain`
- `{index}`: 索引

停止
`POST _ilm/stop`

启动
`POST _ilm/start`

获取
`GET _ilm/policy/{name}`
- `{name}`: 名称

获取全部
`GET _ilm/policy`

删除
`DELETE _ilm/policy/{name}`
- `{name}`: 模板名称

# data streams(数据流)
获取全部状态
`GET _data_stream/_stats`

获取状态
`GET _data_stream/{name}/_stats`
- `{name}`: 名称

获取
`GET _data_stream/{name}`
- `{name}`: 名称

获取全部
`GET _data_stream/policy`

删除
`DELETE _data_stream/{name}`
- `{name}`: 模板名称
