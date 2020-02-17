package com.cs.wujiuqi.data.crawler.core.api;

import java.util.Map;

/**
 * 车间或计划
 */
public interface Plant{
    /**
     * 注入上游的可迭代器(可能多个)
     * @param upIterators
     */
    void setUpIterators(Map<String,StoppableIterator> upIterators);

    /**
     * 导出当前产物迭代器给下游(可能多个)
     * @return
     */
     Map<String,StoppableIterator> getDownIterators();

    /**
     * 设置qps控制
     * @param flowControllers
     */
    void setFlowControllers(Map<String,FlowController> flowControllers);

    /**
     * 启动车间
     */
    void stop();

    /**
     * 停止车间
     */
    void start();
}
