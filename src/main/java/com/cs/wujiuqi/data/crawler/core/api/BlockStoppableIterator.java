package com.cs.wujiuqi.data.crawler.core.api;

import com.cs.wujiuqi.data.crawler.core.common.IteratorBlockEvent;

public interface BlockStoppableIterator<T> extends StoppableIterator {
    default void onBlocking(IteratorBlockEvent event){}
}
