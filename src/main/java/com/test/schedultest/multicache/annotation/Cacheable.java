package com.test.schedultest.multicache.annotation;

import com.xuanwu.apaas.core.multicache.CacheType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

    String value() default "";

    String key() default "";

    //使用缓存种类，ehcache,redis,both,both表示ehcache和redis一起使用
    CacheType use() default CacheType.BOTH;

    //超过空闲时间过期，单位秒，缓存种类选用redis，本参数无效
    int timeToIdle() default 0;

    //最大存活时间，单位秒，当缓存选BOTH时，该参数无效
    int timeToLive() default 0;

    //泛型的Class类型
    Class<?> type() default Exception.class;

}
