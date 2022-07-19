package com.yes.batis.core;

import java.util.List;

/**
 * @author yezhonghan
 * @title: SqlSession
 * @projectName yesbatis
 * @description: SqlSession接口
 * @date 2022/7/19 16:59
 */
public interface SqlSession {

    <T> T query(String mapperId, Object... params) throws Exception;

    <T> List<T> list(String mapperId, Object... params) throws Exception;

    int update(String mapperId, Object... params) throws Exception;

    <T> T getMapper(Class clazz);
}
