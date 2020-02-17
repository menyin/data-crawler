package com.cs.wujiuqi.data.crawler.core.part;

import java.util.concurrent.LinkedBlockingQueue;

public class IntegerAbstractBlockStoppableIterator extends AbstractBlockStoppableIterator<Integer> {
    public IntegerAbstractBlockStoppableIterator(Integer count) {
        this.blockingQueue=new LinkedBlockingQueue();
        for (Integer i = 0; i <count ; i++) {
            try {
                this.blockingQueue.put(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public LinkedBlockingQueue getBlockingQueue() {
        return blockingQueue;
    }
}
