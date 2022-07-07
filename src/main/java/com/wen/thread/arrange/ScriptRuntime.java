package com.wen.thread.arrange;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ScriptRuntime<Event> {

    private final Event t;

    private final ExecutorService executorService;

    private final CountDownLatch completeLatch;

    private final Map<EventHandler<Event>, EventProcess<Event>> processMap;

    private final Callback<Event> callback;

    private final long timeout;

    private volatile Throwable error;

    private final Lock lock = new ReentrantLock();

    private volatile boolean canceled = false;

    private final ArrayList<Future<?>> futures;

    private final static ArrayList<Future<?>> EmptyFutures = new ArrayList<Future<?>>(
            0);

    ScriptRuntime(Event t, ExecutorService executorService,
                  Map<EventHandler<Event>, EventProcess<Event>> processMap,
                  Callback<Event> callback, long timeout) {
        this.t = t;
        this.executorService = executorService;
        this.processMap = processMap;
        this.callback = callback;
        if (callback == null) {
            completeLatch = new CountDownLatch(1);
        } else {
            completeLatch = null;
        }
        this.timeout = timeout;
        if (this.timeout > 0) {
            futures = new ArrayList<Future<?>>(1);
        } else {
            futures = EmptyFutures;
        }
    }

    Event run() {
        ArrayList<EventProcess<Event>> processesWithNoDependencies = getProcessedWithNoDependencies();
        for (EventProcess<Event> process : processesWithNoDependencies) {
            startProcess(process);
        }
        waitIfNecessary();
        return t;
    }

    private ArrayList<EventProcess<Event>> getProcessedWithNoDependencies() {
        ArrayList<EventProcess<Event>> processesWithNoDependencies = new ArrayList<EventProcess<Event>>(1);
        for (EventProcess<Event> process : processMap.values()) {
            if (!process.hasUnsatisfiedDependcies()) {
                processesWithNoDependencies.add(process);
            }
        }
        return processesWithNoDependencies;
    }

    void markAsCompleted() {
        if (callback == null) {
            completeLatch.countDown();
        } else {
            callback.onSuccess(t);
        }
    }

    void markAsError(Throwable error) {
        if (callback == null) {
            this.error = error;
            completeLatch.countDown();
        } else {
            callback.onError(t, error);
        }
    }

    private void cancel() {
        lock.lock();
        try {
            canceled = true;
            for (Future<?> future : futures) {
                future.cancel(true);
            }
        } finally {
            lock.unlock();
        }

    }

    private void waitIfNecessary() {
        if (callback == null) { //
            try {
                if (timeout > 0) {
                    if (!completeLatch.await(timeout, TimeUnit.MILLISECONDS)) {
                        cancel();
                        throw new TimeoutException();
                    }
                } else {
                    completeLatch.await();
                }

                if (error != null) {
                    throw new SirectorException(error);
                }
            } catch (InterruptedException e) {
                throw new SirectorException(e);
            }
        }
    }

    Callback<Event> getCallback() {
        return callback;
    }

    EventProcess<Event> getProcess(EventHandler<Event> eventHandler) {
        return processMap.get(eventHandler);
    }

    void startProcess(EventProcess<Event> process) {
        if (timeout > 0) {
            lock.lock();
            try {
                if (!canceled) {
                    futures.add(executorService.submit(process));
                }
            } finally {
                lock.unlock();
            }
        } else {
            executorService.submit(process);
        }
    }
}
