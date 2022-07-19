package com.yes.batis.core;

import com.yes.batis.bo.BaseConfiguration;
import com.yes.batis.util.ResourceUtil;

import java.io.InputStream;

/**
 * @author yezhonghan
 * @title: SqlSessionFactory
 * @projectName yesbatis
 * @description: SqlSession工厂对象，用于读取配置，打开sql会话窗口
 * @date 2022/7/19 16:59
 */
public class SqlSessionFactory {

    private BaseConfiguration configuration;

    public SqlSessionFactory() throws Exception {
        this("yesbatis.xml");
    }

    public SqlSessionFactory(String configPath) throws Exception {
        XmlConfigParser xmlConfigParser = new XmlConfigParser();
        // 根据配置文件路径加载所有配置
        InputStream resourceStream = ResourceUtil.getResourceStream(configPath);
        this.configuration = xmlConfigParser.parse(resourceStream);
    }

    public SqlSession openSqlSession() throws Exception {
        return new DefaultSqlSession(configuration);
    }


}
