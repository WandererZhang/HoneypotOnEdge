package serialize;

/**
 * @author 78445
 * @description 序列化接口类
 * @date 23:42 2021/2/3
 **/
public interface Serializer {
    /**
     * java对象转二进制
     * @author 78445
     * @date 23:43 2021/2/3
     * @param object
     * @return byte[]
     **/
    byte[] serialize(Object object);
    /**
     * 二进制转java对象
     * @author 78445
     * @date 23:45 2021/2/3
     * @param clazz
     * @param bytes
     * @return T
     **/
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
