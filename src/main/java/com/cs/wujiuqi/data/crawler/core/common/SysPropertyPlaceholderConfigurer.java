package com.cs.wujiuqi.data.crawler.core.common;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.Properties;

/**
 * 注入了properties文件内容
 */
public class SysPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private Properties properties;
    @Override
    protected void loadProperties(Properties props) throws IOException {
        super.loadProperties(props);
        System.setProperty("log.home", props.getProperty("log.home"));
        System.setProperty("log.root.level",props.getProperty("log.root.level"));
//        System.setProperty("logback.configurationFile",props.getProperty("logback.configurationFile"));
        properties=props;
    }

    public Properties getProperties() {
        return properties;
    }
}
