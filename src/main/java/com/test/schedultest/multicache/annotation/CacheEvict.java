package com.test.schedultest.multicache.annotation;

import com.xuanwu.apaas.core.multicache.CacheType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheEvict {

    String value() default "";

    String key() default "";

    //使用缓存种类，ehcache,redis,both,both表示ehcache和redis一起使用
    CacheType use() default CacheType.BOTH;

}
