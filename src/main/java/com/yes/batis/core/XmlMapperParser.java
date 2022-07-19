package com.yes.batis.core;

import com.yes.batis.bo.ExecuteTypeEnum;
import com.yes.batis.bo.StatementMapper;
import com.yes.batis.util.ResourceUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author yezhonghan
 * @title: XmlMapperParser
 * @projectName yesbatis
 * @description: Mapper文件解析器
 * @date 2022/7/19 15:55
 */
public class XmlMapperParser {

    /**
     * 根据mapper文件路径解析出mapper对象,并写入配置对象
     * @param mapperUrl
     * @param mapperMap
     * @return
     */
    public void parse(String mapperUrl, Map<String, StatementMapper> mapperMap) throws Exception {
        InputStream resourceStream = ResourceUtil.getResourceStream(mapperUrl);
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(resourceStream);
        // 获取到mapper文件根节点
        Element rootElement = document.getRootElement();
        // 获取到命名空间
        String namespace = rootElement.attributeValue("namespace");
        // 解析出select配置
        parseElementByExecuteType(rootElement, mapperMap, namespace, ExecuteTypeEnum.SELECT.xmlTag);
        // 解析出update配置
        parseElementByExecuteType(rootElement, mapperMap, namespace, ExecuteTypeEnum.UPDATE.xmlTag);
        // 解析出insert配置
        parseElementByExecuteType(rootElement, mapperMap, namespace, ExecuteTypeEnum.INSERT.xmlTag);
        // 解析出delete配置
        parseElementByExecuteType(rootElement, mapperMap, namespace, ExecuteTypeEnum.DELETE.xmlTag);
    }

    private void parseElementByExecuteType(Element rootElement, Map<String, StatementMapper> mapperMap, String namespace, String xmlTag) throws ClassNotFoundException {
        // 根据标签类型返回对应sql操作类型
        String executeType = ExecuteTypeEnum.getExecuteType(xmlTag);
        // 解析出对应标签类型配置
        List<Element> selectElementList = rootElement.elements(xmlTag);
        for (Element element : selectElementList){
            StatementMapper statementMapper = new StatementMapper();
            // 解析出唯一标识
            String id = element.attributeValue("id");
            String sql = element.getTextTrim();
            // 解析出参数类型
            Class<?> parameterType = null;
            String parameterTypeStr = element.attributeValue("parameterType");
            if (parameterTypeStr != null && !"".equals(parameterTypeStr.trim())){
                parameterType = Class.forName(parameterTypeStr);
            }
            Class<?> resultType = null;
            // 此处根据标签类型的不同解析出不同的返回类型（update、insert、delete等标签只可返回int类型）
            if (ExecuteTypeEnum.SELECT.executeType.equals(executeType)){
                String resultTypeStr = element.attributeValue("resultType");
                resultType = Class.forName(resultTypeStr);
            }else {
                resultType = Integer.class;
            }
            // 将标签内容写入mapper对象
            String mapperId = namespace + "." + id;
            statementMapper.setId(id);
            statementMapper.setParameterType(parameterType);
            statementMapper.setSql(sql);
            statementMapper.setExecuteType(executeType);
            statementMapper.setResultType(resultType);
            mapperMap.put(mapperId, statementMapper);
        }
    }
}
