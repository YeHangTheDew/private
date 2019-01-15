package com.test.schedultest.multicache.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created with IDEA
 * author:yechh
 * Date:2019/1/14
 * Time:15:58
 */
@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    @Autowired
    private RedisTemplate redisTemplate;



    public String hashGet(String name,String key) {
        HashOperations<String,String,String> oper = redisTemplate.opsForHash();
        try {
            return oper.get(name, key);
        } catch(RedisConnectionFailureException e) {
            //连接失败，不抛错，直接不用redis缓存了
            logger.error("connect redis error ",e);
            return null;
        }
    }
    public void hashPut(String name,String key,String value,int timeToLive) {
        HashOperations<String,String,String> oper = redisTemplate.opsForHash();
        try {
            oper.put(name, key, value);
            if(timeToLive != 0) {
                redisTemplate.expire(name,timeToLive, TimeUnit.SECONDS);
            }
        } catch (RedisConnectionFailureException e) {
            //连接失败，不抛错，直接不用redis缓存了
            logger.error("connect redis error ",e);
        }
    }
    public void hashDel(String name,String key) {
        HashOperations<String,String,String> oper = redisTemplate.opsForHash();
        try {
            //如果key为空，直接根据缓存名删除
            if(StringUtils.isEmpty(key)) {
                redisTemplate.delete(name);
            } else {
                oper.delete(name,key);
            }
        } catch (Exception e) {
            //直接不用redis缓存了
            logger.error("connect redis error ",e);
        }
    }


    /**
     * Object转成JSON数据
     */
    private String toJson(Object object){
        if(object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String){
            return String.valueOf(object);
        }
        return JSON.toJSONString(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz){
        return JSON.parseObject(json, clazz);
    }
}
