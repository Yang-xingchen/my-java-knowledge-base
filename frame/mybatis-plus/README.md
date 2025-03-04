# mybatis
[官网](https://baomidou.com/)
[文档](https://baomidou.com/introduce/)
[github](https://github.com/baomidou/mybatis-plus)
[MybatisPlusApplication.java](src/main/java/com/example/MybatisPlusApplication.java)

# 基础使用
1. `mapper`接口继承`BaseMapper<T>`即可使用mybatis-plus功能。[UserMapper.java](src/main/java/com/example/mapper/UserMapper.java)
2. 实体类添加`@TableName`、`@TableId`、`@TableField`注解。[User.java](src/main/java/com/example/entry/User.java)

## CRUD
使用`insert`、`deleteXXX`、`updateXXX`、`selectXXX`进行。
[BaseMain.java](src/main/java/com/example/service/BaseMain.java)

## Wrapper
- QueryWrapper/UpdateWrapper: 字符串字段查询(查询、删除)/更新操作。[WrapperMain.java](src/main/java/com/example/service/WrapperMain.java)
- LambdaQueryWrapper/LambdaUpdateWrapper: 类字段查询(查询、删除)/更新操作。[WrapperLambdaMain.java](src/main/java/com/example/service/WrapperLambdaMain.java) [LambdaWrapperMain.java](src/main/java/com/example/service/LambdaWrapperMain.java)

## ActiveRecord
可以直接使用实体对象操作。
**依然需要定义`Mapper`文件，即便不在代码中显式使用**
- [ActiveRecordUser.java](src/main/java/com/example/entry/ActiveRecordUser.java)
- [ActiveRecordMain.java](src/main/java/com/example/service/ActiveRecordMain.java)

# 扩展插件
mybatis提供的插件，需要手动配置开启
```java
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(xxx);
        return interceptor;
    }
```
**部分插件需添加依赖**
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-jsqlparser</artifactId>
    <version>3.5.10</version>
</dependency>
```
- TenantLineInnerInterceptor: 多租户。[TenantMain.java](src/main/java/com/example/service/TenantMain.java)
- PaginationInnerInterceptor: 分页。[PageMain.java](src/main/java/com/example/service/PageMain.java)
- DynamicTableNameInnerInterceptor: 动态表名。[DynamicTableMain.java](src/main/java/com/example/service/DynamicTableMain.java)
- OptimisticLockerInnerInterceptor: 乐观锁。[LockMain.java](src/main/java/com/example/service/LockMain.java)
- BlockAttackInnerInterceptor: 防止全表更新或删除。
- DataPermissionInterceptor: 数据权限。
- ReplacePlaceholderInnerInterceptor。

# SqlInjector
自定义全局方法
1. 创建方法类，继承`com.baomidou.mybatisplus.core.injector.AbstractMethod`, 并实现对应SQL脚本代码。该类对应一个方法。[MyAbstractMethod.java](src/main/java/com/example/config/MyAbstractMethod.java)
2. 创建配置类，继承`com.baomidou.mybatisplus.core.injector.DefaultSqlInjector`，注入创建的方法类。[MySqlInjector.java](src/main/java/com/example/config/MySqlInjector.java)
3. 创建接口，继承`com.baomidou.mybatisplus.core.mapper.BaseMapper`, 并增加方法类对应的方法。[InjectorMapper.java](src/main/java/com/example/mapper/InjectorMapper.java)
4. 业务接口即可继承该接口，调用对应方法。
