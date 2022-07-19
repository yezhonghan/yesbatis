package com.yes.batis.core;

import com.yes.batis.bo.BaseConfiguration;
import com.yes.batis.bo.ExecuteTypeEnum;
import com.yes.batis.bo.StatementMapper;

import java.lang.reflect.*;
import java.util.List;

/**
 * @author yezhonghan
 * @title: DefaultSqlSession
 * @projectName yesbatis
 * @description: TODO
 * @date 2022/7/19 17:11
 */
public class DefaultSqlSession implements SqlSession{

    private BaseConfiguration configuration;

    public DefaultSqlSession(BaseConfiguration configuration){
        this.configuration = configuration;
    }

    @Override
    public <T> T query(String mapperId, Object... params) throws Exception {
        List<T> list = list(mapperId, params);
        if (list.size() > 1){
            throw new RuntimeException("return rows over 1.");
        }
        return list.get(0);
    }

    @Override
    public <T> List<T> list(String mapperId, Object... params) throws Exception {
        SQLExecutor sqlExecutor = new SQLExecutor(configuration);
        List<T> list = sqlExecutor.executor(mapperId, params);
        return list;
    }

    @Override
    public int update(String mapperId, Object... params) throws Exception {
        SQLExecutor sqlExecutor = new SQLExecutor(configuration);
        return sqlExecutor.executor(mapperId, params);
    }

    @Override
    public <T> T getMapper(Class clazz) {
        // 生成代理对象，使得每次调用clazz对象方法时，都会执行invoke方法
        Object o = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 获取方法名和类名
                String name = method.getName();
                String className = method.getDeclaringClass().getName();
                // 拼接id，获取到StatementMapper对象（这里就要求命名空间和类名一致，mapper标签id和方法名一致）
                String mapperId = className + "." + name;
                StatementMapper statementMapper = configuration.getMapperMap().get(mapperId);
                // 如果是select标签，执行query或者list方法
                if (ExecuteTypeEnum.SELECT.executeType.equals(statementMapper.getExecuteType())){
                    // 获取到返回值类型，如果实现了泛型，那么可以认为是查询list集合
                    Type returnType = method.getGenericReturnType();
                    if (returnType instanceof ParameterizedType){
                        return list(mapperId, args);
                    }
                    return query(mapperId, args);
                }
                // 否则，执行update方法
                return update(mapperId, args);
            }
        });
        return (T) o;
    }
}
