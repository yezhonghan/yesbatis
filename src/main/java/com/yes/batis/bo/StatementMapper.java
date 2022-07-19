package com.yes.batis.bo;

/**
 * @author yezhonghan
 * @title: StatementMapper
 * @projectName yesbatis
 * @description: Mapper对象
 * @date 2022/7/19 15:57
 */
public class StatementMapper {

    /**
     * mapper唯一标识
     */
    private String id;

    /**
     * sql语句
     */
    private String sql;

    /**
     * 参数类型
     */
    private Class parameterType;

    /**
     * 返回类型
     */
    private Class resultType;

    /**
     * 操作类型
     */
    private String executeType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Class getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class parameterType) {
        this.parameterType = parameterType;
    }

    public Class getResultType() {
        return resultType;
    }

    public void setResultType(Class resultType) {
        this.resultType = resultType;
    }

    public String getExecuteType() {
        return executeType;
    }

    public void setExecuteType(String executeType) {
        this.executeType = executeType;
    }
}
