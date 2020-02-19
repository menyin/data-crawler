package com.cs.wujiuqi.data.crawler.core.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

/**
 * AP是spring容器
 * PP是AP加载的properties文件
 */
public class StaticObject {
    public final static ClassPathXmlApplicationContext AP = new ClassPathXmlApplicationContext("spring-*.xml");
/*    public final static Properties PP;
    static {
        SysPropertyPlaceholderConfigurer propertyConfig=(SysPropertyPlaceholderConfigurer)AP.getBean("propertyConfig");
        PP=propertyConfig.getProperties();
    }*/
}
