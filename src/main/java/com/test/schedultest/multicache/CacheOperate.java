package com.test.schedultest.multicache;

import com.xuanwu.apaas.core.multicache.publisher.MessagePublisher;
import com.xuanwu.apaas.core.multicache.util.EhCacheUtil;
import com.xuanwu.apaas.core.multicache.util.RedisUtil;
import com.xuanwu.apaas.core.multicache.util.SerializeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * 缓存切面操作类
 *
 */
@Component
public class CacheOperate {

    private static final Logger logger = LoggerFactory.getLogger(CacheOperate.class);

    @Autowired
    private EhCacheUtil ehCache;

    @Autowired
    private RedisUtil redisCache;

    @Autowired
    private MessagePublisher messagePublisher;

    @Value("${cache.defaultTimeToLive:60}")
    private int defaultTime;



    public void cacheDel(String name,String key,CacheType type) {
        if(type == CacheType.EHCACHE) {
            ehCache.hashDel(name, key);
        } else if(type == CacheType.REDIS) {
            redisCache.hashDel(name, key);
        } else {
            //删除redis对应的缓存
            redisCache.hashDel(name, key);
            if (ehCache.getCacheManager() != null) {
                //发布一个消息，告诉订阅的服务该缓存失效
                messagePublisher.publish(name, key);
            }
        }
    }
    public String  cacheGet(String name,String key,CacheType type){
        String cacheValue=null;
        if(type == CacheType.REDIS) {
            cacheValue = redisCache.hashGet(name,key);
        } else {
            //先从ehcache中取数据
            cacheValue = ehCache.hashGet(name, key);
        }
        if(StringUtils.isEmpty(cacheValue)&&type==CacheType.BOTH){
            cacheValue = redisCache.hashGet(name,key);
        }

        return cacheValue;
    }
    public void cachePut(String name,String key,String value,int timeToIdle,int timeToLive,CacheType cacheType){
        if(cacheType == CacheType.EHCACHE) {
            //将结果序列化后放入ehcache
            ehCache.hashPut(name, key, SerializeUtil.serialize(value), timeToIdle, timeToLive);
        } else if(cacheType == CacheType.REDIS) {
            //将结果序列化后放入redis
            redisCache.hashPut(name, key, SerializeUtil.serialize(value), timeToLive);
        } else { //如果使用ehcache和redis
            redisCache.hashPut(name, key, SerializeUtil.serialize(value), 0);
            //将结果序列化后放入ehcache
            ehCache.hashPut(name, key, SerializeUtil.serialize(value), timeToIdle, timeToLive);
        }

    }

    public void cachePut(String name,String key,String value,CacheType type){
        int timeToIdle=defaultTime;
        int timeToLive=defaultTime;
        cachePut(name, key, value, timeToIdle, timeToLive, type);
    }





}
