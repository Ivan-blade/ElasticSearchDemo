package com.wind.bdc.elasticsearchdemo.service;

/**
 * @author hylu.Ivan
 * @date 2021/7/30 下午3:49
 * @description
 */
public interface RedisService {

    Object get(String key);

    boolean set(String key, Object value);

    public void del(String... key);
}
