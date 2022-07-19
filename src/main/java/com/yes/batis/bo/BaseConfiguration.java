package com.yes.batis.bo;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author yezhonghan
 * @title: BaseConfiguration
 * @projectName yesbatis
 * @description: 基础配置对象
 * @date 2022/7/19 15:56
 */
public class BaseConfiguration {

    /**
     * 数据源
     */
    private DataSource dataSource;

    /**
     * mapper解析集合
     */
    Map<String, StatementMapper> mapperMap;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, StatementMapper> getMapperMap() {
        return mapperMap;
    }

    public void setMapperMap(Map<String, StatementMapper> mapperMap) {
        this.mapperMap = mapperMap;
    }
}
