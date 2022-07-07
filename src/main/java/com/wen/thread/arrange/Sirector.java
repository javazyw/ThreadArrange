package com.wen.thread.arrange;

import java.util.concurrent.ExecutorService;

/**
 *
 * Following is a simple example:
 * <pre>
 * ExecutorService executor = Executors.newFixedThreadPool(4);
 * Sirector sirector = new Sirector(executor);
 * HandlerA a = new HandlerA();
 * HandlerB b = new HandlerB();
 * HandlerC c = new HandlerC();
 * sirector.begin(a, b);
 * sirector.after(a,b).then(c);
 * sirector.ready();
 * Event e = new Event();
 * Event result = sirector.publish(e);
 *
 * class Event{
 * 	   ....
 * }
 *
 * class HandlerA implement EventHandler<Event>{
 *
 * }
 *
 * class handlerB implement EventHandler<event>{
 *
 * }
 *
 * </pre>
 */
public class Sirector<Event> {

    private final ExecutorService executorService;

    private final Script<Event> script;

    private ScriptRuntimeBuilder<Event> builder;

    private volatile boolean ready = false;

    public Sirector(ExecutorService executorService) {
        this.executorService = executorService;
        this.script = new Script<Event>();
    }

    public EventHandlerGroup<Event> begin(EventHandler<Event>... eventHandlers) {
        return script.start(eventHandlers);
    }

    public EventHandlerGroup<Event> after(EventHandler<Event>... eventHandlers) {
        return script.after(eventHandlers);
    }

    public Event publish(Event event, long timeout) {
        if (!ready) {
            throw new IllegalStateException("sirector not started.");
        }
        return builder.build(event, timeout).run();
    }

    public Event publish(Event event) {
        return publish(event, (long) 0);
    }

    public void publish(Event event, Callback<Event> callback) {
        if (!ready) {
            throw new IllegalStateException("sirector not started.");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback can not be null");
        }
        builder.build(event, 0, callback).run();
    }

    public boolean isReady() {
        return ready;
    }

    public synchronized void ready() {
        if (!ready) {
            script.ready();
            builder = new ScriptRuntimeBuilder<Event>(script, executorService);
            ready = true;
        }
    }
}
