package com.cs.wujiuqi.data.crawler.blockinvoke;

import java.util.concurrent.Callable;

public class NodeTask implements Callable<String> {
    private String url;

    public NodeTask(String url) {
        this.url = url;
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(1000);
        System.out.println("HttpClient.execute()//拿着url去请求数据");
        return null;
    }
}