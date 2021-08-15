package com.lagou.resultmapper;

import com.google.gson.Gson;
import com.lagou.pojo.Product;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义结果映射，处理高亮
 */
public class ESSearchResultMapper implements SearchResultMapper {
    /**
     * 完成结果映射
     * 操作的重点应该是将原有的结果：_source取出来，放入高亮的数据
     *
     * @param response
     * @param clazz
     * @param pageable
     * @param <T>
     * @return AggregatedPage需要三个参数进行构建：pageable,List<product>,总记录数
     */
    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
        //获得总记录数
        long totalHits = response.getHits().getTotalHits();
        //记录列表
        List<T> list = new ArrayList<>();
        //获取原始的搜索结果
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            if (hits.getHits().length <= 0) {
                return null;
            }
            //获取_source属性中的所有数据
            Map<String, Object> map = hit.getSourceAsMap();
            //获得高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //每个高亮字段都需要进行设置
            for(Map.Entry<String,HighlightField> highlightField : highlightFields.entrySet()){
                //获得高亮的key：高亮字段
                String key = highlightField.getKey();
                //获得value：高亮之后的效果
                HighlightField value = highlightField.getValue();
                //将高亮字段和文本效果放入到map中
                map.put(key,value.getFragments()[0].toString());
            }
            //将map转换为对象
            Gson gson = new Gson();
            //map-->jsonString-->对象
            T t = gson.fromJson(gson.toJson(map), clazz);
            list.add(t);
        }
        return new AggregatedPageImpl<>(list,pageable,totalHits);
    }
}
