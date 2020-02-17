package com.cs.wujiuqi.data.crawler.core.api;

import com.cs.wujiuqi.data.crawler.core.common.IteratorStopEvent;
import com.cs.wujiuqi.data.crawler.core.exception.IteratorStopException;

/**
 * 阻塞迭代器
 * 所有方法的安全性由具体实现类保证
 * @param <T>
 */
public interface StoppableIterator<T> {
    /**
     * 当无元素则会阻塞
     * @return T 类型对象
     * @throws IteratorStopException
     * 当调用stop()方法后，在next()调用处会抛出BlockingIteratorStopException异常
     * 在next()要退出迭代器也可以直接调用stop()，此时next()会抛异常退出迭代器
     */
    T next() throws IteratorStopException;
    boolean hasNext();
    void stop();
    default void onStop(IteratorStopEvent event){}
}
