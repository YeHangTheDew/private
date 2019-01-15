package com.test.schedultest.multicache.config;

import com.xuanwu.apaas.core.multicache.subscriber.MessageSubscriber;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MultiCacheConfig {

   @Bean
   public CacheManager cacheManager(RedisTemplate redisTemplate) {
      RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
      //设置缓存过期时间(秒)
      Map<String, Long> expires = new HashMap<>();
      expires.put("ExpOpState",0L);
      expires.put("ImpOpState",0L);
      rcm.setExpires(expires);
      rcm.setDefaultExpiration(600);
      return rcm;
   }

   @Bean
   public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
      StringRedisTemplate template = new StringRedisTemplate(factory);
      StringRedisSerializer redisSerializer = new StringRedisSerializer();
      template.setValueSerializer(redisSerializer);
      template.afterPropertiesSet();
      return template;
   }

   /**
    * redis消息监听器容器
    * 可以添加多个监听不同话题的redis监听器，只需要把消息监听器和相应的消息订阅处理器绑定，该消息监听器
    * 通过反射技术调用消息订阅处理器的相关方法进行一些业务处理
    * @param connectionFactory
    * @param listenerAdapter
    * @return
    */
   @Bean
   public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                  MessageListenerAdapter listenerAdapter) {
      RedisMessageListenerContainer container = new RedisMessageListenerContainer();
      container.setConnectionFactory(connectionFactory);
      //订阅了一个叫redis.uncache的通道
      container.addMessageListener(listenerAdapter, new PatternTopic("redis.uncache"));
      //这个container 可以添加多个 messageListener
      return container;
   }

   /**
    * 消息监听器适配器，绑定消息处理器，利用反射技术调用消息处理器的业务方法
    * @param receiver
    * @return
    */
   @Bean
   MessageListenerAdapter listenerAdapter(MessageSubscriber receiver) {
      //这个地方 是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“handle”
      return new MessageListenerAdapter(receiver, "handle");
   }

}