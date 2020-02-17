package com.cs.wujiuqi.data.crawler.core.part;

import com.cs.wujiuqi.data.crawler.core.api.FlowController;
import com.cs.wujiuqi.data.crawler.core.api.Plant;
import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.exception.IteratorStopException;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * 单分支处理
 * @param <T>
 * @param <E>
 */
public abstract class AbstractSinglePlant<T,E> implements Plant,Consumer<E> {
   /* final static private ExecutorService executorService= Executors.newCachedThreadPool();
    private volatile boolean isRunning = true;
    private StoppableIterator<T> upIterator;
    private StoppableIterator<E> downIterator;
    private FlowController flowController;

    //    protected abstract void setExecutorService(ExecutorService executorService);
    @Override
    public void setUpIterator(StoppableIterator upIterator) {
        this.upIterator = upIterator;
    }

    @Override
    public StoppableIterator getDownIterator() {
        return downIterator;
    }

    @Override
    public void setFlowController(FlowController flowController) {
        this.flowController = flowController;
    }

    *//**
     * 该方法可被子类在被重写
     *//*
    @Override
    public void run() {
        while (isRunning) {
            T item;//如果是阻塞的迭代器则可能阻塞,因为其包含BlockingQueue
            try {
                item= upIterator.next();
            } catch (IteratorStopException e) {//
                System.out.println("当前节点要停止了，我记录下日志");
                isRunning=false;
                break;
            }
            try {
                if (flowController.checkQps()) {
                    executorService.submit(new InnerTask(item,this));
                } else {
                    try {
                        wait(flowController.getDelay());
                    } catch (InterruptedException e) {
                        isRunning = false;//标志位设置为false，线程不再运行
                        System.out.println("handler主线程出现异常：" + e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        downIterator.stop();//下游迭代器关闭
        isRunning = false;//当前任务结束标志位重置
    }

    @Override
    public void start() {
        this.executorService.submit(this);
    }


    public class InnerTask<E> implements Runnable{
        private E item;
        private Consumer<E> consumer;
        public InnerTask(E item,Consumer<E> consumer) {
            this.item = item;
            this.consumer=consumer;
        }
        @Override
        public void run() {
            this.consumer.accept(item);
        }
    }*/
}
