
package com.cs.wujiuqi.data.crawler.core.common;

public abstract class EventConsumer {

    public EventConsumer() {
        EventBus.register(this);
    }

}
