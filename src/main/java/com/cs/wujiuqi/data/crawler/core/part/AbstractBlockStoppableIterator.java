package com.cs.wujiuqi.data.crawler.core.part;

import com.cs.wujiuqi.data.crawler.core.api.BlockStoppableIterator;
import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.common.IteratorBlockEvent;
import com.cs.wujiuqi.data.crawler.core.common.IteratorStopEvent;
import com.cs.wujiuqi.data.crawler.core.exception.IteratorStopException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 要求T类型必须有无参数构造函数，否则抛出异常
 *
 * @param <T>
 */
public abstract class AbstractBlockStoppableIterator<T> implements BlockStoppableIterator<T> {
    protected LinkedBlockingQueue<T> blockingQueue;
    private T lastObj;
    private T isStopObj;

//    private volatile boolean isExcuteStop=false;//为保证stop()之后的hasNext()是安全准确的

    public AbstractBlockStoppableIterator() {
        Type genType = this.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Class clazz = (Class) params[0];
        try {
            isStopObj = (T) clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        getBlockingQueue();
    }

    public abstract LinkedBlockingQueue<T> getBlockingQueue();

    @Override
    public T next() throws IteratorStopException {

        synchronized (this.getClass()) {
            try {
                if(blockingQueue.size()==0){
                    onBlocking(new IteratorBlockEvent(Thread.currentThread()));//如果队列为空了，则准备要阻塞了
                }
                T take = blockingQueue.take();
                if (take.hashCode() != isStopObj.hashCode()) {
                    lastObj=take;//记录阻塞前最后一个对象
                    return take;
                } else {
                    onStop(new IteratorStopEvent(lastObj));
                    throw new IteratorStopException();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    /**
     * 只表示迭代器中一瞬间还有元素
     *
     * @return
     */
    @Override
    public boolean hasNext() {
        return blockingQueue.size() != 0 || (blockingQueue.size() == 1 && !blockingQueue.contains(isStopObj));
    }

    /**
     * 只代表通知迭代器在当前时间点前的元素消费完就停止了
     */
    @Override
    public void stop() {
        try {
            blockingQueue.put(isStopObj);//设置特殊标志位
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
