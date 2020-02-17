package com.cs.wujiuqi.data.crawler.core.part;

import com.cs.wujiuqi.data.crawler.core.api.FlowController;
import com.cs.wujiuqi.data.crawler.core.api.Plant;
import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.exception.IteratorStopException;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * 多分支处理
 * 此类实现存在问题：
 *   *当具体任务线程直接使用upIterator.next()，
 *        *如果元素了，则有可能很多线程被阻塞住。
 *        *另外比较难通知流控线程站暂停循环
 *
 */
public abstract class AbstractMultiPlantBack implements Plant {
    final static private ExecutorService executorService = Executors.newCachedThreadPool();
//    final static private ExecutorService executorService = Executors.newScheduledThreadPool(100);
//    private volatile boolean isRunning = true;
    private Map<String, StoppableIterator> upIterators;
    private Map<String, StoppableIterator> downIterators;
    private Map<String, FlowController> flowControllers;
    private Map<String, Consumer> consumers;
//    private AtomicReference<Map<String,Boolean>> isRunnings;//TODO


    @Override
    public void stop() {
        for (Map.Entry<String, StoppableIterator> entry : downIterators.entrySet()) {
            StoppableIterator downIterators = entry.getValue();
            downIterators.stop();//调用迭代器stop()就可以让所在主线程退出循环
        }
//        isRunning = false;//当前任务结束标志位重置
    }

    @Override
    public void start() {
        for (Map.Entry<String, Consumer> entry : consumers.entrySet()) {
            String key = entry.getKey();
            final Consumer consumer = entry.getValue();
            final StoppableIterator upIterator = upIterators.get(key);
            final FlowController flowController = flowControllers.get(key);
//            this.executorService.submit(new Runnable() {
            Thread mainThread=new Thread(new Runnable() {
                @Override
                public void run() {
//                    Lock lockF = new ReentrantLock();
                    Lock lock = new ReentrantLock();
                    Condition condition=lock.newCondition();
//                    Condition conditionF=lock.newCondition();
//                    while (isRunning) {//标志位要每个类型一个
                    while (true) {//循环退出依靠迭代器抛出IteratorStopException异常

                        for (int i = 0; i < 200; i++) {
                            try {
                                if (flowController.checkQps()) {
                                    Runnable runnable= new InnerTask( consumer,upIterator);
//                                executorService.submit(runnable);//消费掉进行消费
                                    Thread thread=new Thread(runnable,"Cny-T-Task");//消费掉进行消费
                                    thread.setDaemon(true);
                                    thread.start();
//                                    System.out.println("thread.start();"+item);
//                                conditionF.await();
                                } else {

                                }
                            } catch (Exception e) {
//                            System.out.println("记录日志"+e);
                                e.printStackTrace();
                            }
                        }
                        try {
//                                  executorService.awaitTermination(flowController.getDelay(), TimeUnit.MICROSECONDS);
                            lock.lock();
                            condition.await(1, TimeUnit.SECONDS);//时间是纳秒,JDK自带阻塞无法实现纳秒级别阻塞
                            lock.unlock();
                        } catch (InterruptedException e) {
//                                    isRunning = false;//标志位设置为false，线程不再运行
                            System.out.println("handler主线程出现异常：" + e);
                        }
                    }
                }
            },"T-main");
            mainThread.setDaemon(true);
            mainThread.start();

        }
    }



    public static class InnerTask implements Runnable {
//        private Object item;
        private Consumer consumer;
        private StoppableIterator upIterator;

        public InnerTask(Consumer consumer,StoppableIterator upIterator) {
            this.consumer = consumer;
            this.upIterator=upIterator;
        }

        @Override
        public void run() {
            try {
                if(upIterator.hasNext()){//不用这个判断，则当upIterator没元素了，会有很多任务线程被阻塞
                    this.consumer.accept(upIterator.next());
                }
            } catch (IteratorStopException e) {
                System.out.println("迭代器已经完了~ ");
                return;
            }

        }

        public static Object clone(Object object){
            try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(object);//序列化
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            return oi.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

          return null;
        }
    }


    public static ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * 实际上无法判断当前任务是否还在运行，因为线程池取的线程，
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
