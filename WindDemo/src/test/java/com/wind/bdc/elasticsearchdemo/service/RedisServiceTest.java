package com.wind.bdc.elasticsearchdemo.service;

import com.wind.bdc.elasticsearchdemo.service.impl.RedisServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hylu.Ivan
 * @date 2021/7/30 下午1:30
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisServiceTest {

    @Autowired
    private RedisServiceImpl redisService;

    @Test
    public void test() {

    }
}