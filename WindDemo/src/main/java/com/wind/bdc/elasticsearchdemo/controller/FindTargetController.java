package com.wind.bdc.elasticsearchdemo.controller;

import com.wind.bdc.elasticsearchdemo.service.ESService;
import com.wind.bdc.elasticsearchdemo.service.FindTargetService;
import com.wind.bdc.elasticsearchdemo.service.impl.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hylu.Ivan
 * @date 2021/7/30 下午1:47
 * @description
 */
@RestController
public class FindTargetController {

    @Autowired
    private FindTargetService findNameService;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private ESService esService;


    /**
     * 测试redis，es都有数据的情况
     */
    @RequestMapping("/findTargetBothHas")
    public String findNameBothHas(String name) {
        return findNameService.findObjByName(name);
    }

    /**
     * 测试没有redis，有es的情况
     */
    @RequestMapping("/findTargetRedisNone")
    public String findNameRedisNone(String name) {
        redisService.del(name);
        return findNameService.findObjByName(name);
    }

    /**
     * 测试redis，es都没有数据的情况
     * 注意es删除数据速度较慢，执行第一次可能还是能从es中查询到数据，所以可能需要执行两次
     */
    @RequestMapping("/findTargetBothNone")
    public String findNameBothNone(String name) {
        try {
            redisService.del(name);
            String id = esService.queryIdByName("hylu_index", "hylu_index", name);
            if(id != null) esService.delete("hylu_index","hylu_index",id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return findNameService.findObjByName(name);
    }
}
