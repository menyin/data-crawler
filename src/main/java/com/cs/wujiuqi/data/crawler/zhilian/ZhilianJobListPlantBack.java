package com.cs.wujiuqi.data.crawler.zhilian;

import com.cs.wujiuqi.data.crawler.core.api.FlowController;
import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.part.AbstractMultiPlantBack;
import com.cs.wujiuqi.data.crawler.core.part.GlobalFlowController;
import com.cs.wujiuqi.data.crawler.core.part.StringAbstractBlockStoppableIterator;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Component
public class ZhilianJobListPlantBack extends AbstractMultiPlantBack implements Consumer {
    public final static String CHAIN_KEY_JOB= "CHAIN_KEY_JOB";
    public final static String CHAIN_KEY_COM= "CHAIN_KEY_COM";
    public final static String CHAIN_KEY_JOB_LIST= "CHAIN_KEY_JOB_LIST";
    private LinkedBlockingQueue<String> downBlockingQueue4Com = new LinkedBlockingQueue<String>();//下游队列无capacity，即添加不限，获取受限。
    private LinkedBlockingQueue<String> downBlockingQueue4Job = new LinkedBlockingQueue<String>();//下游队列无capacity，即添加不限，获取受限。

    @Autowired
    private HttpClient httpClient;

    /**
     * 初始化
     */
    public ZhilianJobListPlantBack() {

        //初始化上游迭代器到当前实例
        Map<String, StoppableIterator> upIterators=new HashMap<>();
//        upIterators.put(CHAIN_KEY_JOB_LIST,new IntegerStoppableIterator(100));
//        upIterators.put(CHAIN_KEY_JOB_LIST,new IntegerBlockStoppableIterator(10));
        LinkedBlockingQueue queue=new LinkedBlockingQueue<String>();
        for (int i = 0; i <100 ; i++) {
            try {
                queue.put(String.valueOf(i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        StoppableIterator iterator=new StringAbstractBlockStoppableIterator(queue);
        upIterators.put(CHAIN_KEY_JOB_LIST,new StringAbstractBlockStoppableIterator(queue));
        this.setUpIterators(upIterators);
        /*new Thread(()->{
            try {
                Thread.sleep(10000);
                System.out.println("来啦~ 停止吧");
                iterator.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"T-stop").start();*/

        //初始化当前消费者
        Map consumers=new HashMap<String,Object>();
        consumers.put(CHAIN_KEY_JOB_LIST,this);
        this.setConsumers(consumers);

        //设置当前消费者的流控
        Map<String, FlowController> flowControllers=new HashMap<String,FlowController>();
        flowControllers.put(CHAIN_KEY_JOB_LIST, new GlobalFlowController(100));
        this.setFlowControllers(flowControllers);

        //初始化下游迭代器
        Map downIterators=new HashMap<String,Object>();
        downIterators.put(CHAIN_KEY_JOB,new StringAbstractBlockStoppableIterator(downBlockingQueue4Job));
        downIterators.put(CHAIN_KEY_COM,new StringAbstractBlockStoppableIterator(downBlockingQueue4Com));
        this.setDownIterators(downIterators);
    }

    /**
     * 迭代运行的代码
     * @param pageIndex 上游迭代器传下来的页码
     */
    public void accept(Object pageIndex) {
        System.out.println(Thread.currentThread().getName()+"--pageIndex="+pageIndex);
        try {
            System.out.println("任务线程执行任务需要1s....");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*long startT = System.currentTimeMillis();
        HttpGet httpGet = new HttpGet("https://capi.zhaopin.com/capi/position/search?component=true&pageIndex=" + pageIndex);
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
                companyNumbers.stream().forEach(number->{
                    try {
                        downBlockingQueue4Job.put(number);
                    } catch (InterruptedException e) {
                        System.out.println(this.getClass()+"职位项添加失败 companyNumber="+number);
                    }
                });
                System.out.println("页码："+pageIndex+"，任务【" + taskId + "】-线程名" + Thread.currentThread().getName() + ",响应码：" + response.getStatusLine() + ",执行时间：" + (System.currentTimeMillis() - startT) + "毫秒");
//                System.out.println(""+companyNumbers+jobNumbers);
            }else{
                System.out.println("页码："+pageIndex+",response is null!!!");
            }
        } catch (Exception e) {
            System.out.println("记录日志到日志系统 taskId:"+taskId+",异常信息："+e.getMessage());
        }*/
    }


}

