package serialize;

import com.alibaba.fastjson.JSON;

/**
 * @author 78445
 */
public class SerializerImpl implements Serializer{
    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes,clazz);
    }
}
