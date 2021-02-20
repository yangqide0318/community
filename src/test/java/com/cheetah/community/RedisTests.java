package com.cheetah.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void testString(){
        String redisKey="test:count";
        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }
    @Test
    public void testHash(){
        String redisKey="test:user";
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","zhangsan");
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));

    }
    @Test
    public void testLists(){
        String redisKey="test:ids";
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);
        redisTemplate.opsForList().leftPush(redisKey,104);
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
    }
    @Test
    public void testSet(){
        String redisKey="test:teachers";
        redisTemplate.opsForSet().add(redisKey,"刘备","关羽","张飞");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }
    @Test
    public void testSortSet(){
        String redisKey="test:student";
        redisTemplate.opsForZSet().add(redisKey,"yqd",100);
        redisTemplate.opsForZSet().add(redisKey,"wt",90);
        redisTemplate.opsForZSet().add(redisKey,"cnm",80);
        redisTemplate.opsForZSet().add(redisKey,"lj",70);
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"yqd"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"yqd"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,2));
    }
    @Test
    public void testKeys(){
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:student",10, TimeUnit.SECONDS);
    }
    @Test
    public void testBoundKey(){
        String redisKeys="test:count";
        BoundValueOperations operations=redisTemplate.boundValueOps(redisKeys);
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }
    //redis的编程式事务
    @Test
    public void testTransactional(){
        Object object=redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey="test:tx";
                redisOperations.multi();
                redisOperations.opsForSet().add(redisKey,"zhangsan");
                redisOperations.opsForSet().add(redisKey,"wangwu");
                redisOperations.opsForSet().add(redisKey,"zhaoliu");
                System.out.println(redisOperations.opsForSet().members(redisKey));
                return redisOperations.exec();
            }
        });
        System.out.println(object);
    }
    @Test
    //统计20万个重复数据的独立总数
    public void testHyperLogLog(){
        String redisKey="test:hll:01";
        for (int i = 0; i <100000 ; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }
        for (int i = 0; i <100000 ; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey,(int)Math.random()*100000+1);
        }
        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
    }
    //将三组数据合并，在统计合并后的重复数据的独立总数
    @Test
    public void testHyperLogLogUnion(){
        String redisKey2="test:hll:02";
        for (int i = 0; i <10000 ; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2,i);
        }
        String redisKey3="test:hll:03";
        for (int i = 5001; i <15000 ; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3,i);
        }
        String redisKey4="test:hll:04";
        for (int i = 10001; i <20000 ; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4,i);
        }
        String unionKey="test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey,redisKey2,redisKey3,redisKey4);
        long size=redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);
    }
    //统计一组数据的布尔值
    @Test
    public void testBitMap(){
        String redisKey="test:bm:01";
        //记录
        redisTemplate.opsForValue().setBit(redisKey,1,true);
        redisTemplate.opsForValue().setBit(redisKey,2,true);
        redisTemplate.opsForValue().setBit(redisKey,3,true);
        redisTemplate.opsForValue().setBit(redisKey,6,true);
        redisTemplate.opsForValue().setBit(redisKey,7,true);
        //查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,3));
        //统计
        Object obj=redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
    }
    //统计3组数据的布尔值，并进行逻辑运算
    @Test
    public void testBitMapOperation(){
        String redisKey2="test:bm:02";
        //记录
        redisTemplate.opsForValue().setBit(redisKey2,1,true);
        redisTemplate.opsForValue().setBit(redisKey2,2,true);
        redisTemplate.opsForValue().setBit(redisKey2,3,true);

        String redisKey3="test:bm:03";
        //记录
        redisTemplate.opsForValue().setBit(redisKey3,1,true);
        redisTemplate.opsForValue().setBit(redisKey3,2,true);
        redisTemplate.opsForValue().setBit(redisKey3,4,true);
        String redisKey4="test:bm:04";
        //记录
        redisTemplate.opsForValue().setBit(redisKey4,4,true);
        redisTemplate.opsForValue().setBit(redisKey4,5,true);
        redisTemplate.opsForValue().setBit(redisKey4,6,true);
        String redisKey="test:bm:or";
        Object obj=redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),redisKey2.getBytes(),redisKey3.getBytes(),redisKey4.getBytes());
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,6));

    }
    @Test
    public void test(){
        String a="a"+"b";
    }
}
