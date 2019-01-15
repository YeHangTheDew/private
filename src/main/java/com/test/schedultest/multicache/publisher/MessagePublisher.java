package com.test.schedultest.multicache.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {

    @Autowired
    private RedisTemplate redisTemplate;

    //将缓存失效的通知发送到redis.uncache通道上
    public void publish(String name,String key) {
        redisTemplate.convertAndSend("redis.uncache", key == null ? name : (name + "#" + key));
    }

}
