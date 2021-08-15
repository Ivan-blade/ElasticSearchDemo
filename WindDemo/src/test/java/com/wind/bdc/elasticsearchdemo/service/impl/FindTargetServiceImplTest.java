package com.wind.bdc.elasticsearchdemo.service.impl;

import com.wind.bdc.elasticsearchdemo.service.ESService;
import com.wind.bdc.elasticsearchdemo.service.FindTargetService;
import com.wind.bdc.elasticsearchdemo.service.RedisService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hylu.Ivan
 * @date 2021/7/30 下午2:24
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FindTargetServiceImplTest {


    @Autowired
    private FindTargetService findNameService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ESService esService;

    /**
     * 测试redis，es都没有数据的情况
     */
//    @Test
    public void test() {
        try {
            redisService.del("hylu");
            String id = esService.queryIdByName("hylu_index", "hylu_index", "hylu");
            if(id != null) esService.delete("hylu_index","hylu_index",id);
            findNameService.findObjByName("hylu");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试redis没有数据，es有数据的情况
     */
//    @Test
    public void test01() {
        redisService.del("hylu");
        findNameService.findObjByName("hylu");
    }

    /**
     * 测试redis,es都有数据的情况:执行以上任意一种方法redis和es都有数据
     */
//    @Test
    public void test02() {
        findNameService.findObjByName("hylu");
    }
}