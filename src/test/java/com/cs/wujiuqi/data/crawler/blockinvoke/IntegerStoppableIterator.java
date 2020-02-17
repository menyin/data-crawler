package com.cs.wujiuqi.data.crawler.blockinvoke;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 计数器式的迭代器
 */
public class IntegerStoppableIterator implements StoppableIterator<Integer> {
    private AtomicInteger count;
    private int num;

    public IntegerStoppableIterator(int num) {
        this.num=num;
        count = new AtomicInteger(num);
    }

    @Override
    public Integer next() throws IteratorStopException {
        int i = count.decrementAndGet();
        if(i<-1){
            throw new IteratorStopException();
        }
        if (i == 0) {
            stop();
        }
        return num-i;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void stop() {
        count.set(-1);
    }

    public static void main(String[] args) {
       final StoppableIterator iterator = new IntegerStoppableIterator(80);
       final ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 100; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(iterator.next());
                    } catch (IteratorStopException e) {
                        System.out.println("迭代器遍历完，已退出,自行处理逻辑，或者直接丢弃");
                        executor.shutdown();//线程池退出
                    }
                }
            });
        }
    }
}
