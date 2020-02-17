package com.cs.wujiuqi.data.crawler.core.common;

import com.cs.wujiuqi.data.crawler.core.api.Event;

public class IteratorStopEvent implements Event {
    private String msg;
    private Object eventObj;
    public IteratorStopEvent(Object eventObj) {
        this(eventObj,null);
    }
    public IteratorStopEvent(Object eventObj,String msg) {
        this.msg = msg;
        this.eventObj = eventObj;
    }
}
