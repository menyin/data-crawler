package com.cs.wujiuqi.data.crawler;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorTest4List {
    final static CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    static {
//实例化keep-alive策略
    /*ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase
                        ("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return 60 * 1000;//如果没有约定，则默认定义时长为60s
        }
    };
    httpClient.setKeepAliveStrategy*/
    }

    final static String[] jobs = ("CZ548654280J00100542611,CC300255137J00144014409,CC300255137J00132117209,CC300255137J00138524909,CC300255137J00140970009,CC283895481J00455369101,CC120866805J00362456101,CC300255137J00157899809,CC160036910J00188783004,CC522483588J00075973607,CC160036910J90250460000,CC348894235J00212669608,CC810042100J00193324409,CC827765150J00134118204,CC347464613J90250296000,CCL1219714590J00338390004,CC300255137J00139222309,CC871514410J00304941602,CCL1200214270J00302686504,CC603166727J00131482512," + "CZ548654280J00100542611,CC300255137J00144014409,CC300255137J00132117209,CC300255137J00138524909,CC300255137J00140970009,CC283895481J00455369101,CC120866805J00362456101,CC300255137J00157899809,CC160036910J00188783004,CC522483588J00075973607,CC160036910J90250460000,CC348894235J00212669608,CC810042100J00193324409,CC827765150J00134118204,CC347464613J90250296000,CCL1219714590J00338390004,CC300255137J00139222309,CC871514410J00304941602,CCL1200214270J00302686504,CC603166727J00131482512," + "CZ548654280J00100542611,CC300255137J00144014409,CC300255137J00132117209,CC300255137J00138524909,CC300255137J00140970009,CC283895481J00455369101,CC120866805J00362456101,CC300255137J00157899809,CC160036910J00188783004,CC522483588J00075973607,CC160036910J90250460000,CC348894235J00212669608,CC810042100J00193324409,CC827765150J00134118204,CC347464613J90250296000,CCL1219714590J00338390004,CC300255137J00139222309,CC871514410J00304941602,CCL1200214270J00302686504,CC603166727J00131482512," + "CZ548654280J00100542611,CC300255137J00144014409,CC300255137J00132117209,CC300255137J00138524909,CC300255137J00140970009,CC283895481J00455369101,CC120866805J00362456101,CC300255137J00157899809,CC160036910J00188783004,CC522483588J00075973607,CC160036910J90250460000,CC348894235J00212669608,CC810042100J00193324409,CC827765150J00134118204,CC347464613J90250296000,CCL1219714590J00338390004,CC300255137J00139222309,CC871514410J00304941602,CCL1200214270J00302686504,CC603166727J00131482512").split(",");

    //    final static String[] jobs = ("CZ548654280J00100542611,CC300255137J00144014409,CC300255137J00132117209,CC300255137J00138524909,CC300255137J00140970009,CC283895481J00455369101,CC120866805J00362456101,CC300255137J00157899809,CC160036910J00188783004,CC522483588J00075973607,CC160036910J90250460000,CC348894235J00212669608,CC810042100J00193324409,CC827765150J00134118204,CC347464613J90250296000,CCL1219714590J00338390004,CC300255137J00139222309,CC871514410J00304941602,CCL1200214270J00302686504,CC603166727J00131482512"+"CZ548654280J00100542611,CC300255137J00144014409,CC300255137J00132117209,CC300255137J00138524909,CC300255137J00140970009,CC283895481J00455369101,CC120866805J00362456101,CC300255137J00157899809,CC160036910J00188783004,CC522483588J00075973607,CC160036910J90250460000,CC348894235J00212669608,CC810042100J00193324409,CC827765150J00134118204,CC347464613J90250296000,CCL1219714590J00338390004,CC300255137J00139222309,CC871514410J00304941602,CCL1200214270J00302686504,CC603166727J00131482512"+"CZ548654280J00100542611,CC300255137J00144014409,CC300255137J00132117209,CC300255137J00138524909,CC300255137J00140970009,CC283895481J00455369101,CC120866805J00362456101,CC300255137J00157899809,CC160036910J00188783004,CC522483588J00075973607,CC160036910J90250460000,CC348894235J00212669608,CC810042100J00193324409,CC827765150J00134118204,CC347464613J90250296000,CCL1219714590J00338390004,CC300255137J00139222309,CC871514410J00304941602,CCL1200214270J00302686504,CC603166727J00131482512").split(",");
//    final static String[] jobs = "CZ548654280J00100542611,CC300255137J00144014409,CC300255137J00132117209,CC300255137J00138524909,CC300255137J00140970009,CC283895481J00455369101,CC120866805J00362456101,CC300255137J00157899809,CC160036910J00188783004,CC522483588J00075973607,CC160036910J90250460000,CC348894235J00212669608,CC810042100J00193324409,CC827765150J00134118204,CC347464613J90250296000,CCL1219714590J00338390004,CC300255137J00139222309,CC871514410J00304941602,CCL1200214270J00302686504,CC603166727J00131482512".split(",");
    private static void httpclient() throws InterruptedException {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        // 创建Get请求
        System.out.println("");
        // 响应模型
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("总开始时间：" + System.currentTimeMillis());

        for (int i = 0; i < 1000; i++) {
            final int ii = i;
            if ((ii + 1) % 6 == 0) {
                Thread.sleep(1000);
                System.out.println("1秒并发10个请求");
            }
            executorService.submit(new Runnable() {
                public void run() {
                    long startT = System.currentTimeMillis();
//                        long startT = System.currentTimeMillis();
//                         HttpGet httpGet = new HttpGet("https://capi.zhaopin.com/capi/position/detail?number="+jobs[ii]);
                    HttpGet httpGet = new HttpGet("https://capi.zhaopin.com/capi/position/search?component=true&pageIndex=" + ii);

                    CloseableHttpResponse response = null;
                    // 由客户端执行(发送)Get请求
                    try {
                        response = httpClient.execute(httpGet);

                        // 从响应模型中获取响应实体
                        HttpEntity responseEntity = response.getEntity();
//                            System.out.println("任务"+ii+"响应状态为:" + response.getStatusLine());
                        if (responseEntity != null) {
//                                System.out.println("任务"+ii+"响应内容长度为:" + responseEntity.getContentLength());
                            String json = EntityUtils.toString(responseEntity);
                            String taskId = DeserializeTest.findJosnValue("taskId", json);
//                                System.out.println("任务"+ii+":"+taskId);
//                System.out.println("响应内容为:\n" + json);
//                                System.out.println("任务"+ii+"共耗时：" + (System.currentTimeMillis() - startT));
                            System.out.println("任务" + ii + "-线程名" + Thread.currentThread().getName() + ",响应码：" + response.getStatusLine() + "字段taskId=" + taskId + ",执行时间：" + (System.currentTimeMillis() - startT) + "毫秒");
                        }
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            // 释放资源
                               /* if (httpClient != null) {
                                    httpClient.close();
                                }*/
                            if (response != null) {
                                response.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


        }

    }

    public static void main(String[] args) throws InterruptedException {
        httpclient();
        /*ExecutorService executor = Executors.newFixedThreadPool(6);
        for (int i = 0; i < 6; i++) {
            executor.submit(new Runnable() {
                public void run() {

                }
            });
        }*/
    }
}
