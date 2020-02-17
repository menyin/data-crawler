package com.cs.wujiuqi.data.crawler.core.exception;

public class IteratorStopException extends Exception{
    public IteratorStopException() {
        super("["+Thread.currentThread().getName()+"] iterator is stoped.");
    }
}
