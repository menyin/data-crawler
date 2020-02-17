package com.cs.wujiuqi.data.crawler.zhilian;

import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.common.IteratorStopEvent;
import com.cs.wujiuqi.data.crawler.core.exception.IteratorStopException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

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
    private static String BASE_URL="https://capi.zhaopin.com/capi/position/search?component=true";
    private static Map<String, List<String>> CONDITIONS = new HashMap<>();

    static {
        CONDITIONS.put("S_SOU_WORK_CITY", Arrays.asList(new String[]{"530", "538", "763", "765"}));//工作城市，北上广深
        CONDITIONS.put("S_SOU_SALARY", Arrays.asList(new String[]{"01001,02000", "02001,04000", "04001,06000", "06001,08000"}));//月薪
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
                System.out.println("一轮结束---------------------");
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
            StoppableIterator si=new ZhilianJobListUpIterator();
            while (true){
                System.out.println(si.next());
            }
        } catch (IteratorStopException e) {
            e.printStackTrace();
        }
    }


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
}
