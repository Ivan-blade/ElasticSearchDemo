package com.wind.bdc.elasticsearchdemo.service.impl;


import com.wind.bdc.elasticsearchdemo.model.PersonData;
import com.wind.bdc.elasticsearchdemo.service.ESService;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author hylu.Ivan
 * @date 2021/7/30 下午1:52
 * @description
 */
@Service
public class ESServiceImpl implements ESService {
    @Autowired
    private Client client;

    @Override
    public String queryIdByName(String index, String type,String name) {
        String id = null;
        SearchRequestBuilder srb = client.prepareSearch(index);
        srb.setTypes(type);
        srb.setSearchType(SearchType.DEFAULT);
        //srb.setScroll(new TimeValue(2000));
        srb.setSize(10000);
        srb.setPreference("_primary");
        // srb.setFetchSource(new String[]{ROW_FEATURE_FIELD_NAME}, null);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("name",name));
        srb.setQuery(QueryBuilders.constantScoreQuery(queryBuilder));

        SearchResponse response = srb.execute().actionGet();

        SearchHits hits = response.getHits();
        SearchHit[] innerHits = hits.getHits();
        for(SearchHit hit :innerHits){
            Map<String, Object> source = hit.getSource();
            for(Map.Entry<String,Object> filed :source.entrySet()){
                if(filed.getValue().toString().equals(name)) id = hit.getId();
            }
        }
        return id;
    }

    @Override
    public PersonData queryByName(String index,String type,String name) {
        PersonData res = new PersonData();
        SearchRequestBuilder srb = client.prepareSearch(index);
        srb.setTypes(type);
        srb.setSearchType(SearchType.DEFAULT);
        //srb.setScroll(new TimeValue(2000));
        srb.setSize(10000);
        srb.setPreference("_primary");
        // srb.setFetchSource(new String[]{ROW_FEATURE_FIELD_NAME}, null);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("name",name));
        srb.setQuery(QueryBuilders.constantScoreQuery(queryBuilder));

        SearchResponse response = srb.execute().actionGet();

        SearchHits hits = response.getHits();
        SearchHit[] innerHits = hits.getHits();
        for(SearchHit hit :innerHits){
            // ID 为hit.getId();
            Map<String, Object> source = hit.getSource();
            //    private String name;
            //    private int code;
            //    private String hobby;
            //    private int age;
            for(Map.Entry<String,Object> filed :source.entrySet()){
                String key = filed.getKey();
                if (key.equals("name")) res.setName(filed.getValue().toString());
                if (key.equals("code")) res.setCode(Integer.parseInt(filed.getValue().toString()));
                if (key.equals("hobby")) res.setHobby(filed.getValue().toString());
                if (key.equals("age")) res.setAge(Integer.parseInt(filed.getValue().toString()));
            }
        }
        return res;
    }


    @Override
    public void add(String index, String type, PersonData personData) throws Exception{
        // 使用XContentBuilder创建一个doc source
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("code", personData.getCode())
                .field("name", personData.getName())
                .field("age", personData.getAge())
                .field("hobby", personData.getHobby())
                .endObject();
        // setId(id)
        // 如果没有设置id，则ES会自动生成一个id setSource
        IndexResponse indexResponse = client.prepareIndex()
                .setIndex(index)
                .setType(type)
                .setId(String.valueOf(personData.getCode()))
                .setSource(builder.string())
                .get();
    }


    @Override
    public void delete(String index, String type, String id)  throws Exception{
        DeleteResponse deleteResponse = client
                .prepareDelete()
                .setIndex(index)
                .setType(type)
                .setId(id)
                .get();
    }

    //创建索引库
    public void createIndex(String index){
        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(index).get();
        System.out.println(createIndexResponse.isAcknowledged()); // true表示成功
    }

    //给索引库增加 type,mapping
    public void addMapping(String index,String type) throws Exception{
        // 使用XContentBuilder创建Mapping
        XContentBuilder builder =
                XContentFactory.jsonBuilder()
                        .startObject()
                        .field("properties")
                        .startObject()
                        .field("code")
                        .startObject()
                        .field("type", "integer")
                        .endObject()
                        .field("name")
                        .startObject()
                        .field("index", "not_analyzed")
                        .field("type", "string")
                        .endObject()
                        .field("age")
                        .startObject()
                        .field("type", "integer")
                        .endObject()
                        .field("hobby")
                        .startObject()
                        .field("type", "string")
                        .endObject()
                        .endObject()
                        .endObject();
        System.out.println(builder.string());
        PutMappingRequest mappingRequest = Requests.putMappingRequest(index).source(builder).type(type);
        client.admin().indices().putMapping(mappingRequest).actionGet();
    }

}
