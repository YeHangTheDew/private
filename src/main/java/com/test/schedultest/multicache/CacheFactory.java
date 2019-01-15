package com.test.schedultest.multicache;

import com.xuanwu.apaas.core.multicache.publisher.MessagePublisher;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;


/**
 * 多级缓存切面
 * @author rongdi
 */
@Component
public class CacheFactory {

    private static final Logger logger = LoggerFactory.getLogger(CacheFactory.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MessagePublisher messagePublisher;

    private CacheManager cacheManager;

    public CacheFactory() {
        InputStream is = this.getClass().getResourceAsStream("/ehcache.xml");
        if(is != null) {
            cacheManager = CacheManager.create(is);
        }
    }

    public void cacheDel(String name,String key,CacheType type) {
        if(type == CacheType.EHCACHE) {
            ehDel(name,key);
        } else if(type == CacheType.REDIS) {
            redisDel(name, key);
        } else {
            //删除redis对应的缓存
            redisDel(name, key);
            if (cacheManager != null) {
                //发布一个消息，告诉订阅的服务该缓存失效
                messagePublisher.publish(name, key);
            }
        }
    }

    public String ehGet(String name,String key) {
        if(cacheManager == null) return null;
        Cache cache=cacheManager.getCache(name);
        if(cache == null) return null;
        cache.acquireReadLockOnKey(key);
        try {
            Element ele = cache.get(key);
            if(ele == null) return null;
            return (String)ele.getObjectValue();
        } finally {
            cache.releaseReadLockOnKey(key);
        }


    }

    public String redisGet(String name,String key) {
        HashOperations<String,String,String> oper = redisTemplate.opsForHash();
        try {
            return oper.get(name, key);
        } catch(RedisConnectionFailureException e) {
            //连接失败，不抛错，直接不用redis缓存了
            logger.error("connect redis error ",e);
            return null;
        }
    }

    public void ehPut(String name,String key,String value,int timeToIdle,int timeToLive) {
        if(cacheManager == null) return;
        synchronized(name.intern()) {
            if (!cacheManager.cacheExists(name)) {
                cacheManager.addCache(name);
            }
        }
        Cache cache=cacheManager.getCache(name);
        //获得key上的写锁，不同key互相不影响，类似于synchronized(key.intern()){}
        cache.acquireWriteLockOnKey(key);
        try {
            Element ele = new Element(key, value);
            if(timeToLive != 0) {
                ele.setTimeToLive(timeToLive);
                timeToIdle = timeToIdle == 0?timeToLive:timeToIdle;
            }
            if(timeToIdle != 0) {
                ele.setTimeToIdle(timeToIdle);
            }

            cache.put(ele);
        } finally {
            //释放写锁
            cache.releaseWriteLockOnKey(key);
        }
    }

    public void redisPut(String name,String key,String value,int timeToLive) {
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

    public void ehDel(String name,String key) {
        if(cacheManager == null) return;
        Cache cache = cacheManager.getCache(name);
        if(cache != null) {
            //如果key为空，直接根据缓存名删除
            if(StringUtils.isEmpty(key)) {
                cacheManager.removeCache(name);
            } else {
                cache.remove(key);
            }
        }
    }

    public void redisDel(String name,String key) {
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
}
