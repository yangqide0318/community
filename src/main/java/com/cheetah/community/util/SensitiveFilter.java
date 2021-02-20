package com.cheetah.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT="***";
    //根节点
    private TrieNode rootNode=new TrieNode();
    //初始化前缀树
    //初始化方法总结，在容器实例化构造器调用之后就会直接调用这个方法，服务启动时就会被调用。
    @PostConstruct
    public void init(){
        try(
        //这里通过任意类的加载器去获取我们的任意的class文件，可以看到这里的this和我们要获取的文件没有任何关系，只是相当于一个通道
        //getResourceAsStream而这个方法就是将目标文件内容已一个流返回
        InputStream is=this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
        //字节-》字符-》缓存字符流
        BufferedReader reader=new BufferedReader(new InputStreamReader(is));

        ){
            String keyWord;
            while ((keyWord=reader.readLine())!=null) {
                //添加到前缀树
                this.addKeyWord(keyWord);
            }
        }catch (IOException e){
            logger.error("加载敏感词失败");
        }
    }
    //将一个敏感词添加到前缀树
    private void addKeyWord(String keyWord){
        TrieNode tempNode=rootNode;
        for (int i=0;i<keyWord.length();i++){
            char c=keyWord.charAt(i);
            TrieNode subNode=tempNode.getSubNode(c);
            if(subNode==null){
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //往后移动
            tempNode=subNode;
            //设置结束的标识
            if(i==keyWord.length()-1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }
    /*
    * 过滤敏感词
    *
    * @param text可能有敏感词的待过滤文本
    * @return 过滤后的文本
    * */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //指针1，指向树，会不断移动一开始指向根
        TrieNode tempNode=rootNode;
        //指针2，指向text，指向一个检查词开头的字符，单向运动，
        int begin=0;
        //指针3指向结尾
        int position=0;
        //结果
        StringBuilder sub=new StringBuilder();
        while (position<text.length()){
            char c=text.charAt(position);
            //跳过符号，对付聪明人
            if(isSymbol(c)){
                //如果指针1处于根节点，就将其计入结果，让指针2向下走
                if(tempNode==rootNode){
                    sub.append(c);
                    begin++;
                }
                //指针3肯定走
                position++;
                continue;
            }
            //检查下级节点,有这个节点就是敏感词的一部分，没有就不是
            tempNode=tempNode.getSubNode(c);
            if(tempNode==null){
                //以begin开头的词不会是敏感词
                sub.append(text.charAt(begin));
                //begin下移
                position=++begin;
                //指针1重新进入更节点
                tempNode=rootNode;
            }else if(tempNode.isKeyWordEnd){
                //发现敏感词，需要将这一段的敏感词都替换掉
                sub.append(REPLACEMENT);
                begin=++position;
            }else {
                position++;
            }
        }
        //将最后一批计入结果
        sub.append(text.substring(begin));
        return sub.toString();
    }
    //判断是否为符号
    private boolean isSymbol(Character c){
        //0x2E80~0x9FFF是东亚文字
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9FFF);
    }
    //前缀数节点
    private class TrieNode{
        //关键词结束标记
        private boolean isKeyWordEnd=false;
        //子节点(key是下级节点字符，value是下级节点)
        private Map<Character,TrieNode> subNodes=new HashMap<>();
        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }
        //添加子节点方法
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }

}
