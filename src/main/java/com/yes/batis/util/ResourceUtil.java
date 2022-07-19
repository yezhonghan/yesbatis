package com.yes.batis.util;

import java.io.InputStream;

/**
 * @author yezhonghan
 * @title: ResourceUtil
 * @projectName yesbatis
 * @description: TODO
 * @date 2022/7/19 15:53
 */
public class ResourceUtil {

    /**
     * 根据路径获取流
     * @param path
     * @return
     */
    public static InputStream getResourceStream(String path){
        return ResourceUtil.class.getClassLoader().getResourceAsStream(path);
    }
}
