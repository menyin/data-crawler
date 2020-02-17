package com.cs.wujiuqi.data.crawler.zhilian;

import com.cs.wujiuqi.data.crawler.core.api.FlowController;
import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.common.JsonUtil;
import com.cs.wujiuqi.data.crawler.core.common.RetryEvent;
import com.cs.wujiuqi.data.crawler.core.part.AbstractMultiPlant;
import com.cs.wujiuqi.data.crawler.core.part.GlobalFlowController;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cs.wujiuqi.data.crawler.core.common.JdbcUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.cs.wujiuqi.data.crawler.zhilian.ZhilianJobListPlant.CHAIN_KEY_JOB;


@Component
public class ZhilianJobPlant extends AbstractMultiPlant implements Consumer {

    private static String URI = "https://capi.zhaopin.com/capi/position/detail?number=";
    private static String SQL_INSERT = "insert into zhaopin_job(_id,cid,jname,workCity,data,modTime,caijiTime,id) values(?,?,?,?,?,?,?,?)";
    private static String SQL_UPDATE = "update zhaopin_job set _id=?,cid=?,jname=?,workCity=?,data=?,modTime=?,caijiTime=? where id=?";
    private static String SQL_CONDITIONS = "SELECT (SELECT count(*) from zhaopin_job WHERE id=?) as isNotExist, (SELECT count(*) from zhaopin_job WHERE id=? and modTime!=?) as isDoUpdate;";
    private Map<String, StoppableIterator> upIterators;




    @Autowired
    private HttpClient httpClient;

    /**
     * 初始化
     */
    public ZhilianJobPlant() {
    }

    @Override
    public Map<String, StoppableIterator> initDownIterators() {
        return null;
    }

    @Override
    protected Map<String, StoppableIterator> initUpIterators() {
        return null;
    }

    @Override
    protected Map<String, FlowController> initFlowControllers() {
        //设置当前消费者的流控
        Map<String, FlowController> flowControllers = new HashMap<String, FlowController>();
        flowControllers.put(CHAIN_KEY_JOB, new GlobalFlowController(4));
        return flowControllers;
    }

    @Override
    protected Map<String, Consumer> initConsumers() {
        //初始化当前消费者
        Map consumers = new HashMap<String, Object>();
        consumers.put(CHAIN_KEY_JOB, this);
        return consumers;
    }

    /**
     * 迭代运行的代码
     *
     * @param jobNumber 上游迭代器传下来的jobNumber
     */
    public void accept(Object jobNumber) {
//        System.out.println("jobNumber");
        long startT = System.currentTimeMillis();
        HttpGet httpGet = new HttpGet(URI + jobNumber);
        HttpResponse response = null;
        String taskId = "";
        try {
            response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String json = EntityUtils.toString(responseEntity);
                //System.out.println(json);
                //String taskId = JsonUtil.findJosnValue("taskId", json);
                String jobId = JsonUtil.findJosnValue(json, "jobId");//职位id
                String number = JsonUtil.findJosnValue(json, "number");//加密的职位id
                String companyId = JsonUtil.findJosnValue(json, "companyId");//公司id
                String name = JsonUtil.findJosnValue(json, "name");//职位名称
                String workCity = JsonUtil.findJosnValue(json, "workCity");//工作城市
                //String cityCode = JsonUtil.findJosnValue("cityCode", json);//工作城市编码，和cityId一样
                String publishTime = JsonUtil.findJosnValue(json, "publishTime");//职位修改时间
                //String testNull = findJosnValue("testNull", json);//正则没法匹配null的字段 但是智联的数据如果为空则会赋值""
                //System.out.println("jobId:"+jobId+",number:"+number+",companyId:"+companyId+",name:"+name+",workCity:"+workCity+",publishTime:"+publishTime);
                //System.out.println("jobNumber：" + jobNumber + "，任务【" + taskId + "】-线程名" + Thread.currentThread().getName() + ",响应码：" + response.getStatusLine() + ",执行时间：" + (System.currentTimeMillis() - startT) + "毫秒");
                //System.out.println(""+companyNumbers+jobNumbers);
                addJob2DB(jobId, number, companyId, name, workCity,json, publishTime);
            } else {
                System.out.println("jobNumber：" + jobNumber + ",response is null!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            triggerRetry(new RetryEvent(CHAIN_KEY_JOB));//用事件总线通知线程10分钟后重试 ps:重试时间可以自定
            System.out.println("记录日志到日志系统 taskId:" + taskId + ",jobNumber:" + jobNumber + ",异常信息：" + e.getMessage());
            //throw new Exception("超过时长，抛出异常给父类，通知流控器线程，线程暂停10分钟后再试（再循环）");

        }
    }


    public static void addJob2DB(String jobId, String number, String companyId, String name, String workCity,String json, String publishTime) throws SQLException {
//        long startT = System.currentTimeMillis();
        QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
//        long publishTimeL= new Date(publishTime.replaceAll("-","/")).getTime();
        publishTime = String.valueOf(new Date(publishTime.replaceAll("-", "/")).getTime()).substring(0, 10);
        String caijiTime = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        Object[] paramsConditions = new Object[]{jobId, jobId, publishTime};
        Object[] paramsEdit = new Object[]{ number, companyId, name,workCity,json, publishTime,caijiTime,jobId};
        boolean[] conditions = qr.query(SQL_CONDITIONS, new ResultSetHandler<boolean[]>() {
            public boolean[] handle(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    boolean[] conditions = new boolean[2];
                    conditions[0] = rs.getInt("isNotExist") == 0;
                    conditions[1] = rs.getInt("isDoUpdate") > 0;
                    return conditions;
                }
                return null;
            }
        }, paramsConditions);

        if (conditions[0]) {//isNotExist
            int update = qr.update(SQL_INSERT, paramsEdit);
        } else if (conditions[1]) {//isDoUpdate
            int update = qr.update(SQL_UPDATE, paramsEdit);
        }
//        System.out.println("共耗时：" + (System.currentTimeMillis() - startT));
//        System.out.println("影响行数："+updates);

    }


}

