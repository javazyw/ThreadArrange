package com.wen.thread.arrange;


public interface EventHandler<Event> {

    /**
     * handler处理类
     * @param event
     */
    public void onEvent(Event event);

}
