package com.cs.wujiuqi.data.crawler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * *经过测试西刺网的大部分开放ip都不可用或请求时间过长或不稳定，除非付费。
 * *可以买几台云服务器，然后搭建代理的web服务
 * xici 可用ip 14.20.235.46	808
               124.205.155.152  9090
 */
public class IpProxyTest {
    public static void main(String[] args) {
        httpclient();
//        httpGetRequest();

    }

    private static void httpclient(){
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        System.out.println("");
        HttpGet httpGet = new HttpGet("https://capi.zhaopin.com/capi/position/detail?number=CC712564680J00291329305");
//        HttpGet httpGet = new HttpGet("https://capi.zhaopin.com/capi/position/search?component=true");
        //代理IP设置，代理 ip查询地址：https://www.xicidaili.com/
        HttpHost httpHost = new HttpHost("124.205.155.152",9090);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(60000)//设置连接超时时间,单位毫秒
                .setSocketTimeout(60000)//设置读取超时时间,单位毫秒
                .setProxy(httpHost)//设置代理
                .build();
        httpGet.setConfig(requestConfig);
        long startT = System.currentTimeMillis();

        // 响应模型
        CloseableHttpResponse response = null;
        try {

            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);

            System.out.println("httpClient#execute共耗时：" + (System.currentTimeMillis() - startT));

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                String respStr = EntityUtils.toString(responseEntity);
                System.out.println("响应内容为:\n" + respStr);


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
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("共耗时：" + (System.currentTimeMillis() - startT));
    }
    private static void httpGetRequest(){
        long startT = System.currentTimeMillis();
        System.out.println(httpGetRequest("https://capi.zhaopin.com/capi/position/detail?number=CC712564680J00291329305"));
        System.out.println("共耗时：" + (System.currentTimeMillis() - startT));

    }
    public static String httpGetRequest(String urlString1) {

        String result = "";
        BufferedReader in = null;

        try {

            URL realUrl = new URL(urlString1);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {

            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;

    }

}
