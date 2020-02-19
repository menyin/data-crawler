package com.cs.wujiuqi.data.crawler.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * logback相关的properties配置是在
 */
public class Logs {
    public final static Logger HTTP ;
    public final static Logger SQL ;
    public final static Logger CONSOLE ;

    static {
        CONSOLE = LoggerFactory.getLogger("crawler.console");
        HTTP = LoggerFactory.getLogger("crawler.http.log");
        SQL = LoggerFactory.getLogger("crawler.sql.log");
    }
}
