# Query DSL
`GET {index}/_search`
```
{
  "fields": [],
  "script_fields": [],
  "_source": [],
  "collapse": {},
  "highlight": {},
  "query": {},
  "filter": {},
  "indices_boost": [],
  "aggs": {},
  "sort": {},
  "from": n,
  "size": m,
  "search_after": []
}
```
- `_source`、`fields`、`script_fields`: 返回的字段列表，默认返回全部字段。类似`SELECT`
  - `_source`: 仅原始字段
  - `fields`: 可对字段进行格式化
  - `script_fields`: 可进行方法计算值
- `collapse`: 去重。类似`DISTINCT`
- `highlight`: 高亮字段，可添加`html标签`将重要的字词进行标注。
- `{index}`: 查询的索引，可使用通配符。类似`FROM`
- `query`, `filter`: 筛选数据(`query`进行打分, `filter`不打分)，默认返回全部。类似`WHERE`
- `indices_boost`: 对于多索引情况，可为不同索引分配得分权重，默认全部1.0。
- `aggs`: 聚合。类似`GROUP BY`。
- `sort`: 排序。类似`ORDER BY`。
- `from`, `size`: 分页，`size`默认10，默认最大值10000。类似`LIMIT`
- `search_after`: 分页，根据上次查询结果取下一页，可优化查询

返回(仅通用数据):
```
{
  "took": 0,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  }
}
```
- `took`: 执行毫秒数
- `timed_out`: 是否超时
- `_shards`: 分片
- `_shards.total`: 总分片数
- `_shards.successful`: 成功的分片数
- `_shards.skipped`: 忽略的分片数
- `_shards.failed`: 失败的分片数

# query & filter
- `query`: 查询，判断文档与查询的匹配程度，返回最佳匹配的文档。匹配程度由`_score`字段表示。忽略匹配全部文档。
- `filter`: 过滤，判断文档是否满足查询，返回匹配的文档，不进行打分。忽略匹配全部文档。
其中，值为查询条件。

请求:
```
{
  "query": {}
}
```

返回(忽略无关数据): 
```
{
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
  }
}
```
- `hits.total.value`: 匹配查询的文档总数
- `hits.total.relation`: 结果精确(`eq`)、只是部分(`gte`)
- `hits.max_score`: 匹配文档最大得分
- `hits.hits`: 匹配的文档
- `hits.hits._index`: 匹配的文档索引
- `hits.hits._id`: 匹配的文档id
- `hits.hits._score`: 匹配的文档得分
- `hits.hits._source`: 匹配的文档内容
- `hits.hits.fields`: 匹配的文档内容, 由`fields`、`script_fields`、`docvalue_fields`、`stored_fields`指定

## leaf query clauses
基本查询语句。

### full text
全文查询，分析文本内容(将文本内容进行分词处理，将结构进行比较)。

#### match & match_phrase & match_phrase_prefix
单字段匹配
- `match`: 将文本和查询条件进行分词，比较两者分词内容
- `match_phrase`: 将文本和查询条件进行分词及短语，比较两者分词及短语内容
- `match_phrase_prefix`: 同`match_phrase`, 最后一个词为前缀查询
```
"match": { 
    "{field}": "{value}"
}
```
```
"match_phrase": { 
    "{field}": "{value}"
}
```
```
"match": { 
    "{field}": {
        "query": "{value}",
        "boost": "{score}"
    }
}
```
- `{field}`: 字段
- `{value}`: 值
- `{score}`: 打分

#### multi_match
多字段匹配，可设定权重
```
"multi_match": { 
    "query": "{value}",
    "type": "{type}",
    "fields": ["{field}", "{field}^{n}"]
}
```
- `{field}`: 字段
- `{value}`: 值
- `multi_match.type`: 类型
  - `best_fields`: 使用`match`, 匹配的最高分
  - `most_fields`: 分数加和
  - `phrase`/`phrase_prefix`: 使用`match_phrase`/`match_phrase_prefix`, 匹配最高分
  - `cross_fields`: 拼接字段后查询
  - `bool_prefix`: `bool`+`match`
- `{n}`: 权重

#### query_string & simple_query_string
可使用查询字符串进行查询
- `query_string`: 较为复杂版本, 可使用`AND`|`OR`|`NOT`等连接条件
- `simple_query_string`: 较为简单版本, 可使用`+`|`-`等判断条件
```
"query_string": { 
    "query": "{value}",
    "fields": ["{field}", "{field}^{n}"]
}
```
```
"query_string": { 
    "query": "{value}",
    "fields": ["{field}", "{field}^{n}"]
}
```
- `{field}`: 字段
- `{value}`: 值
- `{n}`: 权重

#### 其他
- `intervals`: 根据顺序及匹配程度匹配
- `match_bool_prefix`: 根据查询条件分词进行`bool`+`term`查询
- `combined_fields`: 同`multi_match`的`cross_fields`, 但仅支持`text`类型且分词规则相同。

### term-level
精确查找结构话数据, 不分析文本内容。

#### exists
是否存在字段
```
"exists": {
    "field": "{field}"
}
```

#### range
范围查询
```
"range": {
    "{field}": {
        "gt": "{value}",
        "lt": "{value}",
        "boost": "{score}"
    }
}
```
- `gt`: 大于
- `gte`: 不小于
- `lt`: 小于
- `lte`: 不大于
- `{field}`: 字段
- `{value}`: 值
- `{score}`: 打分

#### term & terms & terms_set
精确匹配值，若为数组只需存在一条即可。
- `term`: 提供一个值, 满足即可。
- `terms`: 提供多个值，满足其中一个即可。
- `terms_set`: 提供多个值，按配置满足数量。

> 请勿匹配`text`类型的值
```
"term": {
    "{field}": "{value}"
}
```
```
"term": {
    "{field}": {
        "value": "{value}",
        "boost": "{score}"
    }
}
```
```
"terms": {
    "{field}": ["{value}", "{value}"]
}
```
```
"terms_set": {
    "{field}": {
        "terms": ["{value}", "{value}"],
        "minimum_should_match": {n}
    }
}
```
- `terms_set.{field}.minimum_should_match`: 满足数量, 可选数值或百分比，正数表示最少满足的数量，负数表示最多不满足的数量
- `{field}`: 字段
- `{value}`: 值
- `{score}`: 打分

#### fuzzy
模糊匹配，可处理误输入情况。如:
- 变更 (box → fox)
- 移除 (black → lack)
- 添加 (sic → sick)
- 换位 (act → cat)
```
"fuzzy": {
    "{field}": {
        "value": "{value}",
        "max_expansions": 50
    }
}
```
- `max_expansions`: 最大变更数，默认50
- `{field}`: 字段
- `{value}`: 值


#### 其他
- `match_all`: 匹配全部文档
- `ids`: 根据`_id`字段查询，可提供多个值
- `prefix`: 查询是否包含前缀
- `regexp`: 正则
- `wildcard`: 通配符
- `script`: 使用`script`进行匹配
- `script_score`: 使用`script`进行打分
- `rank_feature`: (仅`rank_feature`和`rank_features`类型)，对文档提高该字段的分数
- `wrapper`: 将指定查询按base64解码后使用
- `span_term`: 跨度查询，对文本的位置、距离进行控制，用于其他跨度查询的组合
- `geo_bounding_box`: (仅`geo_point`和`geo_shape`类型)，匹配正方形范围内的坐标(左上和右下坐标)
- `geo_distance`: (仅`geo_point`和`geo_shape`类型)，匹配圆形范围内的坐标(圆心和距离)
- `geo_shape`: (仅`geo_point`和`geo_shape`类型)，匹配自定义范围内的坐标
- `shape`: (仅`shape`类型)，匹配自定义范围内的坐标
- `parent_id`: (仅`join`类型)，根据父文档id查询子文档
- `distance_feature`: (仅`date`, `date_nanos`, `geo_point`类型), 提升对于目标点指定距离内的打分
- `more_like_this`: 查找与提供文档相识的文档
- `percolate`: 查找可匹配提供文档对应的查询文档(即查询条件存储于目标索引中，使用该索引的文档中的条件查询提供的文档判断匹配程度)
- `knn`: (仅`dense_vector`类型), 依据knn算法，计算与目标向量最接近的向量
- `sparse_vector`:
- `semantic`: (测试阶段)

## Compound query clauses
复合查询语句，由一到多个基本查询语句组合或调整而成。

### bool
布尔组合查询。逻辑判断。

步骤: 
1. 通过`must`、`filter`、`must_not`过滤文档
2. 根据`must`和`should`调整分数

- `must`: 匹配全部并打分，过滤结果
- `should`: 匹配部分并打分，仅提高分数不过滤
- `filter`: 匹配全部但不打分，仅过滤不提高分数
- `must_not`: 不匹配，过滤结果
```
"bool": {
    "must": [],
    "should": [],
    "filter": [],
    "must_not": []
}
```

### boosting
打分查询。调整打分。

步骤:
1. 根据`positive`过滤文档
2. 根据`negative`降低`negative_boost`权重的分数

```
"boosting": {
  "positive": {},
  "negative": {},
  "negative_boost": {n}
}
```
- `positive`: 匹配的文档
- `negative`: 降低分数的文档
- `negative_boost`: 降低分数值, 取值[0-1]的小数

### constant_score
常量分数。给予相同评分。

步骤:
1. 根据`filter`过滤文档
2. 给予所有文档`boost`分数

```
"constant_score": {
  "filter": {},
  "boost": {n}
}
```
- `filter`: 查询
- `boost`: 分数

### dis_max
取最高分。

步骤: 
1. 根据`queries`过滤文档并打分
2. 取 `最高分*1 + sum(非最高分)*tie_breaker` 作为最后得分

```
"dis_max": {
  "queries": [],
  "tie_breaker": {n}
}
```
- `queries`: 查询的语句
- `tie_breaker`: 非最高分的查询分数权重, 取值[0-1]的小数, 默认0

### 其他
- `function_score`: 定义分数的数学运算。
- `nested`: (仅`nested`类型), 查询`nested`的数据, 返回源数据
- `has_child`: (仅`join`类型), 查询`join`子文档的数据, 返回父文档
- `has_parent`: (仅`join`类型), 查询`join`父文档的数据, 返回子文档
- `pinned`: 提高特定id文档指定分数
- `span`: 跨度查询，对其他跨度查询的位置及距离进行匹配
  - `span_multi`: 匹配多个跨度查询
  - `span_first`: 匹配指定跨度查询距离起始点的位置
  - `span_near`: 匹配提供跨度查询间最大距离
  - `span_or`: 匹配多个跨度查询中最少一个
  - `span_not`: 匹配指定的跨度查询及排除指定的跨度查询
  - `span_containing`: 匹配指定的跨度优先级, 同`span_within`
  - `span_within`: 匹配指定的跨度优先级, 同`span_containing`

# aggs
聚合。

请求:
```
{
  "size": 0,
  "aggs": {
    "{name}": {
      "{metricAgg}": {}
    },
    "{name2}": {
      "{bucketAgg}": {},
      "aggs": {
        "{name21}": {}
      }
    },
    "{name3}": {
      "{pipelineAgg}": {
        "buckets_path": ""
      }
    }
  }
}
```
- `size`: 表示无需返回命中文档。
- `aggs`: 聚合
- `aggs.{name}`: 聚合，`{name}`为该聚合自定义名称，显示在返回结果中。可以有多个。
- `aggs.{name}.{metricAgg}`: 度量值聚合，内容为参数
- `aggs.{name}.{bucketAgg}`: 桶聚合，内容为参数
- `aggs.{name}.aggs`: 桶聚合后的子聚合，内容同顶级`aggs`, 将各同视为一个索引分别进行子聚合
- `aggs.{name}.{pipelineAgg}`: 管道聚合，内容为参数
- `aggs.{name}.{pipelineAgg}.buckets_path`: 管道聚合路径，即被聚合的路径

返回(忽略无关数据):
```
{
  "aggregations": {
    "{name}": {
    
    },
    "{name2}": {
      "buckets": [
        "key": "",
        "doc_count": 0,
        "{name21}": {
        
        }
      ]  
    }
  }
}
```
- `aggregations.{name}`: 第一个聚合，内容为结果
- `aggregations.{name2}`: 第二个聚合，内容为结果
- `aggregations.{name2}.buckets`: 第二个聚合各个桶
- `aggregations.{name2}.buckets.key`: 第二个聚合桶的各个key
- `aggregations.{name2}.buckets.doc_count`: 第二个聚合桶的各个文档数量
- `aggregations.{name2}.buckets.{name21}`: 第二个聚合桶的各个子聚合

## 度量(Metric)
将文档中提取值进行聚合。

### 数值分析

单值聚合(结果为单值):
- `avg`: 平均值
- `weighted_avg`: 加权平均值
- `sum`: 总和
- `max`: 最大值
- `min`: 最小值
- `median_absolute_deviation`: 中值绝对离差

多值聚合(结果为多值):
- `percentile_ranks`: 百分位排名，根据提供的值获取该值对应的百分位
- `percentiles`: 百分位，根据提供的百分位获取对应的值，默认获取`[ 1, 5, 25, 50, 75, 95, 99]`百分位
- `stats`: 统计。包含`value_count`(结果名称为`count`)、`min`、`max`、`avg`、`sum`
- `extended_stats`: 扩展统计。包含`value_count`(结果名称为`count`)、`min`、`max`、`avg`、`sum`、`sum_of_squares`(平方和)、`variance`(方差)、`std_deviation`(标准差)、`std_deviation_bounds`(与平均值指定标准差值，默认2个)
- `boxplot`: 箱线图。包含`min`、`max`、`25百分点`(结果名称为`q1`)、`50百分点`(结果名称为`q2`)、`75百分点`(结果名称为`q3`)

相关性(两个字段相关性):
- `t_test`: 相关性判断
- `matrix_stats`: 矩阵统计。分别统计每个字段的`value_count`(结果名称为`count`)、`avg`(结果名称为`mean`)、`variance`(方差)、`skewness`(偏度)、`kurtosis`(峰度)及各字段间的`covariance`(相关性)和`correlation`(相关性(缩放到`[-1, 1]`))

### 计数
- `value_count`: 不去重计数
- `cardinality`: 去重(基数)计数

### 地理位置 / 坐标

地理位置:
- `geo_bounds`: (仅`geo_point`和`geo_shape`类型), 取包含所有值的矩形范围
- `geo_centroid`: (仅`geo_point`类型), 加权质心
- `geo_line`: (仅`geo_point`类型), 根据指定的另一个字段表示的顺序合并成线

坐标:
- `cartesian_bounds`: (仅`point`和`shape`类型), 取包含所有值的矩形范围
- `cartesian_centroid`: (仅`point`类型), 加权质心

### 子聚合
- `rate`: (仅在`date_histogram`和`composite`聚合中使用)，表示该桶所占比例
- `top_hits`: 取最符合的匹配文档

### 其他
- `top_metrics`: 按指定字段排序后返回最大或最小文档的特定字段值
- `scripted_metric`: 使用`script`聚合
- `string_stats`: 字符串统计。包含`value_count`(结果名称为`count`)、`min_length`(最小长度)、`max_length`(最大长度)、`avg_length`(平均长度)、`entropy`(熵)

## 桶(Bucket)
将文档进行分组，并允许使用子聚合。

### 按唯一值
- `terms`: 按唯一值分组
- `multi_terms`: 按多个值的笛卡尔积组合分组

### 按范围
- `range`: 按范围分组
- `date_range`: 按范围分组，仅支持日期类型的`range`，支持使用日期表达式
- `ip_range`: 按范围分组，仅支持ip类型的`range`，支持使用掩码
- `ip_prefix`: 按前缀分组，将前n位相同的ip放入同一个桶
- `histogram`: 柱状图，按指定间隔分组
- `variable_width_histogram`: 柱状图，指定数量自动设置间隔的`histogram`
- `date_histogram`: 柱状图，仅支持日期类型的`histogram`，支持使用日期表达式
- `auto_date_histogram`: 柱状图，指定数量自动设置间隔的`date_histogram`

### 按查询条件
- `filter`: 使用查询过滤文档
- `filters`: 使用多个查询，对匹配的文档放入一个桶
- `adjacency_matrix`: 邻接矩阵聚合，将多个查询条件进行组合，对所有组合匹配的文档放入同一个桶

### 其他
- `categorize_text`: 对半结构化的数据进行分类
- `global`: 所有数据，不受`query`及`filter`影响，仅支持作为顶级聚合
- `missing`: 缺少需要字段或该字段为`null`的桶
- `composite`: 复合分组，将多个分组规则进行笛卡尔积分组

## 管道(Pipeline)
从其他聚合中输入，即聚合其他聚合的结果

计算值
- `max_bucket`: 取最大值的桶key及对应值
- `min_bucket`: 取最小值的桶key及对应值
- `sum_bucket`: 计算总和
- `avg_bucket`: 计算平均值
- `stats_bucket`: 计算统计。包含`value_count`(结果名称为`count`)、`min`、`max`、`avg`、`sum`
- `extended_stats_bucket`: 计算扩展统计。包含`value_count`(结果名称为`count`)、`min`、`max`、`avg`、`sum`、`sum_of_squares`(平方和)、`variance`(方差)、`std_deviation`(标准差)、`std_deviation_bounds`(与平均值指定标准差值，默认2个)
- `percentiles_bucket`: 百分位，根据提供的百分位获取对应的值，默认获取`[ 1, 5, 25, 50, 75, 95, 99]`百分位
- `bucket_script`: 使用`script`聚合

计算连续桶(用于连续桶聚合的子聚合)
- `cumulative_cardinality`: (仅`histogram`系列)，计算累计基数(从范围开始到该桶的总基数)
- `cumulative_sum`: (仅`histogram`系列)，计算累计总和(从范围开始到该桶的总和)
- `derivative`: (仅`histogram`系列)，计算导数
- `moving_fn`: 滑动窗口函数，通过定义的函数计算滑动窗口结果
- `moving_percentiles`: 滑动窗口百分位，通过定义百分位计算滑动窗口结果
- `serial_diff`: 序列差分

改变集合桶(一般用于子聚合)
- `bucket_selector`: 通过`script`判断提供的聚合是否保留在父聚合中
- `bucket_sort`: 对桶进行排序

计算桶间关系
- `normalize`: 规范化，即将值分布到指定范围
- `bucket_correlation`: 相关性
- `bucket_count_ks_test`: Kolmogorov–Smirnov test
- `inference`: 对聚合进行推理

# sort
排序。
```
{
  "sort": [
    {"{field}": {"order": "asc"}}
    {"{fields}": {"order": "asc", "mode": "min"}}
  ]
}
```
- `{field}`: 排序字段, 按顺序依次排列
- `{field}.order`: 排序顺序, 可选`asc`、`desc`
- `{field}.mode`: 多值字段取值方法，可选`min`、`max`、`sum`、`avg`、`median`

# from & size & search_after
分页。
- `from`: 偏移量
- `size`: 大小 
- `search_after`: 提供上一页的最后排序结果值快速获取下一页