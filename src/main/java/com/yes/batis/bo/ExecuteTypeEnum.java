package com.yes.batis.bo;

/**
 * @author yezhonghan
 * @title: ExecuteTypeEnum
 * @projectName yesbatis
 * @description: SQL操作类型枚举类
 * @date 2022/7/19 15:56
 */
public enum ExecuteTypeEnum {

    SELECT("select", "SELECT"),
    UPDATE("update", "UPDATE"),
    INSERT("insert", "UPDATE"),
    DELETE("delete", "UPDATE");


    public static String getExecuteType(String xmlTag){
        ExecuteTypeEnum[] values = values();
        for (ExecuteTypeEnum executeTypeEnum : values){
            if (executeTypeEnum.xmlTag.equals(xmlTag)){
                return executeTypeEnum.executeType;
            }
        }
        return null;
    }

    public String xmlTag;

    public String executeType;

    ExecuteTypeEnum(String xmlTag, String executeType){
        this.xmlTag = xmlTag;
        this.executeType = executeType;
    }

}
