package com.cs.wujiuqi.data.crawler.core.part;

import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.exception.IteratorStopException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class StringAbstractBlockStoppableIterator extends AbstractBlockStoppableIterator<String> {

    public StringAbstractBlockStoppableIterator(LinkedBlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public LinkedBlockingQueue<String> getBlockingQueue() {
        return this.blockingQueue;
    }


    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue queue = new LinkedBlockingQueue<String>();
        for (int i = 0; i < 10; i++) {
            queue.put("item" + i);
        }
        final StoppableIterator<String> iterator = new StringAbstractBlockStoppableIterator(queue);
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < 20; i++) {
            executorService.submit(() -> {
                try {
                    String item = iterator.next();
                    System.out.println(item);
                } catch (IteratorStopException e) {
                    System.out.println("迭代器停止了");
                }
            });

            /*executorService.submit(()->{

            });*/
        }
        Thread.sleep(10000);
        System.out.println("iterator.stop();");
        iterator.stop();

    }
}
