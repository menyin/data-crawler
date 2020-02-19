package com.cs.wujiuqi.data.crawler;

import com.cs.wujiuqi.data.crawler.core.common.Logs;

public class LogTest {
    public static void main(String[] args) {
        Logs.CONSOLE.debug("进入到main方法");
        Logs.HTTP.debug("执行http请求");
        Logs.SQL.debug("执行sql保存");
    }
}
