package com.wen.thread.arrange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EventHandlerGroup<Event> {

    private final Script<Event> script;

    private List<EventHandler<Event>> eventHandlers;

    EventHandlerGroup(Script<Event> script,
                      List<EventHandler<Event>> eventHandlers) {
        synchronized (script) {
            if (script.isReady()) {
                throw new IllegalStateException(
                        "script is ready, cannot be edit any more.");
            }
            this.script = script;
            this.eventHandlers = new ArrayList<EventHandler<Event>>(
                    eventHandlers.size());
            copyList(eventHandlers, this.eventHandlers);
            for (EventHandler<Event> handler : eventHandlers) {
                script.addDependency(handler, null);
            }
        }
    }
    public EventHandlerGroup<Event> then(EventHandler<Event>... eventHandlers) {
        synchronized (script) {
            if (script.isReady()) {
                throw new IllegalStateException(
                        "script is ready, cannot be edit any more.");
            }
            for (EventHandler<Event> from : this.eventHandlers) {
                for (EventHandler<Event> to : eventHandlers) {
                    script.addDependency(from, to);
                }
            }
            for (EventHandler<Event> to : eventHandlers) {
                script.addDependency(to, null);
            }
            this.eventHandlers = new ArrayList<EventHandler<Event>>(
                    eventHandlers.length);
            copyList(Arrays.asList(eventHandlers), this.eventHandlers);
            return this;
        }
    }

    private void copyList(List<EventHandler<Event>> src,
                          List<EventHandler<Event>> dest) {
        for (EventHandler<Event> s : src) {
            dest.add(s);
        }
    }
}
