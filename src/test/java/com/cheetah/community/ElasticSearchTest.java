package com.cheetah.community;

import com.cheetah.community.dao.DiscussPostMapper;
import com.cheetah.community.dao.elasticsearch.DiscussPostRepository;
import com.cheetah.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticSearchTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostRepository discussRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Test
    public void testInsert(){
        discussRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussRepository.save(discussPostMapper.selectDiscussPostById(243));
    }
    @Test
    public void testInsertList(){
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100,0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(102,0,100,0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100,0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100,0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(112,0,100,0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(131,0,100,0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(132,0,100,0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(133,0,100,0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(134,0,100,0));
    }
    @Test
    public void testUpdate(){

    }
    @Test
    public void testSearchByRepository(){
        //构建搜索条件的类
        NativeSearchQuery searchQuery=new NativeSearchQueryBuilder()
                //查询条件，查询输入的关键字，以及查询字段
                .withQuery(QueryBuilders.multiMatchQuery("求职","title","content"))
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
        Page<DiscussPost> page=discussRepository.search(searchQuery);
        //一共多少数据
        System.out.println(page.getTotalElements());
        //一共多少页
        System.out.println(page.getTotalPages());
        //当前我们处在第几页
        System.out.println(page.getNumber());
        //每页多少条数据
        System.out.println(page.getSize());
        for (DiscussPost post:page){
            System.out.println(post);
        }
    }

}
