目前解决多线程编排问题
下图中的EH1，EH2，EH3，EH4，EH5，EH6分别表示六种不同的事件处理器，下图表示六个事件处理器之间比较复杂的依赖关系：
                 +-----+       +-----+ 
        +------> | EH2 |-----> | EH3 |-------+
        |        +-----+     +-----+         |
        |                                    v
     +-----+                             +-----+
     | EH1 |                             | EH6 |
     +-----+                             +-----+
        |                                     |
        |        +-----+       +-----+        |
        +------> | EH4 |-----> | EH5 |--------+
                 +-----+       +-----+
Sirector相应的事务编排代码如下：
sirector.begin(eh1).then(eh2, eh4);
sirector.after(eh2).then(eh3);
sirector.after(eh4).then(eh5);
sirector.after(eh3, eh5).then(eh6);
sirector.ready();
sirector.publish(event);