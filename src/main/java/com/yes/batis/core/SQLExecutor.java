package com.yes.batis.core;

import com.yes.batis.bo.BaseConfiguration;
import com.yes.batis.bo.ExecuteTypeEnum;
import com.yes.batis.bo.SQLProcessed;
import com.yes.batis.bo.StatementMapper;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 18481
 * @title: SQLExecutor
 * @projectName yesbatis
 * @description: SQL执行器
 * @date 2022/7/19 19:10
 */
public class SQLExecutor {

    private DataSource dataSource;

    private Map<String, StatementMapper> statementMapperMap;

    public SQLExecutor(BaseConfiguration configuration){
        this.dataSource = configuration.getDataSource();
        this.statementMapperMap = configuration.getMapperMap();
    }

    public <T> T executor(String mapperId, Object... params) throws Exception {
        Object result = null;
        // 获取StatementMapper对象
        StatementMapper statementMapper = statementMapperMap.get(mapperId);
        // 获取连接
        Connection connection = dataSource.getConnection();
        // 处理SQL语句
        SQLProcessed sqlProcessed = parseSQL(statementMapper.getSql(), "#{", "}");
        // 获取预处理对象
        PreparedStatement preparedStatement = connection.prepareStatement(sqlProcessed.getSql());
        // 获取要处理的参数
        List<String> paramsName = sqlProcessed.getParamsName();
        Class parameterType = statementMapper.getParameterType();
        // 处理预处理对象传参
        processPreparedStatement(preparedStatement, parameterType, paramsName, params);
        String executeType = statementMapper.getExecuteType();
        // 根据SQL操作类型执行不同操作
        if (ExecuteTypeEnum.SELECT.executeType.equals(executeType)){
            // 如果是select操作，根据返回类型获取到返回值
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Object> list = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            Class resultType = statementMapper.getResultType();
            while (resultSet.next()){
                // 根据返回类型获取实例化对象
                Object instance = resultType.newInstance();
                // 遍历获取到的列，拿到对应值
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    // 获取到列名
                    String columnName = metaData.getColumnLabel(i + 1);
                    // 根据列名称获取值
                    Object object = resultSet.getObject(columnName);
                    // 将值设置到实例化对象的对应列
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultType);
                    Method writeMethod = propertyDescriptor.getWriteMethod();
                    writeMethod.invoke(instance,object);
                }
                list.add(instance);
            }
            result = list;
        }else {
            // 如果是update等操作，返回int即可
            result = preparedStatement.executeUpdate();
        }
        return (T) result;
    }

    /**
     * 解析sql语句
     * @param sql
     * @param prefix
     * @param suffix
     */
    private SQLProcessed parseSQL(String sql, String prefix, String suffix){
        SQLProcessed sqlProcessed = new SQLProcessed();
        List<String> paramsName = new ArrayList<>();
        StringBuilder sqlBuild = new StringBuilder();

        char preChar = prefix.charAt(0);
        char sufChar = suffix.charAt(0);

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c != preChar){
                sqlBuild.append(c);
                continue;
            }
            // 匹配前缀，如果未报异常，说明匹配成功，后移下标
            i = matchStr(sql, i, prefix);
            StringBuilder paramBuild = new StringBuilder();
            for (int j = i; j < sql.length(); j++) {
                char paramChar = sql.charAt(j);
                if (paramChar != sufChar){
                    // 如果到最后都没有匹配到后缀，说明sql有问题
                    if (j == sql.length() - 1){
                        throw new RuntimeException("SQL error.");
                    }
                    // 如果未匹配到后缀，将字符拼接到参数名
                    paramBuild.append(paramChar);
                }else {
                    // 匹配到后缀首字符，进行校验，如果校验通过，后移下标。此处-1因为最后循环会+1
                    i = matchStr(sql, j, suffix) - 1;
                    // 将参数名添加到集合
                    paramsName.add(paramBuild.toString());
                    // 追加占位符到sql
                    sqlBuild.append(" ? ");
                    break;
                }
            }
        }
        sqlProcessed.setSql(sqlBuild.toString());
        sqlProcessed.setParamsName(paramsName);
        return sqlProcessed;
    }

    /**
     * 匹配字符
     * @param sql
     * @param start
     * @param s
     * @return
     */
    private int matchStr(String sql, int start, String s){
        int tail = start + s.length();
        // 如果此时根据前缀长度去匹配sql发现已经超长，抛异常
        if (tail > sql.length()){
            throw new RuntimeException("SQL error.");
        }
        // 如果匹配到了前缀第一位字符，后续就必须要全部匹配，否则跑解析异常
        for (int j = 1; j < s.length(); j++) {
            if (sql.charAt(start + j) != s.charAt(j)){
                throw new RuntimeException("SQL error.");
            }
        }
        // 如果通过了前缀校验，返回后移后的下标
        return start + s.length();
    }

    /**
     * 处理预处理对象传参
     * @param preparedStatement
     * @param parameterType
     * @param paramsName
     * @param params
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws SQLException
     */
    private void processPreparedStatement(PreparedStatement preparedStatement, Class parameterType, List<String> paramsName, Object... params) throws NoSuchFieldException, IllegalAccessException, SQLException {
        if (parameterType != null){
            if (params == null){
                throw new RuntimeException("params is null.");
            }
            // 如果定义了传参类型，说明参数就是一个对象
            Object obj = params[0];
            // 按传参顺序将对应名称的参数添加到预处理对象对应位置
            for (int i = 0; i < paramsName.size(); i++) {
                // 根据参数名称获取到Filed对象，并通过反射获取到传参对象中的对应元素
                Field declaredField = parameterType.getDeclaredField(paramsName.get(i));
                declaredField.setAccessible(true);
                Object o = declaredField.get(obj);
                // 将值添加到预处理对象
                preparedStatement.setObject(i + 1, o);
            }
        }else {
            // 否则，按照顺序将传参添加到预处理对象即可
            // 如果传参数量和sql要求数量无法对应
            if (params != null && params.length != paramsName.size()){
                throw new RuntimeException("sql is error");
            }
            if (params != null){
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }
        }
    }

}
