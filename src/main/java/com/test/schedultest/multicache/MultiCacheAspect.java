package com.test.schedultest.multicache;

import com.xuanwu.apaas.core.multicache.annotation.CacheEvict;
import com.xuanwu.apaas.core.multicache.annotation.Cacheable;
import com.xuanwu.apaas.core.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 多级缓存切面
 * @author rongdi
 */
@Aspect
@Component
public class MultiCacheAspect {

    private static final Logger logger = LoggerFactory.getLogger(MultiCacheAspect.class);

    @Autowired
    private CacheFactory cacheFactory;

    //这里通过一个容器初始化监听器，根据外部配置的@EnableCaching注解控制缓存开关
    private boolean cacheEnable;

    //当注解使用0时使用配置文件的过期时间,默认60s
    @Value("${cache.defaultTimeToLive:60}")
    private int defaultTimeToLive;

    @Pointcut("@annotation(com.xuanwu.apaas.core.multicache.annotation.Cacheable)")
    public void cacheableAspect() {
    }

    @Pointcut("@annotation(com.xuanwu.apaas.core.multicache.annotation.CacheEvict)")
    public void cacheEvict() {
    }

    @Around("cacheableAspect()")
    public Object cache(ProceedingJoinPoint joinPoint) {

        //得到被切面修饰的方法的参数列表
        Object[] args = joinPoint.getArgs();
        // result是方法的最终返回结果
        Object result = null;
        //如果没有开启缓存，直接调用处理方法返回
        if(!cacheEnable){
            try {
                result = joinPoint.proceed(args);
            } catch (Throwable e) {
                logger.error("直接调用处理方法返回异常！",e);
            }
            return result;
        }

        // 得到被代理方法的返回值类型
        Class returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();
        // 得到被代理的方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 得到被代理的方法上的注解
        Cacheable ca = method.getAnnotation(Cacheable.class);
        //获得经过el解析后的key值
        String key = parseKey(ca.key(),method,args);
        Class<?> elementClass = ca.type();
        //从注解中获取缓存名称,支持spel表达式
        String name = ca.value();
        try {
            name = parseKey(ca.value(),method,args);
        } catch(Exception e) {
            //不是el表达式直接当成字符串用，忽略错误
        }
        CacheType cacheType = ca.use();
        int timeToIdle = ca.timeToIdle();
        int timeToLive = ca.timeToLive();

        try {
            String cacheValue = null;
            if(cacheType == CacheType.REDIS) {
                cacheValue = cacheFactory.redisGet(name,key);
            } else {
                //先从ehcache中取数据
                cacheValue = cacheFactory.ehGet(name, key);
            }
            if(StringUtils.isEmpty(cacheValue)) {
                //如果使用默认的过期时间0，缓存存活时间改成使用配置文件的时间
                if(timeToIdle == 0) {
                    timeToLive = defaultTimeToLive;
                    timeToIdle = defaultTimeToLive;
                }
                //如果只使用ehcache
                if(cacheType == CacheType.EHCACHE) {
                    // 调用业务方法得到结果
                    result = joinPoint.proceed(args);
                    //将结果序列化后放入ehcache
                    cacheFactory.ehPut(name,key,serialize(result),timeToIdle,timeToLive);
                } else if(cacheType == CacheType.REDIS) {
                    // 调用业务方法得到结果
                    result = joinPoint.proceed(args);
                    //将结果序列化后放入redis
                    cacheFactory.redisPut(name,key,serialize(result),timeToLive);
                } else { //如果使用ehcache和redis
                    //如果ehcache中没数据，从redis中取数据
                    cacheValue = cacheFactory.redisGet(name,key);
                    if(StringUtils.isEmpty(cacheValue)) {
                        //如果redis中没有数据
                        // 调用业务方法得到结果
                        result = joinPoint.proceed(args);
                        //将结果序列化后放入redis，使用两级缓存，redis不用设置过期时间，过期时间由redis配置类控制
                        cacheFactory.redisPut(name,key,serialize(result),0);
                    } else {
                        //如果redis中可以取到数据
                        //将缓存中获取到的数据反序列化后返回
                        result = deserialize(result, returnType, elementClass, cacheValue);
                    }
                    //将结果序列化后放入ehcache
                    cacheFactory.ehPut(name,key,serialize(result),timeToIdle,timeToLive);
                }

            } else {
                //将缓存中获取到的数据反序列化后返回
                result = deserialize(result, returnType, elementClass, cacheValue);
            }

        } catch (Throwable throwable) {
            logger.error("",throwable);
        }

       return result;
    }

    private Object deserialize(Object result, Class returnType, Class<?> elementClass, String cacheValue) {
        if(elementClass == Exception.class) {
            result = deserialize(cacheValue, returnType);
        } else {
            result = deserialize(cacheValue, returnType,elementClass);
        }
        return result;
    }

    /**
     * 在方法调用前清除缓存，然后调用业务方法
     * @param joinPoint
     * @return
     * @throws Throwable
     *
     */
    @Around("cacheEvict()")
    public Object evictCache(ProceedingJoinPoint joinPoint) throws Throwable {
        // 得到被代理的方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //得到被切面修饰的方法的参数列表
        Object[] args = joinPoint.getArgs();
        // 得到被代理的方法上的注解
        CacheEvict ce = method.getAnnotation(CacheEvict.class);
        //获得经过el解析后的key值
        String key = parseKey(ce.key(),method,args);
        //从注解中获取缓存名称,支持spel表达式
        String name = ce.value();
        try {
            name = parseKey(ce.value(),method,args);
        } catch(Exception e) {

        }
        CacheType cacheType = ce.use();
        // 清除对应缓存
        cacheFactory.cacheDel(name,key,cacheType);
        return joinPoint.proceed(args);
    }

    /**
     * 获取缓存的key
     * key 定义在注解上，支持SPEL表达式
     * @return
     */
    private String parseKey(String key,Method method,Object [] args){

        if(StringUtils.isEmpty(key)) return null;

        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);

        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for(int i=0;paraNameArr != null && i<paraNameArr.length;i++){
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context,String.class);
    }

    //序列化
    private String serialize(Object obj) {

        String result = null;
        try {
            result = JsonUtil.serialize(obj);
        } catch(Exception e) {
            result = obj == null ? "空值" : obj.toString();
        }
        return result;

    }

    //反序列化
    private Object deserialize(String str,Class clazz) {

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
    private Object deserialize(String str,Class clazz,Class elementClass) {

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

    public void setCacheEnable(boolean cacheEnable) {
        this.cacheEnable = cacheEnable;
    }

}

