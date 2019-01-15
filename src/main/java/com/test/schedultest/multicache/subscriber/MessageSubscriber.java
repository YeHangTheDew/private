package com.test.schedultest.multicache.subscriber;

import com.xuanwu.apaas.core.multicache.CacheFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(MessageSubscriber.class);

    @Autowired
    private CacheFactory cacheFactory;


    /**
     * 接收到redis订阅的消息后，将ehcache的缓存失效
     * @param message 格式为name_key
     */
    public void handle(String message){

        logger.debug("redis.uncache:"+message);
        if(StringUtils.isEmpty(message)) {
           return;
        }
        String[] strs = message.split("#");
        String name = strs[0];
        String key = null;
        if(strs.length == 2) {
            key = strs[1];
        }
        cacheFactory.ehDel(name,key);

    }

}
