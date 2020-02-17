
package com.cs.wujiuqi.data.crawler.core.common;

import com.cs.wujiuqi.data.crawler.core.api.Event;
import com.google.common.eventbus.AsyncEventBus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 */
public class EventBus {
//    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
    private static com.google.common.eventbus.EventBus eventBus;
    private static Executor executor;
    static {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-context.xml");
        executor = (Executor)applicationContext.getBean("executeService");
        eventBus = new AsyncEventBus(executor, (exception, context)-> {
            /*LOGGER.error("event bus subscriber ex", exception)*/
        });
    }

    public static void post(Event event) {
        eventBus.post(event);
    }

    public static void register(Object bean) {
        eventBus.register(bean);
    }

    public static void unregister(Object bean) {
        eventBus.unregister(bean);
    }

}
