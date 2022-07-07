package com.wen.thread.arrange;

import java.util.*;

class Script<Event> {
    private boolean ready = false;

    private final IdentityHashMap<EventHandler<Event>, List<EventHandler<Event>>> denpendedHandlers = new IdentityHashMap<EventHandler<Event>, List<EventHandler<Event>>>();

    synchronized EventHandlerGroup<Event> after(
            final EventHandler<Event>... handlers) {
        if (ready) {
            throw new IllegalStateException(
                    "script is ready, cannot be edit any more.");
        }
        for (EventHandler<Event> handler : handlers) {
            if (!denpendedHandlers.containsKey(handler)) {
                throw new IllegalStateException(
                        "event handler is not in script yet.");
            }
        }
        return start(handlers);
    }

    synchronized EventHandlerGroup<Event> start(
            final EventHandler<Event>... handlers) {
        if (ready) {
            throw new IllegalStateException(
                    "script is ready, cannot be edit any more.");
        }
        EventHandlerGroup<Event> clips = new EventHandlerGroup<Event>(this,
                Arrays.asList(handlers));
        return clips;
    }

    synchronized Map<EventHandler<Event>, List<EventHandler<Event>>> getdenpendedEventHandlers() {
        return denpendedHandlers;
    }

    synchronized void ready() {
        ready = true;
    }

    synchronized boolean isReady() {
        return ready;
    }

    void addDependency(EventHandler<Event> from, EventHandler<Event> to) {
        if (!denpendedHandlers.containsKey(from)) {
            denpendedHandlers.put(from, new ArrayList<EventHandler<Event>>(0));
        }
        if (to != null && !denpendedHandlers.get(from).contains(to)) {
            denpendedHandlers.get(from).add(to);
        }
    }
}
