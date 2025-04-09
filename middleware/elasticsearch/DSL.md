# DSL
Domain Specific Language, 基于JSON定义的查询语言

---
# Query DSL
`GET {index}/_search`
```
{
    "_source": []
    "query": {},
    "filter": {},
    "aggs": {},
    "sort": {},
    "from": n,
    "size": m
}
```
- `{index}`: 查询的索引，可使用通配符

# _source
返回的字段列表，忽略返回全部字段。

# query
查询，判断文档与查询的匹配程度，返回最佳匹配的文档。匹配程度由`_score`字段表示。忽略匹配全部文档。
其中，值为查询条件。

# filter
过滤，判断文档是否满足查询，返回匹配的文档，不进行打分。忽略匹配全部文档。
其中，值为查询条件。

# 查询条件
`query`和`filter`内容，定义查询的条件。

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

[//]: # (TODO)

# sort
排序。

[//]: # (TODO)

# from & size
分页，类似`sql`的`offset`, `limit`。
- `from`: 偏移量
- `size`: 大小 