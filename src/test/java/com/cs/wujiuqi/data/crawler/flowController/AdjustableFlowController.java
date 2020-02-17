package com.cs.wujiuqi.data.crawler.flowController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实验直接修改limit以改变qps的可行性，实践证明会出现问题，详见main
 */
public class AdjustableFlowController implements FlowController {
    private int limit;
    private final int maxLimit;
    private final long duration;
    private final AtomicInteger count = new AtomicInteger();
    private final AtomicInteger total = new AtomicInteger();
    private final long start0 = System.nanoTime();
    private volatile long start;

    public AdjustableFlowController(int qps) {
        this(qps, Integer.MAX_VALUE, 1000);
    }

    public AdjustableFlowController(int limit, int maxLimit, int duration) {
        this.limit = limit;
        this.maxLimit = maxLimit;
        this.duration = TimeUnit.MILLISECONDS.toNanos(duration);
    }

    @Override
    public void reset() {
        count.set(0);
        start = System.nanoTime();
    }

    @Override
    public int total() {
        return total.get();
    }

    @Override
    public boolean checkQps() throws OverFlowException {
        if (count.incrementAndGet() < limit) {
            total.incrementAndGet();
            return true;
        }

//        if (maxLimit > 0 && total.get() > maxLimit) throw new OverFlowException(true);
        if (maxLimit > 0 && total.get() > maxLimit) throw new OverFlowException();

        if (System.nanoTime() - start > duration) {
            reset();
            total.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public long getDelay() {
        return duration - (System.nanoTime() - start);
    }

    @Override
    public int qps() {
        return (int) (TimeUnit.SECONDS.toNanos(total.get()) / (System.nanoTime() - start0));
    }

    @Override
    public String report() {
        return String.format("total:%d, count:%d, qps:%d", total.get(), count.get(), qps());
    }


    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public AtomicInteger getCount() {
        return count;
    }


    /**
     * 测试多线程下，qps是否可改
     * 测试结果：在limit为普通成员变量时，容易出现"limit:6,count:7"，即在某瞬间，实际qps是比更改后的limit大
     * 结论：因为limit在内存分配堆中，而且是共享，存在线程安全问题，在checkQps()里的方法没法保证线程安全，
     *       除非limit是每次实时从jvm内存之外的地方获取，比如properties或redis，显然Redis是安全的
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        final AdjustableFlowController fc = new AdjustableFlowController(10);
        for (int i = 0; i < 150; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    boolean flag = true;
                    while (flag) {
                        try {
                            if (fc.checkQps()) {
                                flag = false;
                                System.out.println("limit:" + fc.getLimit() + ",count:" + fc.getCount());
                            } else {
                                Thread.sleep(1000);//延迟再执行
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
        int ii = 6;
        for (int i = 6; i > 0; i--) {
            Thread.sleep(1000);
            fc.setLimit(i);
        }

    }
}
