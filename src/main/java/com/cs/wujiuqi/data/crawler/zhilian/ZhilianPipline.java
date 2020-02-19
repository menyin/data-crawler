package com.cs.wujiuqi.data.crawler.zhilian;

import com.cs.wujiuqi.data.crawler.core.api.Pipline;
import com.cs.wujiuqi.data.crawler.core.api.StoppableIterator;
import com.cs.wujiuqi.data.crawler.core.common.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.cs.wujiuqi.data.crawler.zhilian.ZhilianJobListPlantBack.CHAIN_KEY_JOB_LIST;

@Component
public class ZhilianPipline implements Pipline {
    @Autowired
    private ZhilianJobListPlant zhilianJobListPlant;
//    @Qualifier("pageFilterZhilianJobListUpIterator")
    @Resource(name="pageFilterZhilianJobListUpIterator")
    private StoppableIterator<String> topIterator;
    @Autowired
    private ZhilianJobPlant zhilianJobPlant;

    @Override
    public void start() {
        Map<String,StoppableIterator> upIterators=new HashMap<>();
        upIterators.put(CHAIN_KEY_JOB_LIST, topIterator);
        zhilianJobListPlant.setUpIterators(upIterators);
        zhilianJobPlant.setUpIterators(zhilianJobListPlant.getDownIterators());
        zhilianJobListPlant.start();
        zhilianJobPlant.start();
        Logs.CONSOLE.info("pieline-{} start.",this.getClass().getName());
    }

    @Override
    public void stop() {
        zhilianJobListPlant.stop();
        zhilianJobPlant.stop();
        Logs.CONSOLE.info("pieline-{} stop.",this.getClass().getName());
    }
}
