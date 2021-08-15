package com.wind.bdc.elasticsearchdemo.service.impl;

import com.wind.bdc.elasticsearchdemo.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author hylu.Ivan
 * @date 2021/7/30 下午1:52
 * @description
 */
@Service
public class RedisServiceImpl implements RedisService {

    private static Logger LOGGER = LoggerFactory.getLogger(RedisServiceImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;
   /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
   public Object get(String key) {
       return key == null ? null : redisTemplate.opsForValue().get(key);
   }

   /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
   public boolean set(String key, Object value) {
       try {
           redisTemplate.opsForValue().set(key, value);
           return true;
       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }
   }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }
    
}
