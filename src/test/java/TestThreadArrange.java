import com.wen.thread.arrange.Callback;
import com.wen.thread.arrange.EventHandler;
import com.wen.thread.arrange.Sirector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestThreadArrange {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Sirector<HelloWorldEvent> sirector = new Sirector<TestThreadArrange.HelloWorldEvent>(
                executorService);
        HelloWorldEventHanlder onceHandler = new HelloWorldEventHanlder(100);
        HelloWorldEventHanlder twiceHandler = new HelloWorldEventHanlder(200);
        HelloWorldEventHanlder threeHandler = new HelloWorldEventHanlder(300);
        HelloWorldEventHanlder fourTimesHandler = new HelloWorldEventHanlder(400);
        //编排时间处理器
        sirector.begin(onceHandler).then(twiceHandler);
        sirector.after(onceHandler).then(threeHandler);
        sirector.after(twiceHandler, threeHandler).then(fourTimesHandler);
        sirector.ready();
        //同步发布时间
        HelloWorldEvent event = sirector.publish(new HelloWorldEvent(), 10);
       // System.out.println("hello world are called" + event.callCount + "times");//
        //Callback<HelloWorldEvent> alertCallback = new AlertCallback();
        //异步发布时间
        // sirector.publish(new HelloWorldEvent(),alertCallback);
    }

    private static class HelloWorldEvent {
        private int callCount;

        public void increaseCallCount() {
            callCount++;
        }

        public int getCallCount() {
            return callCount;
        }
    }

    private static class HelloWorldEventHanlder implements EventHandler<HelloWorldEvent> {
        private final int times;

        public HelloWorldEventHanlder(int times) {
            this.times = times;
        }

        @Override
        public void onEvent(HelloWorldEvent helloWorldEvent) {
            for (int i = 0; i < times; i++) {
                helloWorldEvent.increaseCallCount();
            }

        }
    }

    static class AlertCallback implements Callback<HelloWorldEvent> {

        @Override
        public void onSuccess(HelloWorldEvent helloWorldEvent) {
            System.out.println("hello world are called" + helloWorldEvent.callCount + "times");
        }

        @Override
        public void onError(HelloWorldEvent helloWorldEvent, Throwable throwable) {
            //异常处理
        }
    }
}
