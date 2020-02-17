package com.cs.wujiuqi.data.crawler.core.common;

import com.cs.wujiuqi.data.crawler.core.api.Event;

public class RetryEvent implements Event {
    private String chainKey;
    private String treadName;
    private String message;
    private Long retryWaitMinute;


    public RetryEvent(String chainKey) {
        this(chainKey,null,null,null);
    }
    public RetryEvent(String chainKey, Long retryWaitMinute) {
        this(chainKey,Long.valueOf(0),null,null);
    }
    public RetryEvent(String chainKey, Long retryWaitMinute,String treadName) {
        this(chainKey,Long.valueOf(0),treadName,null);
    }
    public RetryEvent(String chainKey, Long retryWaitMinute,String treadName, String message) {
        this.chainKey = chainKey;
        this.treadName = treadName;
        this.message = message;
        this.retryWaitMinute = retryWaitMinute;
    }

    public String getChainKey() {
        return chainKey;
    }

    public void setChainKey(String chainKey) {
        this.chainKey = chainKey;
    }

    public String getTreadName() {
        return treadName;
    }

    public void setTreadName(String treadName) {
        this.treadName = treadName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getRetryWaitMinute() {
        return retryWaitMinute;
    }

    public void setRetryWaitMinute(Long retryWaitMinute) {
        this.retryWaitMinute = retryWaitMinute;
    }
}
