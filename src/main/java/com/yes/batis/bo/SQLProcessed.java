package com.yes.batis.bo;

import java.util.List;

/**
 * @author 18481
 * @title: SQLProcessed
 * @projectName yesbatis
 * @description: 处理后SQL对象
 * @date 2022/7/19 19:22
 */
public class SQLProcessed {

    private String sql;

    private List<String> paramsName;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<String> getParamsName() {
        return paramsName;
    }

    public void setParamsName(List<String> paramsName) {
        this.paramsName = paramsName;
    }
}
