package com.cs.wujiuqi.data.crawler.core.common;

import com.cs.wujiuqi.data.crawler.core.api.Event;

public class IteratorBlockEvent implements Event {
    private String msg;
    private Object eventObj;
    public IteratorBlockEvent(Object eventObj) {
        this(eventObj,null);
    }
    public IteratorBlockEvent(Object eventObj,String msg) {
        this.msg = msg;
        this.eventObj = eventObj;
    }
}
