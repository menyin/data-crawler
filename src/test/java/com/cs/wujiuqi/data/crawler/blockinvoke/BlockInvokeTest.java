package com.cs.wujiuqi.data.crawler.blockinvoke;

import com.cs.wujiuqi.data.crawler.flowController.FlowController;

import java.util.concurrent.*;

public class BlockInvokeTest implements Runnable {
    /*这些对象都是单例*/
    private static int rate = 5;//每秒5个
    private static ExecutorService executorService = Executors.newFixedThreadPool(100);
    private static LinkedBlockingQueue<String> blockingQueueUp = new LinkedBlockingQueue<String>();
    private static LinkedBlockingQueue<String> blockingQueueDown = new LinkedBlockingQueue<String>();//下游队列无capacity，即添加不限，获取受限。
    private static StoppableIterator<String> stoppableIterator;//遍历要处理的任务，如果iterator里包含了阻塞队列则next();//必须保证线程安全
    private static FlowController flowController;//流控器
    private volatile boolean isRunning = true;

    public static void main(String[] args) throws InterruptedException {
        blockingQueueDown.iterator().next();
        System.out.println("不阻塞");
//        new BlockInvokeTest().start();
    }

    public void start() {
        executorService.submit(this, 1);//主线程，第二参数是为了区分Callable接口
    }

    public void stop() {
        isRunning = false;
    }

    /**
     * 主线程执行流控代码
     */
    @Override
    public void run() {
        while (isRunning) {
            String url = null;//如果是阻塞的迭代器则可能阻塞,因为其包含BlockingQueue
            try {
                 stoppableIterator.next();
            } catch (IteratorStopException e) {//
                System.out.println("当前节点要停止了，我记录下日志");
                isRunning=false;
                break;
            }
            try {
                if (flowController.checkQps()) {
                    executorService.submit(new NodeTask(url));
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








}
