package com.cs.wujiuqi.data.crawler;

import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.exception.IteratorStopException;
import com.cs.wujiuqi.data.crawler.zhilian.PageFilterZhilianJobListUpIterator;
import com.cs.wujiuqi.data.crawler.zhilian.ZhilianJobListPlant;
import com.cs.wujiuqi.data.crawler.zhilian.ZhilianJobPlant;
import org.apache.http.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.cs.wujiuqi.data.crawler.zhilian.ZhilianJobListPlantBack.CHAIN_KEY_JOB_LIST;

public class Bootstrap {
    private final static ApplicationContext applicationContext;

    static {
        applicationContext = new ClassPathXmlApplicationContext("spring-context.xml");
    }


    public static void main(String[] args) throws InterruptedException, java.text.ParseException {

        /*ZhilianJobListPlant zhilianJobListPlant = (ZhilianJobListPlant) applicationContext.getBean("zhilianJobListPlant");
        ZhilianJobPlant zhilianJobPlant = (ZhilianJobPlant) applicationContext.getBean("zhilianJobPlant");
        zhilianJobPlant.setUpIterators(zhilianJobListPlant.getDownIterators());
        zhilianJobListPlant.start();
        zhilianJobPlant.start();
        Thread.sleep(Integer.MAX_VALUE);*/


        ZhilianJobListPlant zhilianJobListPlant = (ZhilianJobListPlant) applicationContext.getBean("zhilianJobListPlant");
        ZhilianJobPlant zhilianJobPlant = (ZhilianJobPlant) applicationContext.getBean("zhilianJobPlant");
        StoppableIterator topIterator = (PageFilterZhilianJobListUpIterator) applicationContext.getBean("pageFilterZhilianJobListUpIterator");

        Map<String,StoppableIterator> upIterators=new HashMap<>();
        upIterators.put(CHAIN_KEY_JOB_LIST, topIterator);
        zhilianJobListPlant.setUpIterators(upIterators);

        zhilianJobPlant.setUpIterators(zhilianJobListPlant.getDownIterators());
        zhilianJobListPlant.start();
        zhilianJobPlant.start();
        Thread.sleep(Integer.MAX_VALUE);

        /*try {
            StoppableIterator si = (PageFilterZhilianJobListUpIterator) applicationContext.getBean("pageFilterZhilianJobListUpIterator");
            while (true) {
                System.out.println(si.next());
                Thread.sleep(500);
            }
        } catch (IteratorStopException e) {
            e.printStackTrace();
        }*/


//        Lock lock = new ReentrantLock();

        /*long startT=System.currentTimeMillis();
        condition.await(flowController.getDelay(),TimeUnit.NANOSECONDS);//时间是纳秒,JDK自带阻塞无法实现纳秒级别阻塞
        long endT=System.currentTimeMillis();
        Lock lock = new ReentrantLock();*/


        /*Integer[] ccc=new Integer[]{2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,42,44,46,48,50,52,58,163,159,165,157,161,167,169,171,155,173,151,175,153,177,179,181,149,183,147,185,187,145,189,191,193,195,197,199,75,137,139,141,143,73,71,67,59,61,55,57,53,65,39,47,51,45,35,43,33,37,1,29,3,31,13,9,15,7,21,25,19,23,27,17,11,5,49,63,135,41,69,127,123,133,131,125,129,77,121,119,117,115,113,79,111,109,107,105,81,103,101,97,83,89,85,99,54,91,95,64,56,93,87,178,146,150,152,144,148,158,142,156,154,176,140,182,170,164,138,174,162,172,160,136,166,200,180,168,186,132,188,184,134,128,126,124,130,192,190,120,196,118,198,114,112,122,194,108,106,104,110,100,98,116,102,94,90,88,96,84,82,92,80,86,76,70,78,74,72,66,60,62,68};
        HashSet set=new HashSet(Arrays.asList(ccc));
        System.out.println(set.size());*/
    }


}





































