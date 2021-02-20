package com.cheetah.community.dao.elasticsearch;

import com.cheetah.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
//这里用到的这个注解，是spring为数据访问层的注解，而Mapper是Mybatis的专用的注解
//这个elasticsearch就可以看做一个特别的数据访问层
@Repository
//继承默认接口，泛型中声明了两个东西，一个是我们的处理的实体类，一个是实体类的组件
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
