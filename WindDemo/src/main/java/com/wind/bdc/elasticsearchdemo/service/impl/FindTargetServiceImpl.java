package com.wind.bdc.elasticsearchdemo.service.impl;

import com.alibaba.fastjson.JSON;
import com.wind.bdc.elasticsearchdemo.model.PersonData;
import com.wind.bdc.elasticsearchdemo.service.ESService;
import com.wind.bdc.elasticsearchdemo.service.FindTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hylu.Ivan
 * @date 2021/7/30 下午1:52
 * @description
 */
@Service
public class FindTargetServiceImpl implements FindTargetService {

    @Autowired
    private ESService esService;

    @Autowired
    private RedisServiceImpl redisService;


    /**
     * 1.输入一个域账号，先从Redis中进行检索:
     * 若存在，则返回匹配的值。
     * 若不存在，则继续使用ES(hylu_index)进行检索。
     * 2.ES检索条件为NAME=域账号，取回DATA字段:
     * 若存在，则将域账号作为Key，对应的JSON作为Value插入到Redis中，同时返回。
     * 若不存在，则新建新增ES记录，并插入到Redis中，同时返回
     * @param target
     * @return
     */
    @Override
    public String findObjByName(String target) {

        // 从redis获取数据
        Object redisData = redisService.get(target);
        // 如果数据不为null，直接返回
        if(redisData != null) return "已经从redis查询到数据： "+(String)redisData;

        // 如果redis中没有相应数据，去es中查询相应对象
        PersonData res = esService.queryByName("hylu_index", "hylu_index", target);
        try {
            // 如果res.getName为空，则返回结果为null，代表es中也没有数据，开始准备redis和es的数据插入，并在插入完成后返回数据
            if (res.getName() == null) {
                PersonData personData = new PersonData("hylu", 20, "sleep", 18);
                esService.add("hylu_index", "hylu_index",personData);
                redisService.set(target, JSON.toJSONString(personData));
                return  "未从redis和es查询到数据，完成es和redis新数据入库，入库数据为： "+ personData.toString();
            } else {
                // 如果res.getName不为null，说明es中有数据，将数据直接提出存入到redis中并返回
                redisService.set(target, JSON.toJSONString(res));
                return "未从redis查询到数据，已经从es中获取到数据，完成redis新数据入库，入库数据为： "+ res.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return null;
        }

    }

}
