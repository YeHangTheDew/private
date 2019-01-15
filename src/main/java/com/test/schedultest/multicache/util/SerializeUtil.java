package com.test.schedultest.multicache.util;
import com.xuanwu.apaas.core.utils.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created with IDEA
 * author:yechh
 * Date:2019/1/15
 * Time:9:39
 */



public class SerializeUtil {
    /**
     * 序列化
     *
     * @param
     * @return
     */
    public static String serialize(Object obj) {

        String result = null;
        try {
            result = JsonUtil.serialize(obj);
        } catch(Exception e) {
            result = obj == null ? "空值" : obj.toString();
        }
        return result;

    }

    /**
     * 反序列化
     *
     * @param
     * @return
     */
    public static Object deserialize(String str,Class clazz) {

        Object result = null;
        try {
            if(clazz == JSONObject.class) {
                result = new JSONObject(str);
            } else if(clazz == JSONArray.class) {
                result = new JSONArray(str);
            } else {
                result = JsonUtil.deserialize(str,clazz);
            }
        } catch(Exception e) {
        }
        return result;

    }

    //反序列化,支持List<xxx>
    public static Object deserialize(String str,Class clazz,Class elementClass) {

        Object result = null;
        try {
            if(clazz == JSONObject.class) {
                result = new JSONObject(str);
            } else if(clazz == JSONArray.class) {
                result = new JSONArray(str);
            } else {
                result = JsonUtil.deserialize(str,clazz,elementClass);
            }
        } catch(Exception e) {
        }
        return result;

    }
}