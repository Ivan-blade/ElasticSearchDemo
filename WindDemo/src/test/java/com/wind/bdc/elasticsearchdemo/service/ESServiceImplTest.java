package com.wind.bdc.elasticsearchdemo.service;

import com.wind.bdc.elasticsearchdemo.model.PersonData;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hylu.Ivan
 * @date 2021/7/30 上午10:31
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ESServiceImplTest extends TestCase {

    @Autowired
    private ESService esService;


//    @Test
    public void test() {
        try {

            esService.createIndex("hylu_index");
            esService.addMapping("hylu_index", "hylu_index");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}