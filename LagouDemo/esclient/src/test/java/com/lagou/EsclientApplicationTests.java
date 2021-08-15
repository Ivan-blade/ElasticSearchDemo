package com.lagou;

import com.google.gson.Gson;
import com.lagou.pojo.Product;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsclientApplicationTests {

    private RestHighLevelClient restHighLevelClient;
    private Gson gson = new Gson();

    /**
     * 初始化客户端
     */
    @Before
    public void init(){
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost("127.0.0.1",9201,"http"),
                new HttpHost("127.0.0.1",9202,"http"),
                new HttpHost("127.0.0.1",9203,"http")
                );
        restHighLevelClient = new RestHighLevelClient(restClientBuilder);
    }


    @Test
    public void matchQuery()throws IOException{
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //设置查询类型和查询条件
        builder.query(QueryBuilders.matchQuery("title","手机"));
        //调用基础查询方法
        baseQuery(builder);
    }

    @Test
    public void sourceFilter()throws IOException{
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //执行查询条件和查询类型
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
        rangeQueryBuilder.gte(3600);
        rangeQueryBuilder.lte(4300);
        sourceBuilder.query(rangeQueryBuilder);
        //source过去，只保留id、title、price
        sourceBuilder.fetchSource(new String[]{"id","title","price"},null);
        baseQuery(sourceBuilder);;
    }

    /**
     * price: 3600 - 4300
     */
    @Test
    public void rangeQuery()throws IOException{
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //执行查询条件和查询类型
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
        rangeQueryBuilder.gte(3600);
        rangeQueryBuilder.lte(4300);
        sourceBuilder.query(rangeQueryBuilder);
        baseQuery(sourceBuilder);;
    }

    public void baseQuery(SearchSourceBuilder sourceBuilder) throws IOException {
        //创建搜索请求对象
        SearchRequest request = new SearchRequest();
        //查询构建工具
        request.source(sourceBuilder);
        //执行查询
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        //获得查询结果
        SearchHits hits = response.getHits();
        //获得文件数组
        SearchHit[] hitsHits = hits.getHits();
        for(SearchHit searchHit: hitsHits){
            String json = searchHit.getSourceAsString();
            //将json反序列化为Product格式
            Product product = gson.fromJson(json, Product.class);
            System.out.println(product);
        }
    }

    @Test
    public void sortAndPage()throws IOException{
        //创建搜索请求对象
        SearchRequest request = new SearchRequest();
        //查询构建工具
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //添加查询条件，执行查询类型
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        //执行排序 价格降序排序
        sourceBuilder.sort("price", SortOrder.DESC);

        //分页信息
        int pageNumber = 1;
        int pageSize = 3;
        int from = (pageNumber-1)*pageSize;
        //设置分页
        sourceBuilder.from(from);
        sourceBuilder.size(3);

        baseQuery(sourceBuilder);
    }


    @Test
    public void matchAll() throws IOException {
        //创建搜索请求对象
        SearchRequest request = new SearchRequest();
        //查询构建工具
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //添加查询条件，执行查询类型
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        request.source(sourceBuilder);
        //执行查询
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        //获得查询结果
        SearchHits hits = response.getHits();
        //获得文件数组
        SearchHit[] hitsHits = hits.getHits();
        for(SearchHit searchHit: hitsHits){
            String json = searchHit.getSourceAsString();
            //将json反序列化为Product格式
            Product product = gson.fromJson(json, Product.class);
            System.out.println(product);
        }
    }


    @Test
    public void testDelete() throws IOException{
        //初始化DeleteRequest对象
        DeleteRequest request = new DeleteRequest("lagou","item","1");
        //执行删除
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }


    @Test
    public void testView() throws IOException {
        //初始化GetRequest对象
        GetRequest getRequest = new GetRequest("lagou","item","1");
        //执行查询
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        //取出数据
        String source = getResponse.getSourceAsString();
        Product product = gson.fromJson(source, Product.class);
        System.out.println(product);
    }

    /**
     * 插入文档
     */
    @Test
    public void testInsert() throws IOException {
        //1.文档数据
        Product product = new Product();
        product.setBrand("华为");
        product.setCategory("手机");
        product.setId(1L);
        product.setImages("http://image.huawei.com/1.jpg");
        product.setPrice(5999.99);
        product.setTitle("华为P30");
        //2.将文档数据转换为json格式
        String source = gson.toJson(product);
        //3.创建索引请求对象 访问哪个索引库、哪个type、指定文档ID
        //public IndexRequest(String index, String type, String id)
        IndexRequest request = new IndexRequest("lagou","item",product.getId().toString());
        request.source(source, XContentType.JSON);
        //4.发出请求
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 关闭客户端
     */
    @After
    public void close() throws IOException{
        restHighLevelClient.close();
    }


}
