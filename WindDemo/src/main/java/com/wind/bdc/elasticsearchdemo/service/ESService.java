package com.wind.bdc.elasticsearchdemo.service;


import com.wind.bdc.elasticsearchdemo.model.PersonData;


/**
 * @author hylu.Ivan
 * @date 2021/7/30 下午1:52
 * @description
 */
public interface ESService {

     void createIndex(String index);

     void addMapping(String index,String type) throws Exception;

     void add(String index, String type, PersonData personData)  throws Exception;

     void delete(String index, String type, String id) throws Exception;

     String queryIdByName(String index, String type,String name);

     PersonData queryByName(String index, String type,String name);

}
