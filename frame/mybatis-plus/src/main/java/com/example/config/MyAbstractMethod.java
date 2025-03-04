package com.example.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.stream.Collectors;

public class MyAbstractMethod extends AbstractMethod {

    public static final String SQL = """
            <script>
            INSERT INTO %s
                (%s)
            VALUES
            <foreach collection="items" item="item" separator=",">
                (%s)
            </foreach>
            </script>
            """;

    public MyAbstractMethod() {
        super("insertBatch");
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String columns = tableInfo.getFieldList().stream().map(TableFieldInfo::getColumn).map(s -> "`" + s + "`").collect(Collectors.joining(", "));
        String values = tableInfo.getFieldList().stream().map(TableFieldInfo::getProperty).map(s -> "#{item." + s + "}").collect(Collectors.joining(", "));
        String sql = String.format(SQL, tableInfo.getTableName(), columns, values);
        SqlSource sqlSource = createSqlSource(configuration, sql, modelClass);

        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                /* 自增主键 */
                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
                keyProperty = tableInfo.getKeyProperty();
                // 去除转义符
                keyColumn = SqlInjectionUtils.removeEscapeCharacter(tableInfo.getKeyColumn());
            } else if (null != tableInfo.getKeySequence()) {
                keyGenerator = TableInfoHelper.genKeyGenerator(methodName, tableInfo, builderAssistant);
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            }
        }
        return addInsertMappedStatement(mapperClass, modelClass, sqlSource, keyGenerator, keyProperty, keyColumn);
    }

}
