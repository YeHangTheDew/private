package com.test.schedultest.multicache.util;


import com.xuanwu.apaas.core.domain.CacheInfo;
import com.xuanwu.apaas.core.multicache.CacheOperate;
import com.xuanwu.apaas.core.multicache.CacheType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IDEA
 * author:yechh
 * Date:2019/1/15
 * Time:15:41
 */
@Component
public class GobalCacheUtil {

    @Autowired
    private CacheOperate cacheOperate;

    private static final GobalCacheUtil singleton = new GobalCacheUtil();

    public static GobalCacheUtil getInstance() {
        return singleton;
    }


    private GobalCacheUtil() {

    }


    public Object cacheGet(CacheInfo cacheInfo) {
       return SerializeUtil.deserialize(cacheGet(cacheInfo), cacheInfo.getClass());
    }


    public void cacheDel(String name, String key) {
        cacheDel(name, key, CacheType.BOTH);
    }

    public void cachePut(String name, String key, String value) {
        cacheOperate.cachePut(name, key, value , CacheType.BOTH);
    }

    public String cacheGet(String name, String key, CacheType type) {
        return cacheOperate.cacheGet(name, key, type);
    }

    public void cachePut(String name, String key, String value, int timeToIdle, int timeToLive, CacheType type) {
        cacheOperate.cachePut(name, key, value, timeToIdle, timeToLive, type);
    }

    public void cacheDel(String name, String key, CacheType type) {
        cacheOperate.cacheDel(name, key, type);
    }

}
