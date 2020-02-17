package com.cs.wujiuqi.data.crawler.flowController;

public final class OverFlowException extends RuntimeException {

    private boolean overMaxLimit = false;

    public OverFlowException() {
        super("累计中请求数超过最大限制maxLimit", null, false, false);
    }

    public OverFlowException(boolean overMaxLimit) {
        super(null, null, false, false);
        this.overMaxLimit = overMaxLimit;
    }

    public OverFlowException(String message) {
        super(message, null, false, false);
    }

    public boolean isOverMaxLimit() {
        return overMaxLimit;
    }
}
