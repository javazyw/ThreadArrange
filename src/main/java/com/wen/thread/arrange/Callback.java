package com.wen.thread.arrange;


public interface Callback<Event> {
    /**
     * 当数据处理成功的时候回调
     * @param event
     */
    public void onSuccess(Event event);

    /**
     * 当数据出现异常的时候回调
     * @param event
     * @param throwable
     */
    public void onError(Event event, Throwable throwable);

}
