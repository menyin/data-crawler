package com.cs.wujiuqi.data.crawler.zhilian;

import com.cs.wujiuqi.data.crawler.core.api.FlowController;
import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.common.JsonUtil;
import com.cs.wujiuqi.data.crawler.core.common.RetryEvent;
import com.cs.wujiuqi.data.crawler.core.part.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * test 100页、qps=4 、t=100 => total time=25013ms
 * test 100页、qps=5 、t=100 => total time=20027ms 偶尔报错
 * test 100页、qps=5 、t=200 => total time=20010ms
 * test 100页、qps=5 、t=cached => total time=20242ms 和不使用缓存差不多
 */
@Component
public class ZhilianJobListPlant extends AbstractMultiPlant implements Consumer {
    public final static String CHAIN_KEY_JOB= "CHAIN_KEY_JOB";
    public final static String CHAIN_KEY_COM= "CHAIN_KEY_COM";
    private final static String CHAIN_KEY_JOB_LIST= "CHAIN_KEY_JOB_LIST";
    private LinkedBlockingQueue<String> downBlockingQueue4Com; //下游队列无capacity，即添加不限，获取受限。
    private LinkedBlockingQueue<String> downBlockingQueue4Job;//下游队列无capacity，即添加不限，获取受限。

    @Autowired
    private HttpClient httpClient;


    /**
     * 初始化
     */
    public ZhilianJobListPlant() {
        /*new Thread(()->{
            try {
                Thread.sleep(10000);
                System.out.println("来啦~ 停止吧");
                iterator.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"T-stop").start();*/

    }
    @Override
    protected Map<String, StoppableIterator> initUpIterators() {
        //初始化上游迭代器到当前实例
        Map<String, StoppableIterator> upIterators=new HashMap<>();
        upIterators.put(CHAIN_KEY_JOB_LIST,new IntegerStoppableIterator(50));
//        upIterators.put(CHAIN_KEY_JOB_LIST,new IntegerBlockStoppableIterator(100));
        /*LinkedBlockingQueue queue=new LinkedBlockingQueue<String>();
        for (int i = 0; i <100 ; i++) {
            try {
                queue.put(String.valueOf(i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        StoppableIterator iterator=new StringBlockStoppableIterator(queue);
        upIterators.put(CHAIN_KEY_JOB_LIST,new StringBlockStoppableIterator(queue));*/
        return upIterators;
    }
    @Override
    protected Map<String, FlowController> initFlowControllers() {
        //设置当前消费者的流控
        Map<String, FlowController> flowControllers=new HashMap<String,FlowController>();
        flowControllers.put(CHAIN_KEY_JOB_LIST, new GlobalFlowController(1));
        return flowControllers;
    }
    @Override
    protected Map<String, Consumer> initConsumers() {
        //初始化当前消费者
        Map consumers=new HashMap<String,Object>();
        consumers.put(CHAIN_KEY_JOB_LIST,this);
        return consumers;
    }
    @Override
    public Map<String, StoppableIterator> initDownIterators() {
        //初始化下游迭代器
        Map downIterators=new HashMap<String,Object>();
        downBlockingQueue4Job= new LinkedBlockingQueue<String>();
        downBlockingQueue4Com= new LinkedBlockingQueue<String>();
        downIterators.put(CHAIN_KEY_JOB,new StringAbstractBlockStoppableIterator(downBlockingQueue4Job));
        downIterators.put(CHAIN_KEY_COM,new StringAbstractBlockStoppableIterator(downBlockingQueue4Com));
        return downIterators;
    }
    /**
     * 迭代运行的代码
     * @param requestUrl 上游迭代器传下来的带条件url
     */
    public void accept(Object requestUrl){
//        System.out.println(Thread.currentThread().getName()+"--pageIndex="+pageIndex);
        System.out.println(System.currentTimeMillis()+"--requestUrl="+requestUrl);
        /*
        if (pageIndex.toString().equals("10")){//当到10页时，出现错误要求重试
            EventBus.post(new RetryEvent(CHAIN_KEY_JOB_LIST,Long.valueOf(3)));
            System.out.println("当到10页时，出现错误要求重试");
        }*/
       /* try {
            System.out.println("任务线程执行任务需要1s....");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        long startT = System.currentTimeMillis();
        HttpGet httpGet = new HttpGet(requestUrl.toString());
        HttpResponse response = null;
        // 由客户端执行(发送)Get请求
        String taskId="";
        try {
            response = httpClient.execute(httpGet);

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String json = EntityUtils.toString(responseEntity);
//                System.out.println(json);
                taskId=JsonUtil.findJosnValue(json, "taskId");
                Set<String> companyNumbers = JsonUtil.findNoRepeatJosnValues(json,"companyNumber");
                companyNumbers.stream().forEach(companyNumber->{
                    try {
                        downBlockingQueue4Com.put(companyNumber);
                    } catch (InterruptedException e) {
                        System.out.println(this.getClass()+"公司项添加失败 companyNumber="+companyNumber);
                    }
                });
                Set<String> jobNumbers = JsonUtil.findNoRepeatJosnValues(json,"number");
                jobNumbers.stream().forEach(number->{
                    try {
                        downBlockingQueue4Job.put(number);
                    } catch (InterruptedException e) {
                        System.out.println(this.getClass()+"职位项添加失败 companyNumber="+number);
                    }
                });
//                System.out.println("页码："+pageIndex+"，任务【" + taskId + "】-线程名" + Thread.currentThread().getName() + ",响应码：" + response.getStatusLine() + ",执行时间：" + (System.currentTimeMillis() - startT) + "毫秒");
//                System.out.println(""+companyNumbers+jobNumbers);
            }else{
                System.out.println("请求列表页url："+requestUrl+",response is null!!!");
            }
        } catch (Exception e) {
//            e.printStackTrace();
            triggerRetry(new RetryEvent(CHAIN_KEY_JOB_LIST));//用事件总线通知线程10分钟后重试 ps:重试时间可以自定
            System.out.println("记录日志到日志系统 taskId:"+taskId+",requestUrl:"+requestUrl+",异常信息："+e.getMessage());
            //throw new Exception("超过时长，抛出异常给父类，通知流控器线程，线程暂停10分钟后再试（再循环）");
        }
    }



}

