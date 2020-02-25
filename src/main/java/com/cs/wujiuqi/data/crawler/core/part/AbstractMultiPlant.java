package com.cs.wujiuqi.data.crawler.core.part;

import com.cs.wujiuqi.data.crawler.core.api.FlowController;
import com.cs.wujiuqi.data.crawler.core.api.Plant;
import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.common.EventBus;
import com.cs.wujiuqi.data.crawler.core.common.EventConsumer;
import com.cs.wujiuqi.data.crawler.core.common.Logs;
import com.cs.wujiuqi.data.crawler.core.common.RetryEvent;
import com.cs.wujiuqi.data.crawler.core.exception.IteratorStopException;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * 多分支处理
 */
public abstract class AbstractMultiPlant extends EventConsumer implements Plant {
    //    final static private ExecutorService executorService = Executors.newCachedThreadPool();
    @Autowired
    protected ExecutorService executorService=Executors.newCachedThreadPool(1111);
    //        final static private ExecutorService executorService = Executors.newScheduledThreadPool(100);
//    private volatile boolean isRunning = true;
    private volatile boolean isCatchQpsExection = false;
    private Map<String, StoppableIterator> upIterators;
    private Map<String, StoppableIterator> downIterators;
    private Map<String, FlowController> flowControllers;
    private Map<String, Consumer> consumers;
    private Map<String, AtomicBoolean> isRetrys = new HashMap<>();//是否重试
    private Map<String, AtomicLong> retryWaitMinutes = new HashMap<>();//重试时间间隔

    protected AbstractMultiPlant() {
        init();
    }

    protected void init() {
        this.downIterators = initDownIterators();
        this.upIterators = initUpIterators();
        this.flowControllers = initFlowControllers();
        this.consumers = initConsumers();
    }

    @Override
    public void stop() {
        for (Map.Entry<String, StoppableIterator> entry : downIterators.entrySet()) {
            StoppableIterator downIterators = entry.getValue();
            downIterators.stop();//调用迭代器stop()就可以让所在主线程退出循环
        }
//        isRunning = false;//当前任务结束标志位重置
        Logs.CONSOLE.info("plant-{} stop,it's downIterators stop both",this.getClass().getName());
    }

    @Override
    public void start() {

        for (Map.Entry<String, Consumer> entry : consumers.entrySet()) {
            String chainKey = entry.getKey();
            final Consumer consumer = entry.getValue();
            final StoppableIterator upIterator = upIterators.get(chainKey);
            final FlowController flowController = flowControllers.get(chainKey);
            final AtomicBoolean isRetry = new AtomicBoolean(false);
            isRetrys.put(chainKey, isRetry);
            final AtomicLong retryWaitMinute = new AtomicLong(10);
            retryWaitMinutes.put(chainKey, retryWaitMinute);
            this.executorService.submit(new Runnable() {
                //            new Thread(new Runnable() {
                @Override
                public void run() {
                    flowController.reset();
                    Lock lock = new ReentrantLock();
                    Condition condition = lock.newCondition();
//                    doStart(lock, condition, flowController, consumer, upIterator, null);
                    long startT = System.currentTimeMillis();

                    Object item = null;
                    while (true) {
                        lock.lock();
                        if (isRetry.get()) {//有一个任务线程请求10分钟重试,
                            try {
//                                System.out.println("//有一个任务线程请求10后重试,.....");
//                                condition.await(retryWaitMinute.longValue(),TimeUnit.MINUTES);
                                condition.await(retryWaitMinute.longValue(), TimeUnit.SECONDS);
                                isRetry.compareAndSet(true, false);
                                lock.unlock();
                                Logs.CONSOLE.debug("have a thread catch retry message after {}minutes",retryWaitMinute);
                                continue;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        lock.unlock();

                        try {
                            if (item == null) {
                                item = upIterator.next();
                            }
//                            if (item.toString().equals("9999")) break;
                        } catch (IteratorStopException e) {//
                            Logs.CONSOLE.debug("current iterator be closed,use times={}",System.currentTimeMillis() - startT);
                            break;//递归退出依靠迭代器抛出IteratorStopException异常
//                            return;
                        }
                        try {
                            if (flowController.checkQps()) {
                                executorService.submit(new InnerTask(item, consumer));//消费掉进行消费
//                new Thread(new InnerTask(item, consumer)).start();//消费掉进行消费
                                Logs.CONSOLE.debug("add new task to executorService,item={},consumer={}",item,consumer);
                                item = null;
                            } else {

                                try {

//                                    executorService.awaitTermination(flowController.getDelay(), TimeUnit.MICROSECONDS);
                                    lock.lock();
                                    Logs.CONSOLE.debug("have a new task add to executorService delay,item={},consumer={}",item,consumer);
                                    condition.awaitNanos(flowController.getDelay());//时间是纳秒,JDK自带阻塞无法实现纳秒级别阻塞
                                    lock.unlock();
                                } catch (InterruptedException e) {
//                                    isRunning = false;//标志位设置为false，线程不再运行
                                    Logs.CONSOLE.error("{} durning a new task add to executorService,throw a exception,e={}",AbstractMultiPlant.class,e);
                                }
                            }
                            continue;
                        } catch (Exception e) {
                            Logs.CONSOLE.error("{} durning a new task add to executorService,throw a exception,e={}",AbstractMultiPlant.class,e);
                        }
                    }
                    long endT = System.currentTimeMillis();
                    Logs.CONSOLE.error("total tasks add to executorService,total use time={}ms",endT - startT);

                }
            });
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    void on(RetryEvent retryEvent) {
        AtomicBoolean isRetry = isRetrys.get(retryEvent.getChainKey());
        isRetry.compareAndSet(false, true);//设置重试
        if (retryEvent.getRetryWaitMinute() != null) {
            AtomicLong retryWaitMinute = retryWaitMinutes.get(retryEvent.getChainKey());
            retryWaitMinute.set(retryEvent.getRetryWaitMinute().longValue());
        }

    }

    public static class InnerTask<E> implements Runnable {
        private E item;
        private Consumer<E> consumer;
        private Condition condition;

        public InnerTask(E item, Consumer<E> consumer) {
//            System.out.println(System.nanoTime() + "--item=" + item);
//            this.item = (E)InnerTask.clone(item);
            this.item = item;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            this.consumer.accept(this.item);
        }


    }

    public abstract Map<String, StoppableIterator> initDownIterators();

    protected abstract Map<String, StoppableIterator> initUpIterators();

    protected abstract Map<String, FlowController> initFlowControllers();

    protected abstract Map<String, Consumer> initConsumers();

    public ExecutorService getExecutorService() {
        return executorService;
    }

    protected static void triggerRetry(RetryEvent RetryEvent) {
        EventBus.post(RetryEvent);
    }

    /**
     * 实际上无法判断当前任务是否还在运行，因为线程池取的线程，
     *
     * @param upIterators
     */
    /*public boolean isRunning() {
        return isRunning;
    }*/
    @Override
    public void setUpIterators(Map<String, StoppableIterator> upIterators) {
        this.upIterators = upIterators;
    }

    @Override
    public Map<String, StoppableIterator> getDownIterators() {
        return downIterators;
    }

    @Override
    public void setFlowControllers(Map<String, FlowController> flowControllers) {
        this.flowControllers = flowControllers;
    }

    public Map<String, StoppableIterator> getUpIteratorss() {
        return upIterators;
    }

    public void setDownIterators(Map<String, StoppableIterator> downIterators) {
        this.downIterators = downIterators;
    }

    public Map<String, StoppableIterator> getUpIterators() {
        return upIterators;
    }

    public Map<String, FlowController> getFlowControllers() {
        return flowControllers;
    }

    public Map<String, Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(Map<String, Consumer> consumers) {
        this.consumers = consumers;
    }
}
