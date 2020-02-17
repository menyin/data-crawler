package com.cs.wujiuqi.data.crawler.zhilian;

import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.common.IteratorStopEvent;
import com.cs.wujiuqi.data.crawler.core.exception.IteratorStopException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 智联顶层迭代器，循环执行所有条件组合，并且在产生
 * 注：因为我们条件中，大部分在128以内，地区数据量在300+，所以索引不用byte存储，而改用String存储
 */
public class ZhilianJobListUpIteratorBack implements StoppableIterator {
    /*模拟部分条件数据数据*/
    private static String[] S_SOU_WORK_CITY = {"530", "538", "763", "765"};//工作城市，北上广深
    private static String[] S_SOU_SALARY = {"01001,02000", "02001,04000", "04001,06000", "06001,08000"};//月薪
    private static String[][] CONDITIONS = {S_SOU_WORK_CITY, S_SOU_SALARY};//所有条件的二维数组
    private static String[] CONDITION_NAMES = {"S_SOU_WORK_CITY", "S_SOU_SALARY"};//各个条件的名称字符串

    /*每个实例都拥有一套条件指针*/
    private int[] conditionPointers = new int[CONDITIONS.length];//每个条件的指针
    private int limit = 0;//limit次迭代后会产生一次onStop()
    private int count = 0;//为了使得在limit个迭代后产生一次onStop()而准备的计数器
    private boolean isStop = false;

    public ZhilianJobListUpIteratorBack(int limit) {
        this.limit = limit;
    }

    @Override
    public Object next() throws IteratorStopException {
        synchronized (this.getClass()) {
            if (isStop || ((limit != 0 && count != 0 && count % limit == 0))) {//调用stop(),或指定周期都会
                isStop = false;
                onStop(new IteratorStopEvent(count));//触发迭代器停止事件 //下次next从这个count开始
                throw new IteratorStopException();
            }
            if (limit != 0) {
                count++;
            }
            return getItem();

        }
    }

    private Set<List<Character>> diacaers;

    /**
     * 测试迭代器
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        new ZhilianJobListUpIteratorBack(0).tesDicaer();
    }

    private List<String> dicaerList = new ArrayList<>();

    private void tesDicaer() throws InterruptedException {
          /*StoppableIterator<String> iterator = new ZhilianJobListUpIterator(30);
        while (true) {
            try {
                System.out.println(iterator.next());
            } catch (IteratorStopException e) {
                System.out.println("迭代器已经停止，我要通过EventBus通知");
                break;
            }
            Thread.sleep(500);
        }*/
//        List c1=Arrays.asList("1","2","3");
        List c1 = Arrays.asList("aaaaa", "bbbbbb", "ccccccc", "dddddd", "eeeeeee", "ffffff", "ggggggg", "hhhhhh", "iiiiiii", "jjjjjj");
        List c2 = Arrays.asList("111111", "222222", "3333333", "44444", "555555", "666666", "77777", "888888", "999999", "000000");
        List c3 = Arrays.asList("1111111", "2222221", "33333331", "444441", "5555551", "6666661", "777771", "8888881", "9999991", "0000001");
        List c4 = Arrays.asList("11111112", "22222212", "333333312", "4444412", "55555512", "66666612", "7777712", "88888812", "99999912", "00000012");
        List c5 = Arrays.asList("111111129", "222222128", "3333333127", "44444126", "555555125", "666666124", "77777123", "888888123", "999999123", "000000123");
        List c6 = Arrays.asList("1111111290", "2222221281", "33333331272", "444441263", "5555551254", "6666661245", "777771236", "8888881237", "9999991238", "0000001239");
        List c7 = Arrays.asList("11111112909", "22222212818", "333333312727", "4444412636", "55555512545", "66666612454", "7777712363", "88888812372", "99999912381", "00000012390");
        List c8 = Arrays.asList("111111129090", "222222128180", "333333312727", "4444412636", "55555512545", "66666612454", "7777712363", "88888812372", "99999912381", "00000012390");
        List c1Indexs1 = getIndexs(c1);
        List c1Indexs2 = getIndexs(c2);
        List c1Indexs3 = getIndexs(c3);
        List c1Indexs4 = getIndexs(c4);
        List c1Indexs5 = getIndexs(c5);
        List c1Indexs6 = getIndexs(c6);
        List c1Indexs7 = getIndexs(c7);
        List c1Indexs8 = getIndexs(c8);
        ImmutableSet<Character> c1IndexsChars1 = ImmutableSet.copyOf(c1);
        ImmutableSet<Character> c1IndexsChars2 = ImmutableSet.copyOf(c2);
        ImmutableSet<Character> c1IndexsChars3 = ImmutableSet.copyOf(c3);
        ImmutableSet<Character> c1IndexsChars4 = ImmutableSet.copyOf(c4);
        ImmutableSet<Character> c1IndexsChars5 = ImmutableSet.copyOf(c5);
        ImmutableSet<Character> c1IndexsChars6 = ImmutableSet.copyOf(c6);
        ImmutableSet<Character> c1IndexsChars7 = ImmutableSet.copyOf(c7);
        ImmutableSet<Character> c1IndexsChars8 = ImmutableSet.copyOf(c8);
        diacaers = Sets.cartesianProduct(c1IndexsChars1, c1IndexsChars2, c1IndexsChars3, c1IndexsChars4, c1IndexsChars5, c1IndexsChars6, c1IndexsChars7, c1IndexsChars8);
        System.out.println(diacaers.size());
        for (List<Character> characters : diacaers) {
            dicaerList.add(characters.toString());
//            System.out.println(characters);
        }
        Thread.sleep(Integer.MAX_VALUE);

    }

    private static List<String> getIndexs(List list) {
        List c1_indexs = new ArrayList();
        for (short i = 0; i < list.size(); i++) {
            c1_indexs.add(String.valueOf(i));
        }
        return c1_indexs;
    }

    private Object getItem() {
        String[] resultConditions = new String[CONDITIONS.length];

        for (int i = 0; i < conditionPointers.length; i++) {

            /*每次如果有一个条件的指针未到顶则移动指针一位*/
            if (conditionPointers[i] < CONDITIONS[i].length - 1) {
                conditionPointers[i]++;
            }

            /*判断是否全部条件都到最后一个了*/
            boolean isFull = true;
            for (int m = 0; m < conditionPointers.length; m++) {
                if (conditionPointers[m] != CONDITIONS[m].length - 1) {
                    isFull = false;
                    break;
                }
            }

            /*收集各个条件的值*/
            for (int j = 0; j < CONDITIONS.length; j++) {
                resultConditions[j] = CONDITION_NAMES[j] + "=" + CONDITIONS[j][conditionPointers[j]];
            }
            /*到达终点，则指针全部重置为0*/
            if (isFull) {
                conditionPointers = new int[CONDITIONS.length];
                System.out.println("--------------------");
            }
            return String.join("&", resultConditions);//有一个条件没有叠加就叠加，直接返回

        }
        return resultConditions;
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
