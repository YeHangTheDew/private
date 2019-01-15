package com.test.schedultest.multicache.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Created with IDEA
 * author:yechh
 * Date:2019/1/14
 * Time:15:56
 */
@Component
public class EhCacheUtil {
    private CacheManager cacheManager;
    public EhCacheUtil() {
        InputStream is = this.getClass().getResourceAsStream("/ehcache.xml");
        if(is != null) {
            cacheManager = CacheManager.create(is);
        }
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void hashDel(String name,String key) {
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
    public void hashPut(String name,String key,String value,int timeToIdle,int timeToLive) {
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
    public String hashGet(String name,String key) {
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



}
