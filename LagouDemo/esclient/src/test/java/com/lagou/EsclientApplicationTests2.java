package com.lagou;

import com.lagou.pojo.Product;
import com.lagou.respository.ProductRepository;
import com.lagou.resultmapper.ESSearchResultMapper;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsclientApplicationTests2 {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private ProductRepository productRepository;

    /**
     * 需求：
     * 查询title中包含小米手机的商品，以价格升序排序，分页查询：每页展示2条，查询第1页。
     * 对查询结果进行聚合分析：获取品牌及个数
     */
    @Test
    public void nativeQuery(){
        //1.构架一个原生查询器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //2.source过滤
        //2.1 参数：final String[] includes, final String[] excludes
        //如果不想执行source过滤可以将该行注释
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[0],new String[0]));
        //3.查询条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("title","小米手机"));
        //4.设置分页和排序规则
        queryBuilder
                .withPageable(PageRequest.of(0,10,Sort.by(Sort.Direction.DESC,"price")));
        //5.高亮
        HighlightBuilder.Field field = new HighlightBuilder.Field("title");
        field.preTags("<font style='color:red'>");
        field.postTags("</font>");
        queryBuilder.withHighlightFields(field);
        //6.聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("brandAgg").field("brand"));
        //7.查询
        AggregatedPage<Product> result = template.queryForPage(queryBuilder.build(), Product.class,new ESSearchResultMapper());
        //获取结果
        long total = result.getTotalElements();
        //页码
        int totalPages = result.getTotalPages();
        //获得本页的数据集合
        List<Product> content = result.getContent();
        System.out.println(total+"  "+totalPages);
        content.stream().forEach(product -> System.out.println(product));

    }

    @Test
    public void findByPrice() {
        List<Product> list = productRepository.findByPriceBetween(2000.00, 4000.00);
        System.out.println(list.size());
    }


    @Test
    public void findAll() {
        Iterable<Product> iterable = productRepository.findAll();
        iterable.forEach(System.out::println);
    }

    @Test
    public void findById2() {
        Optional<Product> optional = productRepository.findById(100L);
        //取出数据
        //orElse方法的作用：如果optional中封装的实体对象为空也就是没有从索引库中查询出匹配的文档，返回orElse方法的参数
        Product defaultResult = new Product();
        defaultResult.setTitle("default data");
        Product product = optional.orElse(defaultResult);
        System.out.println(product);
    }

    @Test
    public void findById() {
        Optional<Product> optional = productRepository.findById(3L);
        //取出数据
        //orElse方法的作用：如果optional中封装的实体对象为空也就是没有从索引库中查询出匹配的文档，返回orElse方法的参数
        Product product = optional.orElse(null);
        System.out.println(product);
    }

    @Test
    public void insertDocument() {
        Product product = new Product(6L, "小米手机", "手机", "锤子", 3299.99, "http://image.chuizi.com/1.jpg");
        //新增
        productRepository.save(product);
        System.out.println("save success");
    }

    @Test
    public void insertDocuments() {
        Product product1 = new Product(2L, "坚果手机", "手机", "phone", 3299.99, "http://image.chuizi.com/1.jpg");
        Product product2 = new Product(3L, "华为手机", "手机", "phone", 3299.99, "http://image.chuizi.com/1.jpg");
        Product product3 = new Product(4L, "苹果手机", "手机", "phone", 3299.99, "http://image.chuizi.com/1.jpg");
        Product product4 = new Product(5L, "索尼手机", "手机", "phone", 3299.99, "http://image.chuizi.com/1.jpg");
        List<Product> list = new ArrayList<>();
        list.add(product1);
        list.add(product2);
        list.add(product3);
        list.add(product4);
        productRepository.saveAll(list);
        System.out.println("save success");
    }


    @Test
    public void createIndex() {
        //创建索引的方法
        template.createIndex(Product.class);
    }

    @Test
    public void createType() {
        //创建类型映射
        template.putMapping(Product.class);
    }

    @Test
    public void init() {
        System.out.println(template);
    }

}
