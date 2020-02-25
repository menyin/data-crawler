package com.cs.wujiuqi.data.crawler;

import com.cs.wujiuqi.data.crawler.core.api.Pipline;
import com.cs.wujiuqi.data.crawler.core.common.Logs;
import com.cs.wujiuqi.data.crawler.core.common.StaticObject;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Map;
import static com.cs.wujiuqi.data.crawler.core.common.StaticObject.AP;

public class Bootstrap {



    public static void main(String[] args) throws InterruptedException{


        /*Map<String,Pipline> piplines=AP.getBeansOfType(Pipline.class);
        for (Map.Entry<String,Pipline> piplineEntry:piplines.entrySet()){
            piplineEntry.getValue().start();
            System.out.println(piplineEntry.getKey()+"- 启动");
        }*/

//        System.out.println(System.getProperties());
//        Logs.CONSOLE.debug("进入到main方法");
//        Logs.HTTP.debug("执行http请求");
//        Logs.SQL.debug("执行http请求");
//
//        Thread.sleep(Integer.MAX_VALUE);

    }


}





































