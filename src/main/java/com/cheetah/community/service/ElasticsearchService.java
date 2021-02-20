package com.cheetah.community.service;

import com.cheetah.community.dao.elasticsearch.DiscussPostRepository;
import com.cheetah.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;
    public void saveDiscussPost(DiscussPost post){
        discussPostRepository.save(post);
    }
    public void deleteDiscussPost(int id){
        discussPostRepository.deleteById(id);
    }
    public Page<DiscussPost> searchDiscussPost(String keyWord,int current, int limit) {
        //构建搜索条件的类
        NativeSearchQuery searchQuery=new NativeSearchQueryBuilder()
                //查询条件，查询输入的关键字，以及查询字段
                .withQuery(QueryBuilders.multiMatchQuery(keyWord,"title","content"))
                //排序方式，是多个条件，安装编码顺序的优先级
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                //分页条件
                .withPageable(PageRequest.of(0,10))
                //高亮显示，就是我们平常搜索时所看到的红色
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        //底层调用了elasticsearchTemplate.queryForPage(searchQuery,DiscussPost.class,SearchResultMapper);
        //底层获取到了高亮显示值但是没有处理
        Page<DiscussPost> page=discussPostRepository.search(searchQuery);
        return page;
    }
}
