package com.cs.wujiuqi.data.crawler.zhilian;

import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.common.JsonUtil;
import com.cs.wujiuqi.data.crawler.core.common.RetryEvent;
import com.cs.wujiuqi.data.crawler.core.exception.IteratorStopException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 智联顶层迭代器，循环执行所有条件组合，并且在产生
 * 注：因为我们条件中，大部分在128以内，地区数据量在300+，所以索引不用byte存储，而改用String存储
 * 注：condition+pageIndex，有大部分页面的pageIndex到不了50，所以检测pageIndex的有效性是当前迭代器的主要任务
 * 注：每次pageIndex之前测试下总共有几条数据
 * 注：笛卡尔积耗内存问题
 */
@Component
public class PageFilterZhilianJobListUpIterator extends ZhilianJobListUpIterator {
    private static String CHECK_PAGE_INDEX_LIMIT = "https://capi.zhaopin.com/capi/position/search?component=true&pageIndex=1";
    private int currPageIndex = 0;
    private int pageIndexLimit = 50;//用浮点减少转化
    private int pageSizeLimit = 20;
    private String currDefectiveResult;
    @Autowired
    private HttpClient httpClient;

    /**
     * 这里也验证了synchronize是可重入锁：next()递归调用，并未发生死锁
     * 笛卡尔积实际测试在1ms以内,即微妙级别，
     *
     * @return
     * @throws IteratorStopException
     */
    @Override
    public String next() throws IteratorStopException {
        synchronized (this.getClass()) {
            ++currPageIndex;
            if (currPageIndex == 1) {
                System.out.println("页码为1时，开始检测页码限制.......");
                //http请求检测当前条件下总记录数 getPageIndexLimit();
//                double numTotal=Double.valueOf("numTotal,假如是125");//TODO 带着currResult去请求
                String numTotalStr = getPageIndexLimit();
                double numTotal;
                numTotal = numTotalStr == null ? 1000 : Double.valueOf(numTotalStr);
                if (numTotal != pageIndexLimit * pageSizeLimit) {
                    pageIndexLimit = (int) Math.ceil(numTotal / (double) pageSizeLimit);
                }
            }
            //页码检测
            if (currPageIndex > pageIndexLimit) {
                currPageIndex = 0;
                currDefectiveResult = null;
                return next();
            }
            if(currDefectiveResult == null){
//                long startT = System.currentTimeMillis();
                currDefectiveResult = super.next();//暂存
//                System.out.println("Carteion use time:" + (System.currentTimeMillis() - startT) + "ms");
            }
            return currDefectiveResult + "&pageIndex=" + currPageIndex;
        }
    }

    private String getPageIndexLimit() {

        long startT = System.currentTimeMillis();
        HttpGet httpGet = new HttpGet(CHECK_PAGE_INDEX_LIMIT);
        HttpResponse response = null;
        // 由客户端执行(发送)Get请求
        String taskId = null;
        String numTotal = null;
        try {
            response = httpClient.execute(httpGet);

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String json = EntityUtils.toString(responseEntity);
//                System.out.println(json);
                taskId = JsonUtil.findJosnValue(json, "taskId");
                numTotal = JsonUtil.findJosnValue(json, "numTotal");
            } else {
//                System.out.println("页码："+pageIndex+",response is null!!!");
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("记录日志到日志系统 taskId:" + taskId + ",异常信息：" + e.getMessage());
            //throw new Exception("超过时长，抛出异常给父类，通知流控器线程，线程暂停10分钟后再试（再循环）");

        }
        return numTotal;
    }

    /**
     * 测试迭代器
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        try {
            StoppableIterator si = new PageFilterZhilianJobListUpIterator();
            while (true) {
                System.out.println(si.next());
                Thread.sleep(500);
            }
        } catch (IteratorStopException e) {
            e.printStackTrace();
        }
    }

}
