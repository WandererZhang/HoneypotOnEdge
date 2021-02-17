package com.honeypot.serialize;

/**
 * @author 78445
 * 序列化接口类
 * @date 23:42 2021/2/3
 **/
public interface Serializer {
    /**
     * java对象转二进制
     *
     * @param object
     * @return byte[]
     * @author 78445
     * @date 23:43 2021/2/3
     **/
    byte[] serialize(Object object);

    /**
     * 二进制转java对象
     *
     * @param clazz
     * @param bytes
     * @return T
     * @author 78445
     * @date 23:45 2021/2/3
     **/
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
