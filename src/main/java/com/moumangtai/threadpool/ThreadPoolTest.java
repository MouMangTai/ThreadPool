package com.moumangtai.threadpool;


import java.util.concurrent.TimeUnit;

/**
 * 线程池测试类
 */
public class ThreadPoolTest {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(2,5,1000, TimeUnit.MILLISECONDS,(tasks,task)->{
            // 1. 死等
//            tasks.push(task);
            // 2. 超时等待
//            tasks.push(task,20,TimeUnit.MILLISECONDS);
            // 3. 抛弃(什么都不做）
            // 4. 抛出异常
//            throw new RuntimeException("队列已满");
            // 5. 让主线程自己执行
//            task.run();
        });

        for (int i = 0; i < 10; i++) {
            int j = i;
            threadPool.execute(()->{
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("执行任务"+j);
            });
        }
    }
}
