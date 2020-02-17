package com.cs.wujiuqi.data.crawler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerformanceTest {
    final static private ExecutorService executorService = Executors.newFixedThreadPool(10000);

    /**
     * 用线程池和不用线程池对比
     * 不用线程池7958 相差不大
     * 用线程池9322ms左右
     * @param args
     */
    public static void main(String[] args) {
        userExecutor();
//        userNewThread();
    }
    public static void userExecutor(){
        long startT = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            final int ii=i;
            executorService.execute(()->{
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ii==9999){
                    long endT = System.currentTimeMillis();
                    System.out.println( "总耗时：" + (endT - startT));
                }
            });
        }

    }
   /* public static void userNewThread(){
        long startT = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            final int ii=i;
            new Thread(()->{
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ii==9999){
                    long endT = System.currentTimeMillis();
                    System.out.println( "总耗时：" + (endT - startT));
                }
            }).start();
        }

    }*/
}
