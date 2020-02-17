package com.cs.wujiuqi.data.crawler.blockinvoke;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class StringBlockStoppableIterator implements StoppableIterator<String> {
    private final static LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
    static {
        try {
            for (int i = 0; i <100 ; i++) {
                blockingQueue.put("http:/www.baicu.com?pageIndex="+i);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public  String next() throws IteratorStopException {
        try {
            String take = blockingQueue.take();
            if (take != this.toString()) {
                return take;
            } else {
                throw new IteratorStopException();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return blockingQueue.iterator().hasNext();
    }

    /**
     * 当停止标志位后，会等剩余任务执行完才会释放
     */
    @Override
    public void stop() {
        try {
            blockingQueue.put(this.toString());//设置特殊标志位
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当迭代器是阻塞迭代器时，则next()在没有元素情况下会阻塞当前线程
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        final StoppableIterator iterator = new StringBlockStoppableIterator();
        final ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 110; i++) {
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
        Thread.sleep(10000);
        System.out.println("10s后向blockingQueue添加10个元素，让等待线程继续执行");
        for (int i = 0; i <10 ; i++) {
            blockingQueue.put("http:/www.baicu.com?pageIndex=后加的"+i);
        }

    }
}
