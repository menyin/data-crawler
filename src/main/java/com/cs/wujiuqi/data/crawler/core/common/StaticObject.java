package com.cs.wujiuqi.data.crawler.core.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StaticObject {
    public final static ApplicationContext AP = new ClassPathXmlApplicationContext("spring-*.xml");

}
