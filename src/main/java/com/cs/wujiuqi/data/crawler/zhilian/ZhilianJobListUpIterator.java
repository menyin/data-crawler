package com.cs.wujiuqi.data.crawler.zhilian;

import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.common.IteratorStopEvent;
import com.cs.wujiuqi.data.crawler.core.common.JsonUtil;
import com.cs.wujiuqi.data.crawler.core.common.Logs;
import com.cs.wujiuqi.data.crawler.core.exception.IteratorStopException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 智联顶层迭代器，循环执行所有条件组合，并且在产生
 * 注：因为我们条件中，大部分在128以内，地区数据量在300+，所以索引不用byte存储，而改用String存储
 * 注：condition+pageIndex，有大部分页面的pageIndex到不了50，所以检测pageIndex的有效性是当前迭代器的主要任务
 * 注：每次pageIndex之前测试下总共有几条数据
 * 注：笛卡尔积耗内存问题
 */
public class ZhilianJobListUpIterator implements StoppableIterator<String> {
    /*模拟部分条件数据数据*/
    private static Set<List<Character>> CONDITION_CARTEINS;
    private Iterator<List<Character>> conditionCarteinsIterator;
//    private static String BASE_URL="https://capi.zhaopin.com/capi/position/search?component=true";
    private static String BASE_URL="https://capi.zhaopin.com/capi/position/searchV2?platform=12&at=30ee350c76ca4d6ba4d452600071cca7&rt=9fb809cf4f1943beba823653c9211587";
    private static Map<String, List<String>> CONDITIONS = new HashMap<>();

    static {
        /*条件初始化*/
        initConditions();
        /*笛卡尔积生成*/
        List list = new ArrayList<ImmutableSet<Character>>();
        for (Map.Entry<String, List<String>> condition : CONDITIONS.entrySet()) {
            ImmutableSet<Character> character = ImmutableSet.copyOf((List) condition.getValue());
            list.add(character);
        }
        CONDITION_CARTEINS = Sets.cartesianProduct(list);
    }

    /*每个实例都拥有一套条件指针*/
    private int limit = 0;//limit次迭代后会产生一次onStop()
    private int count = 0;//为了使得在limit个迭代后产生一次onStop()而准备的计数器
    private boolean isStop = false;
    public ZhilianJobListUpIterator() {
        conditionCarteinsIterator=CONDITION_CARTEINS.iterator();
    }
    public ZhilianJobListUpIterator(int limit) {
        this.limit = limit;
    }

    @Override
    public String next() throws IteratorStopException {
        synchronized (this.getClass()) {
            if (conditionCarteinsIterator.hasNext()) {
                List<Character> conditions = conditionCarteinsIterator.next();
                Iterator<String> keyIterator=CONDITIONS.keySet().iterator();//用迭代器拿出key
                Iterator<Character> conditionVal=conditions.iterator();
                List<String> resultList = new ArrayList<>();
                while (keyIterator.hasNext()&&conditionVal.hasNext()){
                    resultList.add("&"+keyIterator.next()+"="+conditionVal.next());
                }

                return BASE_URL+String.join("",resultList);
            }
            else {
                conditionCarteinsIterator=CONDITION_CARTEINS.iterator();
                Logs.CONSOLE.info("-------------ZhilianJobListUpIterator 一轮迭代结束--------------");
                return   next();
            }

        }
    }


    /**
     * 测试迭代器
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        try {
            StoppableIterator si=new ZhilianJobListUpIterator();//ZhilianJobListUpIterator是无穷循环迭代的
            long startT = System.currentTimeMillis();
            while (si.hasNext()){
//                System.out.println(si.next());
                si.next();
            }
            System.out.println(System.currentTimeMillis() - startT);
        } catch (IteratorStopException e) {
            e.printStackTrace();
        }

    /*    long startT = System.currentTimeMillis();
        System.out.println(getAreaList());
//        initConditions();
        System.out.println(System.currentTimeMillis() - startT);*/

//        testCartesin();
    }

   /* public static void testCartesin(){
        *//*笛卡尔积生成*//*
        List list = new ArrayList<ImmutableSet<Character>>();
        list.add(ImmutableSet.copyOf(Arrays.asList("a","b","c","d")));
        list.add(ImmutableSet.copyOf(Arrays.asList("1","2","3","4")));
        Set set = Sets.cartesianProduct(list);
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }

    }
*/
    @Override
    public boolean hasNext() {
        return true; //因为是循环迭代的，所以这里面一直有值
    }

    @Override
    public void stop() {
        isStop = true;
    }

    @Override
    public void onStop(IteratorStopEvent event) {
//        EventBus.post(event);//事件总线通知//TODO
    }

    /**
     * 通过js脚本生成地区code以逗号分隔的字符串
     * 再通过文件加载字符串文件转为List<String>
     * @return
     */
    private static List<String> getAreaList(){
        File file = new File("data/zhilian.area.js");
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while((size = in.read(temp)) != -1){
                out.write(temp, 0, size);
            }
            byte[] content = out.toByteArray();
            String areasStr=new String(content);
            String[] areas = areasStr.split(",");
            return Arrays.asList(areas);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static void initConditions(){
        try {
           Files.asCharSource(new File("data/zhilian.conditions.js"), Charset.defaultCharset())
                    .readLines(new LineProcessor<String>() {
                        public boolean processLine(String line) throws IOException {
                            //这里处理一行字符串
//                            System.out.println(line);
                            String[] split = line.split("=");
                            String key = split[0];
                            List<String> values=Arrays.asList(split[1].split(","));
                            CONDITIONS.put(key,values);
                            return true;//如果是false，则会中断读取操作
                        }

                        public String getResult() {
                            return null;//返回的结果。可以自定义
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
