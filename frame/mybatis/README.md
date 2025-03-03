# mybatis
[文档](https://mybatis.org/mybatis-3/zh_CN/index.html)
[github](https://github.com/baomidou/mybatis-3)
[MybatisApplication.java](src/main/java/com/example/MybatisApplication.java)

## 使用方式
### java注解
> 可使用java注解定义mapper，仅能提供简易的功能

基本使用: [annotation](src/main/java/com/example/annotation/AnnotationMain.java)
[文档](https://mybatis.org/mybatis-3/zh_CN/java-api.html)

### mapper.xml
> 可使用xml定义mapper，提供完整的功能

基本使用: [base](src/main/java/com/example/base/BaseMain.java)
[文档](https://mybatis.org/mybatis-3/zh_CN/sqlmap-xml.html)

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "https://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="...">

</mapper>
```

NOTE:
1. xml与java接口目录层级需一样，但是xml根目录为`resources`, java接口根目录为`java`
2. namespace(命名空间)为java接口引用，必须指定, 这样可以进行接口绑定

## 枚举处理
### EnumTypeHandler
默认，采用名称读写枚举
### EnumOrdinalTypeHandler
采用`Enum.ordinal()`读写枚举，需指定javaType
1. xml [UserMapper.xml](src/main/resources/com/example/base/UserMapper.xml)
   参数: 
   ```
   #{user.gender,javaType=com.example.base.Gender,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}
   ```
   结果: 
    ```
   <result column="gender" property="gender" javaType="com.example.base.Gender" typeHandler="org.apache.ibatis.type.EnumOrdinalTypeHandler"/>
   ```
2. java [UserMapper.java](src/main/java/com/example/annotation/UserMapper.java)
   参数: 同xml
   结果:
   ```
   @Results(@Result(javaType = Gender.class, typeHandler = EnumOrdinalTypeHandler.class, column = "gender", property = "gender"))
   ```

## 写
insert、update、delete进行写操作。

其中返回内容为变更行数。

### 自动生成字段(自增主键等)
useGeneratedKeys: （仅适用于insert和update）这会令MyBatis使用JDBC的getGeneratedKeys方法来取出由数据库内部生成的主键（比如：像MySQL和SQL Server这样的关系型数据库管理系统的自动递增字段），默认值：false。
keyProperty: （仅适用于insert和update）指定能够唯一识别对象的属性，MyBatis会使用getGeneratedKeys的返回值或insert语句的selectKey子元素设置它的值，默认值：未设置（unset）。如果生成列不止一个，可以用逗号分隔多个属性名称。
keyColumn: （仅适用于insert和update）设置生成键值在表中的列名，在某些数据库（像PostgreSQL）中，当主键列不是表中的第一列的时候，是必须设置的。如果生成列不止一个，可以用逗号分隔多个属性名称。

使用`@Options`定义属性，内容同mapper.xml

## 读
select进行读操作。

其中返回内容为读取内容。

### resultMap 结果映射
- `<constructor/>`: 构造方法, 将会调用相同参数的构造方法创建返回对象
    - `<idArg/>`: 同`<id/>`
    - `<arg/>`: 同`<result/>`
- `<id/>`: 显示标记出作为id的元素可提高性能
- `<result/>`: 一般字段
- `<association/>`: 对象映射
- `<collection/>`: 集合映射
- `<discriminator/>`: 泛型映射: 根据列值进行不同的结果映射
    - `<case/>`: 判断类型

#### 关联(`<association/>`、`@One`)/集合(`<collection/>`、`@Many`)
##### 嵌套select查询
执行另一个select查询获取结果
- column: 原查询的字段, 可使用`{prop1=col1,prop2=col2}`语法传递多个值及设置参数名称
- select: 使用的`<select/>`查询的id
- fetchType: lazy-懒加载，eager-积极加载

##### 嵌套结果映射
从当前结果集中选取字段映射, 标签内元素同resultMap
- resultMap: 引用的resultMap
- columnPrefix: 字段前缀

#### 鉴别器(`<discriminator/>`、`@TypeDiscriminator`)
```xml
<discriminator javaType="int" column="u_gender">
    <case value="0" resultMap="man"/>
    <case value="1" resultMap="woman"/>
</discriminator>
```
当`u_gender`为`0`时使用id为`man`的resultMap
当`u_gender`为`1`时使用id为`woman`的resultMap

NOTE:
**使用鉴别器时，该resultMap的其他标签将会忽略**

## 插件
基本使用: [PluginMain.java](src/main/java/com/example/plugin/PluginMain.java)
插件: [Plugin.java](src/main/java/com/example/plugin/Plugin.java)

```
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class Plugin implements Interceptor{

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // TODO
        return invocation.proceed();
    }
}
```
1. 创建类实现`org.apache.ibatis.plugin.Interceptor`接口，实现`intercept`处理具体逻辑
2. 添加`org.apache.ibatis.plugin.Intercepts`注解
3. 在`org.apache.ibatis.plugin.Intercepts`注解添加`org.apache.ibatis.plugin.Signature`注解，表示需要拦截的方法
   1. type为具体类，可选值为: `org.apache.ibatis.executor.Executor`、`org.apache.ibatis.executor.parameter.ParameterHandler`、`org.apache.ibatis.session.ResultHandler`、`org.apache.ibatis.executor.statement.StatementHandler`
   2. method为方法名称，args为参数。两者共同确定拦截的方法
4. Spring注册该类为bean

