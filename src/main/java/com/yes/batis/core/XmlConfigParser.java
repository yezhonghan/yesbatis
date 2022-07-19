package com.yes.batis.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.yes.batis.bo.BaseConfiguration;
import com.yes.batis.bo.StatementMapper;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Driver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author yezhonghan
 * @title: XmlConfigParser
 * @projectName yesbatis
 * @description: 配置文件解析器
 * @date 2022/7/19 15:54
 */
public class XmlConfigParser {

    /**
     * 根据输入流解析出batis配置对象
     * @param inputStream
     * @return
     * @throws Exception
     */
    public BaseConfiguration parse(InputStream inputStream) throws Exception{
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(inputStream);
        // 获取根节点
        Element rootElement = document.getRootElement();
        // 解析出数据源配置
        DataSource dataSource = parseProperty(rootElement);
        // 生成batis配置对象，并将配置写入
        BaseConfiguration configuration = new BaseConfiguration();
        configuration.setDataSource(dataSource);
        // 解析mapper文件，并将配置写入
        HashMap<String, StatementMapper> statementMapperMap = new HashMap<>();
        parseStatementMapper(rootElement, statementMapperMap);
        configuration.setMapperMap(statementMapperMap);

        return configuration;
    }

    /**
     * 解析数据源配置
     * @param rootElement
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private DataSource parseProperty(Element rootElement) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // 获取数据源配置节点
        List<Element> propertyElementList = rootElement.elements("property");
        // 用于接收数据源配置
        Properties properties = new Properties();
        for (Element element : propertyElementList){
            // 解析数据源配置
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name, value);
        }
        // 生成并返回数据源对象
        return generateDataSource(properties);
    }

    /**
     * 解析mapper文件，并将之写入batis mapper配置
     * @param rootElement
     * @param mapperMap
     * @return
     */
    private void parseStatementMapper(Element rootElement, Map<String, StatementMapper> mapperMap) throws Exception {
        List<Element> mapperElementList = rootElement.elements("mapper");
        for (Element element : mapperElementList){
            // 获取到mapper文件路径
            String mapperUrl = element.attributeValue("resource");
            // 解析mapper文件
            XmlMapperParser xmlMapperParser = new XmlMapperParser();
            xmlMapperParser.parse(mapperUrl, mapperMap);
        }
    }

    /**
     * 根据配置生成数据源对象
     * @param properties
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private DataSource generateDataSource(Properties properties) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        DruidDataSource druidDataSource = new DruidDataSource();
        // 实例化数据库驱动
        String driverClassStr = properties.getProperty("driverClass");
        Class<?> driverClass = Class.forName(driverClassStr);
        Driver driver = (Driver) driverClass.newInstance();
        // 将配置载入数据源
        druidDataSource.setUrl(properties.getProperty("jdbcUrl"));
        druidDataSource.setDriver(driver);
        druidDataSource.setUsername(properties.getProperty("username"));
        druidDataSource.setPassword(properties.getProperty("password"));

        return druidDataSource;
    }
}
